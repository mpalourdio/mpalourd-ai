/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.mpalourd_ai.controller.anthropic

import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatProperties
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/anthropic")
class AnthropicController(
    anthropicChatClientBuilder: ChatClient.Builder,
    chatMemoryRepository: JdbcChatMemoryRepository,
    anthropicChatProperties: AnthropicChatProperties,
    private val session: HttpSession,
    private val anthropicReactiveChatProcessorHandler: AnthropicReactiveChatProcessorHandler,
) {

    private final val messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(
        MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .build())
        // TODO: Should not be necessary to set this
        // But, it fails in native image with "Caused by: java.lang.IllegalArgumentException: scheduler cannot be null"
        .scheduler(MessageChatMemoryAdvisor.DEFAULT_SCHEDULER)
        .build()

    private final val chatClient = anthropicChatClientBuilder
        .defaultSystem("Ensure you strictly answer less or equal to ${anthropicChatProperties.options.maxTokens} characters.")
        .defaultAdvisors(
            SimpleLoggerAdvisor(),
            messageChatMemoryAdvisor
        )
        .build()

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostMapping("/chat", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chat(@RequestBody chatRequestBody: ChatRequestBody): Flux<AnthropicChatLightResponse> {
        log.info(
            "Prompt (${chatRequestBody.isCustom}): ${chatRequestBody.prompt}, for session ${session.id}. " +
                    "Using model: ${chatRequestBody.modelType.name}, temperature: ${chatRequestBody.modelType.temperature}"
        )
        return anthropicReactiveChatProcessorHandler.streamResponse(chatClient,  chatRequestBody.prompt, chatRequestBody, session)
    }
}
