package com.wardrobe.armoire.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.cart.CartModel

class CheckoutAdapter : ListAdapter<CartModel, CheckoutAdapter.CheckoutViewHolder>(DIFF_CALLBACK) {

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.text_item_name)
        private val price: TextView = itemView.findViewById(R.id.text_item_price)
        private val image: ImageView = itemView.findViewById(R.id.image_item)

        fun bind(item: CartModel) {
            name.text = item.name
            price.text = "Rp${item.price}"

            val context = itemView.context
            val imageResId = context.resources.getIdentifier(
                item.imageUrl, "drawable", context.packageName
            )
            if (imageResId != 0) {
                image.setImageResource(imageResId)
            } else {
                image.setImageResource(R.drawable.search_svgrepo_com)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_cart, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CartModel>() {
            override fun areItemsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
                return oldItem.productUid == newItem.productUid
            }

            override fun areContentsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
