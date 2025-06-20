package com.wardrobe.armoire.ui.wardrobe

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentOutfitDetailBinding
import com.wardrobe.armoire.databinding.FragmentWardrobeDetailBinding
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.ui.outfit.OutfitViewmodel
import java.io.File


class OutfitDetailFragment : Fragment() {

    private lateinit var binding: FragmentWardrobeDetailBinding
    private lateinit var outfit: OutfitModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWardrobeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        outfit = arguments?.getParcelable("outfit") ?: return

        binding.imageWardrobe.loadSmartImage(outfit.path)
        binding.textDescription.text = outfit.description

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun ImageView.loadSmartImage(path: String, fallbackRes: Int = R.drawable.search_svgrepo_com) {
        val context = this.context

        when {
            path.startsWith("/data") || path.startsWith(context.filesDir.absolutePath) -> {
                val file = File(path)
                if (file.exists()) {
                    setImageURI(Uri.fromFile(file))
                } else {
                    setImageResource(fallbackRes)
                }
            }
            path.endsWith(".jpg") || path.endsWith(".png") -> {
                try {
                    val inputStream = context.assets.open(path)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    setImageDrawable(drawable)
                } catch (e: Exception) {
                    setImageResource(fallbackRes)
                }
            }
            else -> {
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
