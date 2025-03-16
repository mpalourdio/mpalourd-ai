/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mpalourdio.projects.mpalourd_ai.controller

import com.mpalourdio.projects.mpalourd_ai.config.AiConfigurationProperties
import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.http.MediaType
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.io.File

@RestController
@RequestMapping("/api/openai")
class OpenAiController(
    chatClientBuilder: ChatClient.Builder,
    aiConfigurationProperties: AiConfigurationProperties,
    private val openAiChatProperties: OpenAiChatProperties,
    private val session: HttpSession
) {
    private final val customDefaultSystem: String =
        File(aiConfigurationProperties.defaultSystemFilePath).readText(Charsets.UTF_8)

    private final val messageChatMemoryAdvisor = MessageChatMemoryAdvisor(InMemoryChatMemory())

    private final val customChatClient = chatClientBuilder.clone()
        .defaultSystem(customDefaultSystem)
        .defaultAdvisors(messageChatMemoryAdvisor)
        .build()

    private final val boringChatClient = chatClientBuilder.clone()
        .defaultSystem("You are the mpalourdio corp. chatbot")
        .defaultAdvisors(messageChatMemoryAdvisor)
        .build()

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        log.info("Custom default system: \n${this.customDefaultSystem}")
        log.info("Chat Model in use: ${this.openAiChatProperties.options.model}")
    }

    @GetMapping("/csrf")
    fun csrf(csrfToken: CsrfToken): CsrfToken {
        return csrfToken;
    }

    @PostMapping("/chat", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun chat(@RequestBody chatRequestBody: ChatRequestBody): Flux<String> {
        log.info("Prompt (${chatRequestBody.isCustom}): ${chatRequestBody.prompt}, for session ${session.id}")

        val chatClient = when (chatRequestBody.isCustom) {
            true -> customChatClient
            false -> boringChatClient
        }

        return chatClient.prompt(chatRequestBody.modelType.formatting + chatRequestBody.prompt)
            .options(
                ChatOptions.builder()
                    .model(chatRequestBody.modelType.name)
                    .temperature(chatRequestBody.modelType.temperature)
                    .build()
            )
            .advisors { a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, session.id) }
            .stream()
            .content()
    }
}
