package com.wardrobe.armoire.model.api.gpt

import android.util.Log
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
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.shop.ShopModel
import com.wardrobe.armoire.model.user.UserModel
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

    suspend fun getCustomPromptRecommendation(
        user: UserModel,
        shopItems: List<ShopModel>,
        customPrompt: String
    ): String {
        Log.d("ApiConfig", "Called getCustomPromptRecommendation | user.styles=${user.style}, itemCount=${shopItems.size}, prompt=$customPrompt")
        val prompt = """
            My preferred styles are: ${user.style?.joinToString(", ") ?: "None"}
            
            Here are available shop items:
            ${
            shopItems.joinToString("\n") {
                "UID: ${it.uid}, Name: ${it.name}, Style: ${it.style}, Description: ${it.description}, Price: ${it.price}"
            }
        }

            $customPrompt

            Return JSON like:
            {
              "recommended_shop_uids": ["uid1", "uid2"]
            }
        """.trimIndent()

        return try {
            requestOpenAI(prompt)
        } catch (e: Exception) {
            Log.e("ApiConfig", "Error in getCustomPromptRecommendation", e)
            "{}"
        }
    }

    suspend fun getShopRecommendation(user: UserModel, shopItems: List<ShopModel>): String {
        Log.d("ApiConfig", "Called getShopRecommendation | user.styles=${user.style}, itemCount=${shopItems.size}")
        val prompt = """
            My preferred styles are: ${user.style?.joinToString(", ") ?: "None"}

            Here are available shop items:
            ${
            shopItems.joinToString("\n") {
                "UID: ${it.uid}, Name: ${it.name}, Style: ${it.style}, Description: ${it.description}, Price: ${it.price}"
            }
        }

            Recommend which items I should buy based on my style. Return JSON:
            {
              "recommended_shop_uids": ["uid1", "uid2"]
            }
        """.trimIndent()

        return try {
            requestOpenAI(prompt)
        } catch (e: Exception) {
            Log.e("ApiConfig", "Error in getShopRecommendation", e)
            "{}"
        }
    }

    suspend fun getOutfitRecommendation(user: UserModel, outfitItems: List<OutfitModel>): String {
        Log.d("ApiConfig", "Called getOutfitRecommendation | user.styles=${user.style}, outfitCount=${outfitItems.size}")
        val prompt = """
            My preferred styles are: ${user.style?.joinToString(", ") ?: "None"}

            Here are my outfits:
            ${
            outfitItems.joinToString("\n") {
                "UID: ${it.uid}, Style: ${it.style}, Description: ${it.description}, Status: ${it.status}"
            }
        }

            Recommend which outfits I should wear based on my style. Return JSON:
            {
              "recommended_outfit_uids": ["outfit1", "outfit2"]
            }
        """.trimIndent()

        return try {
            requestOpenAI(prompt)
        } catch (e: Exception) {
            Log.e("ApiConfig", "Error in getOutfitRecommendation", e)
            "{}"
        }
    }

    private suspend fun requestOpenAI(prompt: String): String {
        Log.d("ApiConfig", "Calling OpenAI with prompt length=${prompt.length}")
        val request = ChatCompletionRequest(
            model = ModelId("gpt-4-1106-preview"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = """
                        You are a helpful assistant specializing in fashion and design.
                        You must respond with only valid JSON. Do not include any text or explanation outside the JSON.
                        If you cannot respond in valid JSON, respond with an empty JSON: {}.
                    """.trimIndent()
                ),
                ChatMessage(ChatRole.User, prompt)
            )
        )
        val completion: ChatCompletion = openai.chatCompletion(request)
        val rawResponse = completion.choices.firstOrNull()?.message?.content ?: "{}"
        Log.d("ApiConfig", "Received response: $rawResponse")
        return extractJson(rawResponse)
    }

    private fun extractJson(content: String): String {
        val jsonStart = content.indexOfFirst { it == '{' }
        val jsonEnd = content.lastIndexOf('}' )
        val result = if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            content.substring(jsonStart, jsonEnd + 1)
        } else {
            "{}"
        }
        Log.d("ApiConfig", "Extracted JSON: $result")
        return result
    }
}


