package com.wardrobe.armoire.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.shop.ShopModel

class ShopViewmodel(application: Application) : AndroidViewModel(application)  {
    private val _shopAllItems = MutableLiveData<List<ShopModel>>()
    val shopAllitems: LiveData<List<ShopModel>> = _shopAllItems

    private val shopDao = AppDatabase.getDatabase(application).shopDao()


    fun getAllShopItems(){
        viewModelScope.launch(Dispatchers.IO) {
            val items = shopDao.getAllShopItems()
            _shopAllItems.postValue(items)
        }
    }

    fun getShopItemsSortedByPrice(ascending: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = if (ascending) {
                shopDao.getShopItemsByPriceAsc()
            } else {
                shopDao.getShopItemsByPriceDesc()
            }
            _shopAllItems.postValue(items)
        }
    }

    fun filterShopItems(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allItems = shopDao.getAllShopItems()
            val filtered = allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.style.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            _shopAllItems.postValue(filtered)
        }
    }

}