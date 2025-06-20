package com.wardrobe.armoire.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.cart.CartModel

class CartAdapter(
    private var items: List<CartModel> = listOf(),
    private val onCheckChanged: (CartModel) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txt_name)
        val price: TextView = view.findViewById(R.id.txt_price)
        val image: ImageView = view.findViewById(R.id.img_product)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.price.text = "Rp${item.price}"

        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(
            item.imageUrl, "drawable", context.packageName
        )

        if (imageResId != 0) {
            holder.image.setImageResource(imageResId)
        } else {
            holder.image.setImageResource(R.drawable.search_svgrepo_com)
        }

        holder.checkbox.setOnCheckedChangeListener(null) // avoid triggering on recycled views
        holder.checkbox.isChecked = item.isChecked

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(item.copy(isChecked = isChecked))
        }
    }

    fun submitList(newItems: List<CartModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}

