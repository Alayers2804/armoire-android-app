package com.wardrobe.armoire.model.api.gpt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.shop.ShopModel
import com.wardrobe.armoire.model.user.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendationViewmodel(application: Application) : AndroidViewModel(application) {

    private val shopDao = AppDatabase.getDatabase(application).shopDao()
    private val outfitDao = AppDatabase.getDatabase(application).outfitDao()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _recommendedShopItems = MutableLiveData<List<ShopModel>>()
    val recommendedShopItems: LiveData<List<ShopModel>> = _recommendedShopItems

    private val _recommendedOutfits = MutableLiveData<List<OutfitModel>>()
    val recommendedOutfits: LiveData<List<OutfitModel>> = _recommendedOutfits

        fun fetchShopRecommendations(userId: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val user = userDao.getUserById(userId) ?: return@launch
                val shopItems = shopDao.getAllShopItems()

                val response = ApiConfig.getShopRecommendation(user, shopItems)
                val recommended = Gson().fromJson(response, ShopRecommendation::class.java)
                val filtered = shopItems.filter { recommended.recommended_shop_uids.contains(it.uid) }

                _recommendedShopItems.postValue(filtered)
            }
        }

    fun fetchOutfitRecommendations(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@launch
            val outfits = outfitDao.getAllWardrobe()

            val response = ApiConfig.getOutfitRecommendation(user, outfits)
            val recommended = Gson().fromJson(response, OutfitRecommendation::class.java)
            val filtered = outfits.filter { recommended.recommended_outfit_uids.contains(it.uid) }

            _recommendedOutfits.postValue(filtered)
        }
    }

    fun fetchCustomPromptRecommendations(userId: String, prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@launch
            val shopItems = shopDao.getAllShopItems()

            val response = ApiConfig.getCustomPromptRecommendation(user, shopItems, prompt)
            val recommended = Gson().fromJson(response, ShopRecommendation::class.java)
            val filtered = shopItems.filter { recommended.recommended_shop_uids.contains(it.uid) }

            _recommendedShopItems.postValue(filtered)
        }
    }

}
