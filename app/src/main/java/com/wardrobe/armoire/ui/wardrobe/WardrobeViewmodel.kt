package com.wardrobe.armoire.ui.wardrobe

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.wardrobe.CategoryItem
import com.wardrobe.armoire.model.wardrobe.WardrobeCategory
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WardrobeViewmodel(application: Application): AndroidViewModel(application) {

    private val _wardrobeMyItems = MutableLiveData<List<WardrobeModel>>()
    val wardrobeMyitems: LiveData<List<WardrobeModel>> = _wardrobeMyItems

    private val _wardrobeMyPreloved = MutableLiveData<List<WardrobeModel>>()
    val wardrobeMyPreloved: LiveData<List<WardrobeModel>> = _wardrobeMyPreloved

    val categoriesLiveData = MediatorLiveData<List<WardrobeCategory>>()

    private val wardrobeDao = AppDatabase.getDatabase(application).wardrobeDao()

    init {
        categoriesLiveData.addSource(wardrobeMyitems) { updateCategories() }
        categoriesLiveData.addSource(wardrobeMyPreloved) { updateCategories() }
    }

    private fun updateCategories() {
        val items = wardrobeMyitems.value?.map { CategoryItem.Wardrobe(it) } ?: emptyList()
        val saved = wardrobeMyPreloved.value?.map { CategoryItem.Wardrobe(it) } ?: emptyList()

        val categories = listOf(
            WardrobeCategory("My Items", items),
            WardrobeCategory("My Preloved", saved)
        )
        categoriesLiveData.value = categories
    }

    fun fetchWardrobeByStatus(status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wardrobe = wardrobeDao.getWardrobeByStatus(status)
                when (status) {
                    "my_item" -> _wardrobeMyItems.postValue(wardrobe)
                    "my_preloved" -> _wardrobeMyPreloved.postValue(wardrobe)
                    else -> Log.w("WardrobeViewModel", "Unknown status: $status")
                }
            } catch (e: Exception) {
                Log.e("WardrobeViewModel", "Error fetching wardrobe for status: $status", e)
            }
        }
    }

    fun observeWardrobeByStatus(status: String) {
        viewModelScope.launch {
            wardrobeDao.observeWardrobeByStatus(status).collect { wardrobe ->
                when (status) {
                    "my_item" -> _wardrobeMyItems.postValue(wardrobe)
                    "my_preloved" -> _wardrobeMyPreloved.postValue(wardrobe)
                }
            }
        }
    }

    fun insertWardrobeItem(item: WardrobeModel) {
        viewModelScope.launch(Dispatchers.IO) {
            wardrobeDao.insert(item)
            fetchWardrobeByStatus("my_item") // Refresh list
        }
    }
}
