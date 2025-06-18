package com.wardrobe.armoire.ui.wardrobe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentMyItemsBinding

class MyItemsFragment : Fragment() {

    private var _binding: FragmentMyItemsBinding? = null
    private val binding get() = _binding!!

    private val wardrobeViewmodel: WardrobeViewmodel by activityViewModels()
    private lateinit var wardrobeAdapter: WardrobeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wardrobeAdapter = WardrobeAdapter(emptyList()) { uid ->
            // Handle item click
            Toast.makeText(requireContext(), "Clicked item UID: $uid", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = wardrobeAdapter
        }

        wardrobeViewmodel.wardrobeMyitems.observe(viewLifecycleOwner) {
            wardrobeAdapter.updateData(it)
            binding.itemCount.text = "MyItems (${it.size})"
        }

        wardrobeViewmodel.fetchWardrobeByStatus("my_item")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
