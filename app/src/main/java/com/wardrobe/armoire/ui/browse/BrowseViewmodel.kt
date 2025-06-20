package com.wardrobe.armoire.ui.browse

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.outfit.OutfitModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowseViewmodel(application: Application) : AndroidViewModel(application) {

    private val _outfitBrowser = MutableLiveData<List<OutfitModel>>()
    val outfitBrowser: LiveData<List<OutfitModel>> = _outfitBrowser

    private val outfitDao = AppDatabase.getDatabase(application).outfitDao()

    fun getWardrobeByStyle(style: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wardrobe = outfitDao.getOutfitByStyle(style)
                _outfitBrowser.postValue(wardrobe)
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Error fetching outfit for status: $style", e)
            }
        }
    }

    fun getAllWardrobe(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wardrobe = outfitDao.getAllOutfit()
                _outfitBrowser.postValue(wardrobe)
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Error fetching outfit", e)
            }
        }
    }
}