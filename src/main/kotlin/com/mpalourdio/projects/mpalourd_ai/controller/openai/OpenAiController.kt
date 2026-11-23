/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.mpalourd_ai.controller.openai

import com.mpalourdio.projects.mpalourd_ai.config.AiConfigurationProperties
import com.mpalourdio.projects.mpalourd_ai.external.ExternalApiHandler
import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.session.SessionService
import org.springframework.ai.session.advisor.SessionAdvisors
import org.springframework.ai.vectorstore.pgvector.PgVectorStore
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.io.File

@RestController
@RequestMapping("/api/openai")
class OpenAiController(
    openAiChatClientBuilder: ChatClient.Builder,
    aiConfigurationProperties: AiConfigurationProperties,
    vectorStore: PgVectorStore,
    private val sessionService: SessionService,
    private val session: HttpSession,
    private val openAiReactiveChatProcessorHandler: OpenAiReactiveChatProcessorHandler,
    private val externalApiHandler: ExternalApiHandler,
) {
    private final val customDefaultSystem: String =
        File(aiConfigurationProperties.defaultSystemFilePath).readText(Charsets.UTF_8)

    private final val customChatClient = openAiChatClientBuilder.clone()
        .defaultSystem(customDefaultSystem)
        .defaultAdvisors {
            it
                .advisors(SessionAdvisors.builder(sessionService).build())
                .advisors(SimpleLoggerAdvisor())
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
        }
        .build()

    private final val boringChatClient = openAiChatClientBuilder.clone()
        .defaultSystem("You are the mpalourdio corp. chatbot")
        .defaultAdvisors {
            it
                .advisors(SimpleLoggerAdvisor())
                .advisors(SessionAdvisors.builder(sessionService).build())
        }
        .build()

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        log.info("Custom default system: \n${this.customDefaultSystem}")
    }

    @GetMapping("/refresh", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun refresh(): String {
        this.externalApiHandler.refresh()
        return "OK"
    }

    @PostMapping("/chat", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chat(@RequestBody chatRequestBody: ChatRequestBody): Flux<ChatLightResponse> {
        val concatPrompt = chatRequestBody.modelType.formatting.orEmpty() + chatRequestBody.prompt

        log.info(
            "Prompt (${chatRequestBody.isCustom}): ${concatPrompt}, for session ${session.id}. " +
                    "Using model: ${chatRequestBody.modelType.name}, temperature: ${chatRequestBody.modelType.temperature}"
        )

        val chatClient = when (chatRequestBody.isCustom) {
            true -> customChatClient
            false -> boringChatClient
        }

        return openAiReactiveChatProcessorHandler.streamResponse(chatClient, concatPrompt, chatRequestBody, session)
    }
}
