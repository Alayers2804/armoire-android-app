package com.wardrobe.armoire.model.api.gpt

data class FashionRecommendationResult(
    val recommended_shop_uids: List<String> = emptyList(),
    val recommended_outfit_uids: List<String> = emptyList(),
    val wardrobe_suggestion: List<WardrobeDay> = emptyList()
)

data class WardrobeDay(
    val day: String,
    val shop_uid: String
)

data class ShopRecommendation(
    val recommended_shop_uids: List<String> = emptyList()
)
data class OutfitRecommendation(val recommended_outfit_uids: List<String>)