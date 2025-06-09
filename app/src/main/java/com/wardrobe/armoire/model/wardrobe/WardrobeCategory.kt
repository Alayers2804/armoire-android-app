package com.wardrobe.armoire.model.wardrobe

import com.wardrobe.armoire.model.outfit.OutfitModel

data class WardrobeCategory(
    val title: String,
    val items: List<CategoryItem>
)

sealed class CategoryItem {
    data class Wardrobe(val data: WardrobeModel) : CategoryItem()
    data class Outfit(val data: OutfitModel) : CategoryItem()
}