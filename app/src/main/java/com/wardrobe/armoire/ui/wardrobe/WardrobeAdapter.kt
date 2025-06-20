package com.wardrobe.armoire.ui.wardrobe

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.WardrobeModel

class WardrobeAdapter(
    private var dataList: List<WardrobeModel>,
    private val onClick: (WardrobeModel) -> Unit,
    private val isSelectable: Boolean = false,
    private val isDeletable: Boolean = false,
    private val onDelete: (WardrobeModel) -> Unit = {}
) : RecyclerView.Adapter<WardrobeViewholder>() {

    private val selectedItems = mutableSetOf<String>()

    fun updateData(newList: List<WardrobeModel>) {
        Log.d("WardrobeAdapter", "updateData: ${newList.size} items")
        dataList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WardrobeViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wardrobe_item_list, parent, false)
        return WardrobeViewholder(view)
    }

    override fun onBindViewHolder(holder: WardrobeViewholder, position: Int) {
        val item = dataList[position]
        val isSelected = selectedItems.contains(item.uid)

        holder.bind(
            item = item,
            isSelected = isSelectable && isSelected,
            isSelectable = isSelectable,
            onClick = {
                if (isSelectable) {
                    if (selectedItems.contains(it.uid)) selectedItems.remove(it.uid)
                    else selectedItems.add(it.uid)
                    notifyItemChanged(position)
                }
                onClick(it)
            },
            isDeletable = isDeletable,
            onDelete = onDelete
        )
    }

    override fun getItemCount(): Int = dataList.size
}

