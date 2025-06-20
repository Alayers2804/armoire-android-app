package com.wardrobe.armoire.ui.wardrobe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wardrobe.armoire.databinding.FragmentMyOutfitsBinding
import com.wardrobe.armoire.model.api.gpt.GptVisionHelper
import com.wardrobe.armoire.model.api.gpt.GptVisionResponse
import com.wardrobe.armoire.model.api.gpt.RetrofitInstance
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import com.wardrobe.armoire.ui.outfit.OutfitAdapter
import com.wardrobe.armoire.ui.outfit.OutfitViewmodel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOutfitsFragment : Fragment() {


    private var _binding: FragmentMyOutfitsBinding? = null
    private val binding get() = _binding!!

    private val outfitViewModel: OutfitViewmodel by activityViewModels()
    private val wardrobeViewModel: WardrobeViewmodel by activityViewModels()

    private lateinit var adapter: OutfitAdapter

    private var outfitStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        outfitStatus = arguments?.getString("status") // e.g. "my_outfit" or "my_saved"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOutfitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = OutfitAdapter(emptyList()) { uid ->
            Toast.makeText(requireContext(), "Clicked: $uid", Toast.LENGTH_SHORT).show()
        }

        binding.buttonAddOutfit.setOnClickListener {
            wardrobeViewModel.fetchWardrobeByStatus("my_item")

            val items = wardrobeViewModel.wardrobeMyitems.value ?: emptyList()

            WardrobeSelectorDialog(items) { selectedItems ->
                binding.buttonAddOutfit.isEnabled = false
                binding.loadingIndicator.visibility = View.VISIBLE

                outfitViewModel.createOutfitFromWardrobes(requireContext(), selectedItems) { success, message ->
                    binding.buttonAddOutfit.isEnabled = true
                    binding.loadingIndicator.visibility = View.GONE // Hide loading

                    if (success) {
                        Toast.makeText(requireContext(), "Outfit created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), message ?: "Unknown error", Toast.LENGTH_LONG).show()
                    }
                }
            }.show(parentFragmentManager, "wardrobe_selector")
        }



        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@MyOutfitsFragment.adapter
        }

        when (outfitStatus) {
            "my_outfit" -> {
                outfitViewModel.outfitMyOutfits.observe(viewLifecycleOwner) {
                    adapter.updateData(it)
                    binding.outfitTitle.text = "MyOutfits (${it.size})"
                }
            }
            "my_saved" -> {
                outfitViewModel.outfitMySaved.observe(viewLifecycleOwner) {
                    adapter.updateData(it)
                    binding.outfitTitle.text = "MySaved (${it.size})"
                }
            }
        }

        outfitStatus?.let { outfitViewModel.fetchOutfitByStatus(it) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}