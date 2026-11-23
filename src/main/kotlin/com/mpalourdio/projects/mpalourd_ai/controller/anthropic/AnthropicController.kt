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
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatProperties
import org.springframework.ai.session.SessionService
import org.springframework.ai.session.advisor.SessionAdvisors
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
    anthropicChatProperties: AnthropicChatProperties,
    private val session: HttpSession,
    private val sessionService: SessionService,
    private val anthropicReactiveChatProcessorHandler: AnthropicReactiveChatProcessorHandler,
) {
    private final val chatClient = anthropicChatClientBuilder
        .defaultSystem("Ensure you strictly answer less or equal to ${anthropicChatProperties.maxTokens} characters.")
        .defaultAdvisors {
            it
                .advisors(SimpleLoggerAdvisor())
                .advisors(SessionAdvisors.builder(sessionService).build())
        }
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
        return anthropicReactiveChatProcessorHandler.streamResponse(
            chatClient,
            chatRequestBody.prompt,
            chatRequestBody,
            session
        )
    }
}
