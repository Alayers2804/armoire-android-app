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
        val prompt = """
        My preferred styles are: ${user.style?.joinToString(", ") ?: "None"}

        Here are available shop items:
        ${shopItems.joinToString("\n") {
            "UID: ${it.uid}, Name: ${it.name}, Style: ${it.style}, Description: ${it.description}, Price: ${it.price}"
        }}

        $customPrompt

        Return JSON like:
        {
          "recommended_shop_uids": ["uid1", "uid2"]
        }
    """.trimIndent()

        return requestOpenAI(prompt)
    }


    suspend fun getShopRecommendation(user: UserModel, shopItems: List<ShopModel>): String {
        val prompt = """
            My preferred styles are: ${user.style?.joinToString(", ") ?: "None"}

            Here are available shop items:
            ${shopItems.joinToString("\n") {
            "UID: ${it.uid}, Name: ${it.name}, Style: ${it.style}, Description: ${it.description}, Price: ${it.price}"
        }}

            Recommend which items I should buy based on my style. Return JSON:
            {
              "recommended_shop_uids": ["uid1", "uid2"]
            }
        """.trimIndent()
        return requestOpenAI(prompt)
    }

    suspend fun getOutfitRecommendation(user: UserModel, outfitItems: List<OutfitModel>): String {
        val prompt = """
            My preferred styles are: ${user.style?.joinToString(", ") ?: "None"}

            Here are my outfits:
            ${outfitItems.joinToString("\n") {
            "UID: ${it.uid}, Style: ${it.style}, Description: ${it.description}, Status: ${it.status}"
        }}

            Recommend which outfits I should wear based on my style. Return JSON:
            {
              "recommended_outfit_uids": ["outfit1", "outfit2"]
            }
        """.trimIndent()
        return requestOpenAI(prompt)
    }

    private suspend fun requestOpenAI(prompt: String): String {
        val request = ChatCompletionRequest(
            model = ModelId("gpt-4-1106-preview"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful assistant specializing in fashion and design. Respond only in JSON."
                ),
                ChatMessage(ChatRole.User, prompt)
            )
        )
        val completion: ChatCompletion = openai.chatCompletion(request)
        return completion.choices.firstOrNull()?.message?.content ?: "{}"
    }
}

