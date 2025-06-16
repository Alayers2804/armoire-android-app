package com.wardrobe.armoire.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.model.order.OrderDao
import com.wardrobe.armoire.model.order.OrderModel
import com.wardrobe.armoire.utils.Preferences
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class OrderViewmodel : ViewModel() {

    private lateinit var orderDao: OrderDao
    private lateinit var preferences: Preferences

    fun init(orderDao: OrderDao, preferences: Preferences) {
        this.orderDao = orderDao
        this.preferences = preferences
    }

    fun getOrdersByStatus(status: String): LiveData<List<OrderModel>> {
        return preferences.getUserUid().flatMapLatest { uid ->
            orderDao.getOrdersByStatus(uid, status)
        }.asLiveData()
    }

    fun insert(order: OrderModel) = viewModelScope.launch {
        orderDao.insertOrder(order)
    }

    fun update(order: OrderModel) = viewModelScope.launch {
        orderDao.updateOrder(order)
    }

    fun delete(order: OrderModel) = viewModelScope.launch {
        orderDao.deleteOrder(order)
    }
}
