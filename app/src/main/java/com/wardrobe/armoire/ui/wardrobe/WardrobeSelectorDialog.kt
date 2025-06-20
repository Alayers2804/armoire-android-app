package com.wardrobe.armoire.ui.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.wardrobe.WardrobeModel

class WardrobeSelectorDialog(
    private val wardrobes: List<WardrobeModel>,
    private val onSelect: (List<WardrobeModel>) -> Unit
) : BottomSheetDialogFragment() {

    private val selectedItems = mutableSetOf<WardrobeModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_wardrobe_selector, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_wardrobe_select)
        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = WardrobeAdapter(
            wardrobes,
            onClick = { wardrobeItem ->
                if (selectedItems.contains(wardrobeItem)) selectedItems.remove(wardrobeItem)
                else selectedItems.add(wardrobeItem)
            },
            isSelectable = true,
        )

        confirmButton.setOnClickListener {
            onSelect(selectedItems.toList())
            dismiss()
        }

        return view
    }
}