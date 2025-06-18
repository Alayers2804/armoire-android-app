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
