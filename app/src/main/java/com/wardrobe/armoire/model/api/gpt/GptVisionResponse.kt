package com.wardrobe.armoire.model.api.gpt

data class GptVisionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String

)

data class GptFashionVisionResponse(
    val dallePrompt: String,
    val description: String,
    val style: String
)

data class DalleResponse(
    val created: Long,
    val data: List<DalleImageData>
)

data class DalleImageData(
    val url: String
)