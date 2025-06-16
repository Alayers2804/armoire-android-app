package com.wardrobe.armoire.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.order.OrderModel

class OrderAdapter(
    private var orders: List<OrderModel>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txt_product_name)
        val price: TextView = view.findViewById(R.id.txt_total_price)
        val status: TextView = view.findViewById(R.id.txt_status)
        val image: ImageView = view.findViewById(R.id.img_product)
        val button: Button = view.findViewById(R.id.btn_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.name.text = order.productName
        holder.price.text = "Rp${order.totalPrice}"
        holder.status.text = order.status

        // Load image from drawable path
        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(
            order.imageUrl, "drawable", context.packageName
        )
        if (imageResId != 0) {
            holder.image.setImageResource(imageResId)
        } else {
            holder.image.setImageResource(R.drawable.search_svgrepo_com) // fallback
        }

        // Example: Show "Send" button only for Packing status
        holder.button.visibility =
            if (order.status.equals("Packing", ignoreCase = true)) View.VISIBLE else View.GONE

        holder.button.setOnClickListener {
            // You can define a callback here if needed
            Toast.makeText(context, "Sending order: ${order.productName}", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateOrders(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
