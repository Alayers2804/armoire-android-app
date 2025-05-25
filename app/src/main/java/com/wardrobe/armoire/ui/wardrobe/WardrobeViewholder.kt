package com.wardrobe.armoire.ui.wardrobe

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.WardrobeModel

class WardrobeViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imageWardrobe: ImageView = itemView.findViewById(R.id.img_wardrobe)
    private val txtWardrobe: TextView = itemView.findViewById(R.id.txt_wardrobe)

    fun bind(item: WardrobeModel, onClick: (String) -> Unit) {
        imageWardrobe.setImageResource(0)
        txtWardrobe.text = item.description
        itemView.setOnClickListener {
            onClick(item.description ?: "")
        }
    }
}