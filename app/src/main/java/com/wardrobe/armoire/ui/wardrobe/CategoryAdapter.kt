package com.wardrobe.armoire.ui.wardrobe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.CategoryItem
import com.wardrobe.armoire.model.wardrobe.WardrobeCategory
import com.wardrobe.armoire.ui.outfit.OutfitAdapter

class CategoryAdapter(
    private val categories: List<WardrobeCategory>,
    private val onItemClick: (Any) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.txt_category_title)
        val innerRecycler: RecyclerView = view.findViewById(R.id.recycler_inner)

        var wardrobeAdapter: WardrobeAdapter? = null
        var outfitAdapter: OutfitAdapter? = null

        fun bind(category: WardrobeCategory) {
            titleText.text = category.title

            titleText.setOnClickListener {
                onItemClick(category.title)
            }
            val firstItem = category.items.firstOrNull()

            if (firstItem is CategoryItem.Wardrobe) {
                val wardrobeList =
                    category.items.mapNotNull { (it as? CategoryItem.Wardrobe)?.data }
                if (wardrobeAdapter == null) {
                    wardrobeAdapter = WardrobeAdapter(
                        dataList = wardrobeList,
                        onClick = { model -> onItemClick(model) },
                        isSelectable = false,
                        isDeletable = false
                    )
                    innerRecycler.layoutManager = LinearLayoutManager(
                        innerRecycler.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    innerRecycler.adapter = wardrobeAdapter
                } else {
                    wardrobeAdapter?.updateData(wardrobeList)
                }
            } else if (firstItem is CategoryItem.Outfit) {
                val outfitList = category.items.mapNotNull { (it as? CategoryItem.Outfit)?.data }
                if (outfitAdapter == null) {
                    outfitAdapter = OutfitAdapter(
                        dataList = outfitList,
                        onClick = { model -> onItemClick(model) }, // pass full OutfitModel
                        isDeletable = false
                    )
                    innerRecycler.layoutManager = LinearLayoutManager(
                        innerRecycler.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    innerRecycler.adapter = outfitAdapter
                } else {
                    outfitAdapter?.updateData(outfitList)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_section, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<WardrobeCategory>) {
        // You can do more intelligent diffing here with DiffUtil if needed
        (categories as MutableList).clear()
        (categories as MutableList).addAll(newCategories)
        notifyDataSetChanged()
    }
}
