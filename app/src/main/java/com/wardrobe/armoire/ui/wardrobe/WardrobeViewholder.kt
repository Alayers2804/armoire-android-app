package com.wardrobe.armoire.ui.wardrobe

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import java.io.File

class WardrobeViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imageWardrobe: ImageView = itemView.findViewById(R.id.img_wardrobe)


    fun bind(
        item: WardrobeModel,
        isSelected: Boolean,
        isSelectable: Boolean,
        onClick: (WardrobeModel) -> Unit,
        isDeletable: Boolean,
        onDelete: (WardrobeModel) -> Unit
    ) {
        imageWardrobe.loadSmartImage(item.path)

        val container = itemView.findViewById<LinearLayout>(R.id.card_container)
        container.setBackgroundColor(
            if (isSelectable && isSelected) Color.LTGRAY else Color.WHITE
        )

        itemView.setOnClickListener { onClick(item) }

        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        if (isDeletable) {
            deleteButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.GONE
        }

        deleteButton.setOnClickListener {
            onDelete(item)
        }
    }


    fun ImageView.loadSmartImage(path: String, fallbackRes: Int = R.drawable.search_svgrepo_com) {
        val context = this.context

        when {
            path.startsWith("/data") || path.startsWith(context.filesDir.absolutePath) -> {
                // Local file from internal storage
                val file = File(path)
                if (file.exists()) {
                    setImageURI(Uri.fromFile(file))
                } else {
                    setImageResource(fallbackRes)
                }
            }

            path.endsWith(".jpg") || path.endsWith(".png") -> {
                // Asset image
                try {
                    val inputStream = context.assets.open(path)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    setImageDrawable(drawable)
                } catch (e: Exception) {
                    setImageResource(fallbackRes)
                }
            }

            else -> {
                // Assume drawable name
                val resId = context.resources.getIdentifier(path, "drawable", context.packageName)
                if (resId != 0) {
                    setImageResource(resId)
                } else {
                    setImageResource(fallbackRes)
                }
            }
        }
    }

}