/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.mpalourd_ai.controller.openai

import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import jakarta.servlet.http.HttpSession
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class OpenAiReactiveChatProcessorHandler {

    fun streamResponse(
        chatClient: ChatClient,
        prompt: String,
        chatRequestBody: ChatRequestBody,
        session: HttpSession
    ): Flux<ChatLightResponse> {
        return chatClient.prompt(prompt)
            .options(
                ChatOptions.builder()
                    .model(chatRequestBody.modelType.name)
                    .temperature(chatRequestBody.modelType.temperature)
                    .build()
            )
            .advisors { a -> a.param(CONVERSATION_ID, session.id) }
            .stream()
            .chatResponse()
            .filter { c -> c.result?.metadata?.finishReason != "STOP" }
            .map { c -> ChatLightResponse(c.result?.output?.text!!) }
    }
}

data class ChatLightResponse(val text: String)
