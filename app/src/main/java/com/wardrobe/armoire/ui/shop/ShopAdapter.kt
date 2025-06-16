package com.wardrobe.armoire.ui.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.shop.ShopModel

class ShopAdapter(
    private var items: List<ShopModel>,
    private val onItemClick: (ShopModel) -> Unit
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img_wardrobe)
        val title: TextView = itemView.findViewById(R.id.txt_title)
        val price: TextView = itemView.findViewById(R.id.txt_price)
        val button: Button = itemView.findViewById(R.id.btn_view_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item_list, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.name
        holder.price.text = "Rp${item.price}"

        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(
            item.path, "drawable", context.packageName
        )
        if (imageResId != 0) {
            holder.img.setImageResource(imageResId)
        } else {
            holder.img.setImageResource(R.drawable.search_svgrepo_com)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.button.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ShopModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
