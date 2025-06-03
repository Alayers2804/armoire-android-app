package com.wardrobe.armoire.model.api.gpt

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.Chat
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.wardrobe.armoire.BuildConfig
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds


object ApiConfig {

    private val openai = OpenAI(
        token = BuildConfig.OPENAI_API_KEY,
        timeout = Timeout(socket = 60.seconds),
        logging = LoggingConfig(LogLevel.All)
    )

    private fun chatCompletionRequest(context: String): ChatCompletionRequest {
        return ChatCompletionRequest(
            model = ModelId("gpt-4-1106-preview"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful assistant specializing in fashion and design. Always give the best fashion recommendations based on user style input."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "Hi, I would like a recommendation based on my preferred styles: $context"
                )
            )
        )
    }

    suspend fun getModelResponse(context: String): String {
        val request = chatCompletionRequest(context)
        val completion: ChatCompletion = openai.chatCompletion(request)
        return completion.choices.firstOrNull()?.message?.content ?: "No response from model."
    }
}