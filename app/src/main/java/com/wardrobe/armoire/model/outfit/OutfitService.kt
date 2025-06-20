package com.wardrobe.armoire.model.outfit

import android.util.Base64
import com.wardrobe.armoire.model.api.gpt.DalleRequest
import com.wardrobe.armoire.model.api.gpt.GptVisionHelper
import com.wardrobe.armoire.model.api.gpt.GptVisionRequest
import com.wardrobe.armoire.model.api.gpt.RetrofitInstance

object OutfitService {

    private val allowedStyles = listOf(
        "Old Money", "Streetwear", "Softboy", "Starboy", "Minimalism", "Vintage",
        "Stargirl", "Indie", "Cottagecore", "Scandi", "Downtown", "Alt", "Y2K"
    )

    data class OutfitDetails(val prompt: String, val description: String, val style: String)

    fun fetchGptResponse(request: GptVisionRequest): String? {
        val response = RetrofitInstance.api.getVisionRecommendation(request).execute()
        return if (response.isSuccessful) response.body()?.choices?.firstOrNull()?.message?.content else null
    }

    fun parseOutfitDetails(rawResponse: String): OutfitDetails {
        val parsed = rawResponse.lines()
            .mapNotNull {
                val index = it.indexOf(":")
                if (index != -1) {
                    val key = it.substring(0, index).trim().lowercase()
                    val value = it.substring(index + 1).trim()
                    key to value
                } else null
            }.toMap()

        val dallePrompt = parsed["dalleprompt"] ?: parsed["dalle prompt"] ?: error("Missing DallePrompt")
        val description = parsed["description"] ?: "No description"
        val rawStyle = parsed["style"] ?: "Streetwear"

        val allowedStyles = listOf(
            "Old Money", "Streetwear", "Softboy", "Starboy", "Minimalism", "Vintage",
            "Stargirl", "Indie", "Cottagecore", "Scandi", "Downtown", "Alt", "Y2K"
        )
        val style = allowedStyles.find { it.equals(rawStyle, true) } ?: "Streetwear"

        return OutfitDetails(dallePrompt, description, style)
    }

    fun fetchDalleImage(prompt: String): String? {
        val dalleResponse = RetrofitInstance.api.createImage(DalleRequest(prompt = prompt)).execute()
        return dalleResponse.body()?.data?.firstOrNull()?.url
    }

    suspend fun verifyOutfitImageWithGpt(imageBytes: ByteArray, expectedDescription: String, expectedStyle: String): Boolean {
        val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        val prompt = """
            You are a fashion verification assistant. Based on the image provided, determine if the outfit matches the following:

            Expected Description: $expectedDescription
            Expected Style: $expectedStyle

            Respond ONLY with:
            Match: YES or NO
            Reason: <short explanation>
        """.trimIndent()

        val visionRequest = GptVisionHelper().buildVisionRequest(prompt, listOf(base64))
        val response = RetrofitInstance.api.getVisionRecommendation(visionRequest).execute()

        if (!response.isSuccessful || response.body() == null) return false

        val responseText = response.body()?.choices?.firstOrNull()?.message?.content ?: return false
        return responseText.contains("Match: YES", ignoreCase = true)
    }
}

object PromptBuilder {
    fun buildVisionPrompt(): String = """
        You are a fashion assistant that has capability to recommend what people should wear.
        Based on the images (a full wardrobe set: top, pants, bag, and shoes), generate:
        - A DALL·E prompt that describes the full outfit as a flat lay.
        - A short style description.
        - A fashion style from the exact list provided.

        ⚠️ IMPORTANT:
        Return only in this exact format, no extra text, no markdown:

        DallePrompt: <text>
        Description: <text>
        Style: <ONE from [Old Money, Streetwear, Softboy, Starboy, Minimalism, Vintage, Stargirl, Indie, Cottagecore, Scandi, Downtown, Alt, Y2K]>
    """.trimIndent()
}