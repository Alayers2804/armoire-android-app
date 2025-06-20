package com.wardrobe.armoire.ui.wardrobe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.WardrobeModel

class WardrobeAdapter(
    private var dataList: List<WardrobeModel>,
    private val onClick: (String) -> Unit,
    private val isSelectable: Boolean = false  // <-- default false for normal use
) : RecyclerView.Adapter<WardrobeViewholder>() {

    private val selectedItems = mutableSetOf<String>()

    fun updateData(newList: List<WardrobeModel>) {
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
            onClick = { uid ->
                if (isSelectable) {
                    if (selectedItems.contains(uid)) selectedItems.remove(uid)
                    else selectedItems.add(uid)
                    notifyItemChanged(position)
                }
                onClick(uid)
            }
        )
    }

    override fun getItemCount(): Int = dataList.size
}

