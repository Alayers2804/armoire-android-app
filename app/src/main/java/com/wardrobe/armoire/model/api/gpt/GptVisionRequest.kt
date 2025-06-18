package com.wardrobe.armoire.model.api.gpt

data class GptVisionRequest(
    val model: String = "gpt-4o",
    val messages: List<Message>
)

data class Message(
    val role: String = "user",
    val content: List<Content>
)

data class Content(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String,
    val detail: String = "high"
)