package com.wardrobe.armoire.model.api.gpt

data class FashionRecommendationResult(
    val recommended_shop_uids: List<String>,
    val recommended_outfit_uids: List<String>,
    val wardrobe_suggestion: List<WardrobeDay>
)

data class WardrobeDay(
    val day: String,
    val shop_uid: String
)

data class ShopRecommendation(val recommended_shop_uids: List<String>)
data class OutfitRecommendation(val recommended_outfit_uids: List<String>)