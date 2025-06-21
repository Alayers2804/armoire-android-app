package com.wardrobe.armoire.ui.outfit

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.BuildConfig
import com.wardrobe.armoire.model.api.gpt.DalleRequest
import com.wardrobe.armoire.model.api.gpt.GptVisionHelper
import com.wardrobe.armoire.model.api.gpt.RetrofitInstance
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.outfit.OutfitService
import com.wardrobe.armoire.model.outfit.PromptBuilder
import com.wardrobe.armoire.model.wardrobe.CategoryItem
import com.wardrobe.armoire.model.wardrobe.WardrobeCategory
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import com.wardrobe.armoire.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.files.FileNotFoundException
import java.io.ByteArrayOutputStream
import java.io.File

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
                val wardrobe = outfitDao.getOutfitByStatus(status)
                when (status) {
                    "my_outfit" -> _outfitMyOutfits.postValue(wardrobe.toList())
                    "my_saved" -> _outfitMySaved.postValue(wardrobe.toList())
                    else -> Log.w("OutfitViewModel", "Unknown status: $status")
                }
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Error fetching outfit for status: $status", e)
            }
        }
    }

    fun observeWardrobeByStatus(status: String) {
        viewModelScope.launch {
            outfitDao.observeOutfitByStatus(status).collect { outfit ->
                when (status) {
                    "my_outfit" -> _outfitMyOutfits.postValue(outfit)
                    "my_saved" -> _outfitMySaved.postValue(outfit)
                }
            }
        }
    }

    fun createOutfitFromWardrobes(
        context: Context,
        wardrobes: List<WardrobeModel>,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64Images = ImageUtils.convertWardrobesToBase64(context, wardrobes)

                if (base64Images.isEmpty()) {
                    withContext(Dispatchers.Main) { onResult(false, "No valid images") }
                    return@launch
                }

                val visionPrompt = PromptBuilder.buildVisionPrompt()
                val gptRequest = GptVisionHelper().buildVisionRequest(visionPrompt, base64Images)
                val gptResponse = OutfitService.fetchGptResponse(gptRequest) ?: run {
                    withContext(Dispatchers.Main) { onResult(false, "Failed to get GPT response") }
                    return@launch
                }

                val outfitDetails = OutfitService.parseOutfitDetails(gptResponse)
                val dalleResponse = OutfitService.fetchDalleImage(outfitDetails.prompt) ?: run {
                    withContext(Dispatchers.Main) { onResult(false, "Failed to get DALL·E image") }
                    return@launch
                }

                val imageFile = ImageUtils.downloadAndSaveImage(context, dalleResponse)

                val newOutfit = OutfitModel(
                    path = imageFile.absolutePath,
                    description = outfitDetails.description,
                    style = outfitDetails.style,
                    status = "my_outfit"
                )

                outfitDao.insert(newOutfit)

                val updatedList = outfitDao.getOutfitByStatus("my_outfit").toMutableList().apply {
                    add(newOutfit)
                }

                withContext(Dispatchers.Main) {
                    _outfitMyOutfits.value = updatedList
                    onResult(true, null)
                }

            } catch (e: Exception) {
                Log.e("OutfitAPI", "Unexpected error: ${e.message}", e)
                withContext(Dispatchers.Main) { onResult(false, e.message) }
            }
        }
    }

    fun deleteOutfit(outfit: OutfitModel) {
        viewModelScope.launch(Dispatchers.IO) {
            outfitDao.delete(outfit)
            val updatedList = outfitDao.getOutfitByStatus("my_outfit").toMutableList().apply {
                remove(outfit)
            }
            _outfitMyOutfits.postValue(updatedList)
        }
    }

}