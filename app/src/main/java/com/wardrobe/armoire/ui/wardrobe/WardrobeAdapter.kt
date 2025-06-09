package com.wardrobe.armoire.ui.wardrobe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.WardrobeModel

class WardrobeAdapter(private var dataList : List<WardrobeModel>, private var onClick: (String) -> Unit) :
    RecyclerView.Adapter<WardrobeViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WardrobeViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wardrobe_item_list, parent, false)
        return  WardrobeViewholder(view)
    }

    fun updateData(newList: List<WardrobeModel>) {
        dataList = newList
        notifyDataSetChanged()  // Refresh the whole list
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: WardrobeViewholder, position: Int) {
        val item = dataList[position]
        holder.bind(item, onClick)
    }
}