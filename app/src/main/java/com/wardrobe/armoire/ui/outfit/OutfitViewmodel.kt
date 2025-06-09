package com.wardrobe.armoire.ui.outfit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.wardrobe.CategoryItem
import com.wardrobe.armoire.model.wardrobe.WardrobeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OutfitViewmodel(application: Application) : AndroidViewModel(application) {

    private val _outfitMyOutfits = MutableLiveData<List<OutfitModel>>()
    val outfitMyOutfits: LiveData<List<OutfitModel>> = _outfitMyOutfits

    private val _outfitMySaved = MutableLiveData<List<OutfitModel>>()
    val outfitMySaved: LiveData<List<OutfitModel>> = _outfitMySaved

    val categoriesLiveData = MediatorLiveData<List<WardrobeCategory>>()

    private val outfitDao = AppDatabase.getDatabase(application).outfitDao()

    init {
        categoriesLiveData.addSource(outfitMyOutfits) { updateCategories() }
        categoriesLiveData.addSource(outfitMySaved) { updateCategories() }
    }

    private fun updateCategories() {
        val outfits = outfitMyOutfits.value?.map { CategoryItem.Outfit(it) } ?: emptyList()
        val saved = outfitMySaved.value?.map { CategoryItem.Outfit(it) } ?: emptyList()

        val categories = listOf(
            WardrobeCategory("My Outfits", outfits),
            WardrobeCategory("My Saved Outfits", saved)
        )
        categoriesLiveData.value = categories
    }

    fun fetchOutfitByStatus(status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wardrobe = outfitDao.getWardrobeByStatus(status)
                when (status) {
                    "my_outfit" -> _outfitMyOutfits.postValue(wardrobe)
                    "my_saved" -> _outfitMySaved.postValue(wardrobe)
                    else -> Log.w("OutfitViewModel", "Unknown status: $status")
                }
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Error fetching outfit for status: $status", e)
            }
        }
    }
}