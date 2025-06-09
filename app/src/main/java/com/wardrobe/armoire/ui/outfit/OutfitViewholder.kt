package com.wardrobe.armoire.ui.outfit

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.outfit.OutfitModel

class OutfitViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imageOutfit: ImageView = itemView.findViewById(R.id.img_outfit)
    private val txtOutfit: TextView = itemView.findViewById(R.id.txt_outfit)

    fun bind(item: OutfitModel, onClick: (String) -> Unit) {
        val context = itemView.context

        val imageResId = context.resources.getIdentifier(
            item.path, "drawable", context.packageName
        )

        if (imageResId != 0) {
            imageOutfit.setImageResource(imageResId)
        } else {
            imageOutfit.setImageResource(R.drawable.search_svgrepo_com) // fallback
        }

        txtOutfit.text = ""
        itemView.setOnClickListener {
            onClick(item.description ?: "")
        }
    }
}