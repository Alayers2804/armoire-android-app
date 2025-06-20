package com.wardrobe.armoire.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.R
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore

class OrderStatusFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderViewmodel: OrderViewmodel
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = arguments?.getString("status")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_orders)
        recyclerView.layoutManager = LinearLayoutManager(context)

        orderAdapter = OrderAdapter(emptyList()) { order ->
            // Update order status to "On Delivery"
            val updatedOrder = order.copy(status = "On delivery")
            orderViewmodel.update(updatedOrder)

            Toast.makeText(requireContext(), "Order ${order.productName} sent!", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = orderAdapter

        orderViewmodel = ViewModelProvider(this)[OrderViewmodel::class.java]

        val orderDao = AppDatabase.getDatabase(requireContext()).orderDao()
        val preferences = Preferences.getInstance(requireContext().dataStore)

        orderViewmodel.init(orderDao, preferences)

        status?.let {
            orderViewmodel.getOrdersByStatus(it).observe(viewLifecycleOwner) { orders ->
                orderAdapter.updateOrders(orders)
            }
        }
    }

    companion object {
        fun newInstance(status: String) = OrderStatusFragment().apply {
            arguments = Bundle().apply {
                putString("status", status)
            }
        }
    }
}