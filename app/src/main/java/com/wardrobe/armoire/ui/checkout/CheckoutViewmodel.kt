package com.wardrobe.armoire.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.cart.CartDao
import com.wardrobe.armoire.model.cart.CartModel
import com.wardrobe.armoire.model.order.OrderDao
import com.wardrobe.armoire.model.order.OrderModel
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import com.wardrobe.armoire.utils.preferenceDefaultValue
import kotlinx.coroutines.launch
import java.util.UUID

class CheckoutViewmodel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getDatabase(application).cartDao()
    private val orderDao = AppDatabase.getDatabase(application).orderDao()
    private val preferences = Preferences.getInstance(application.dataStore)

    private val _selectedCartItems = MutableLiveData<List<CartModel>>()
    val selectedCartItems: LiveData<List<CartModel>> = _selectedCartItems

    private val _subtotal = MutableLiveData<Int>()
    val subtotal: LiveData<Int> = _subtotal

    private val _shippingCost = MutableLiveData<Int>().apply { value = 27000 }
    val shippingCost: LiveData<Int> = _shippingCost

    val total: LiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(_subtotal) { value = (_subtotal.value ?: 0) + (_shippingCost.value ?: 0) }
        addSource(_shippingCost) { value = (_subtotal.value ?: 0) + (_shippingCost.value ?: 0) }
    }

    fun loadSelectedCartItems() {
        viewModelScope.launch {
            preferences.getUserUid().collect { uid ->
                if (uid != preferenceDefaultValue) {
                    cartDao.getCartItems(uid).collect { items ->
                        _selectedCartItems.postValue(items)
                        _subtotal.postValue(items.sumOf { it.price })
                    }
                }
            }
        }
    }

    fun placeOrder(shippingAddress: String) {
        viewModelScope.launch {
            preferences.getUserUid().collect { uid ->
                if (uid != preferenceDefaultValue) {
                    val items = _selectedCartItems.value ?: return@collect
                    items.forEach { item ->
                        val order = OrderModel(
                            orderId = UUID.randomUUID().toString(),
                            productUid = item.productUid,
                            productName = item.name,
                            imageUrl = item.imageUrl,
                            status = "Packing",
                            shipping = shippingAddress,
                            totalPrice = item.price,
                            userUid = uid
                        )
                        orderDao.insertOrder(order)
                        cartDao.deleteCartItem(item)
                    }
                    _selectedCartItems.postValue(emptyList())
                    _subtotal.postValue(0)
                }
            }
        }
    }
}
