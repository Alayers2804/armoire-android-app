package com.wardrobe.armoire.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.model.cart.CartDao
import com.wardrobe.armoire.model.cart.CartModel
import com.wardrobe.armoire.utils.Preferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CartViewmodel(
    private val cartDao: CartDao,
    private val preferences: Preferences
) : ViewModel() {

    val cartItems: LiveData<List<CartModel>> = preferences.getUserUid().flatMapLatest { uid ->
        cartDao.getCartItems(uid)
    }.asLiveData()

    fun insert(item: CartModel) = viewModelScope.launch {
        cartDao.insertCartItem(item)
    }

    fun update(item: CartModel) = viewModelScope.launch {
        cartDao.updateCartItem(item)
    }

    fun delete(item: CartModel) = viewModelScope.launch {
        cartDao.deleteCartItem(item)
    }

    fun clearCart() = viewModelScope.launch {
        preferences.getUserUid().firstOrNull()?.let { uid ->
            cartDao.clearUserCart(uid)
        }
    }
}
