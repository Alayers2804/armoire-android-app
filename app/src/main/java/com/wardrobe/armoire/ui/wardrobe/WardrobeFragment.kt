package com.wardrobe.armoire.ui.wardrobe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentWardrobeBinding
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.wardrobe.CategoryItem
import com.wardrobe.armoire.model.wardrobe.WardrobeCategory
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import com.wardrobe.armoire.ui.outfit.OutfitViewmodel
import kotlin.getValue

class WardrobeFragment : Fragment() {

    private var _binding: FragmentWardrobeBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter

    private val wardrobeViewmodel: WardrobeViewmodel by activityViewModels()
    private val outfitViewmodel: OutfitViewmodel by activityViewModels()

    private val combinedCategories = MediatorLiveData<List<WardrobeCategory>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWardrobeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wardrobeViewmodel.observeWardrobeByStatus("my_item")
        wardrobeViewmodel.observeWardrobeByStatus("my_preloved")
        outfitViewmodel.observeWardrobeByStatus("my_outfit")
        outfitViewmodel.observeWardrobeByStatus("my_saved")

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            binding.recyclerView.setPadding(
                binding.recyclerView.paddingLeft,
                binding.recyclerView.paddingTop,
                binding.recyclerView.paddingRight,
                bottomInset
            )
            insets
        }

        val bottomNavHeight = requireActivity()
            .findViewById<BottomNavigationView>(R.id.menu_navigation).height


        binding.recyclerView.setPadding(0, 0, 0, bottomNavHeight)



        wardrobeViewmodel.wardrobeMyitems.observe(viewLifecycleOwner) { updateCombinedCategories() }
        wardrobeViewmodel.wardrobeMyPreloved.observe(viewLifecycleOwner) { updateCombinedCategories() }
        outfitViewmodel.outfitMyOutfits.observe(viewLifecycleOwner) { updateCombinedCategories() }
        outfitViewmodel.outfitMySaved.observe(viewLifecycleOwner) { updateCombinedCategories() }

        combinedCategories.observe(viewLifecycleOwner) { categories ->
            if (!::categoryAdapter.isInitialized) {
                categoryAdapter = CategoryAdapter(categories) { item ->
                    when (item) {
                        is String -> {
                            when (item) {
                                "My Wardrobe" -> {
                                    findNavController().navigate(R.id.action_wardrobeFragment_to_myItemsFragment)
                                }
                                "My Preloved" -> {
                                    Toast.makeText(requireContext(), "Still In Development", Toast.LENGTH_SHORT).show()
                                }
                                "My Outfits" -> {
                                    val bundle = bundleOf("status" to "my_outfit")
                                    findNavController().navigate(R.id.action_wardrobeFragment_to_myOutfitsFragment, bundle)
                                }
                                "My Saved" -> {
                                    val bundle = bundleOf("status" to "my_saved")
                                    findNavController().navigate(R.id.action_wardrobeFragment_to_myOutfitsFragment, bundle)
                                }
                            }
                        }

                        is WardrobeModel -> {
                            val bundle = bundleOf("wardrobe" to item)
                            findNavController().navigate(R.id.wardrobeDetailFragment, bundle)
                        }

                        is OutfitModel -> {
                            val bundle = bundleOf("outfit" to item)
                            findNavController().navigate(R.id.outfitDetailFragment, bundle)
                        }
                    }
                }

                binding.recyclerView.layoutManager = LinearLayoutManager(context)
            }
            binding.recyclerView.adapter = categoryAdapter
            categoryAdapter.updateData(categories)
        }


    }

    fun updateCombinedCategories() {
        val combined = mutableListOf<WardrobeCategory>()

        val wardrobeItems = wardrobeViewmodel.wardrobeMyitems.value?.map { CategoryItem.Wardrobe(it) } ?: emptyList()
        val wardrobePreloved = wardrobeViewmodel.wardrobeMyPreloved.value?.map { CategoryItem.Wardrobe(it) } ?: emptyList()
        val outfitItems = outfitViewmodel.outfitMyOutfits.value?.map { CategoryItem.Outfit(it) } ?: emptyList()
        val outfitSaved = outfitViewmodel.outfitMySaved.value?.map { CategoryItem.Outfit(it) } ?: emptyList()

        combined.add(WardrobeCategory("My Wardrobe", wardrobeItems))
        combined.add(WardrobeCategory("My Preloved", wardrobePreloved))
        combined.add(WardrobeCategory("My Outfits", outfitItems))
        combined.add(WardrobeCategory("My Saved", outfitSaved))

        combinedCategories.value = combined
    }

    override fun onResume() {
        super.onResume()
        wardrobeViewmodel.wardrobeMyitems.observe(viewLifecycleOwner) {
            updateCombinedCategories()
        }
    }
}