package com.wardrobe.armoire.ui.outfit

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import com.wardrobe.armoire.ui.wardrobe.WardrobeViewholder

class OutfitAdapter(
    private var dataList: List<OutfitModel>,
    private var onClick: (OutfitModel) -> Unit = {},
    private val isDeletable: Boolean = false,
    private val onDelete: (OutfitModel) -> Unit = {}
) : RecyclerView.Adapter<OutfitViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wardrobe_outfit_list, parent, false)
        return OutfitViewholder(view)
    }

    fun updateData(newList: List<OutfitModel>) {
        Log.d("OutfitAdapter", "updateData: ${newList.size} items")
        dataList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: OutfitViewholder, position: Int) {
        val item = dataList[position]

        holder.bind(
            item = item,
            onClick = { model ->
                onClick(model)
            },
            isDeletable = isDeletable,
            onDelete = onDelete
        )

    }
}
