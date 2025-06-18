package com.wardrobe.armoire.model.api.gpt

import android.content.Context
import android.net.Uri
import android.util.Base64

class GptVisionHelper {

    fun buildVisionRequest(promptText: String, base64Images: List<String>): GptVisionRequest {
        val contentList = mutableListOf<Content>()

        contentList.add(Content(type = "text", text = promptText))

        base64Images.forEach {
            contentList.add(
                Content(
                    type = "image_url",
                    image_url = ImageUrl(url = "data:image/jpeg;base64,$it")
                )
            )
        }

        return GptVisionRequest(
            model = "gpt-4o",
            messages = listOf(
                Message(content = contentList)
            )
        )
    }

    fun convertImageToBase64(imageUri: Uri, context: Context): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes() ?: return ""
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

}