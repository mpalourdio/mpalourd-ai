package com.mpalourdio.projects.mpalourd_ai.controller

import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import jakarta.servlet.http.HttpSession
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ReactiveChatProcessorHandler {

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
            .advisors { a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, session.id) }
            .stream()
            .chatResponse()
            .filter { c -> c.result.metadata.finishReason != "STOP" }
            .map { c -> ChatLightResponse(c.result.output.text) }
    }
}

data class ChatLightResponse(val text: String)
