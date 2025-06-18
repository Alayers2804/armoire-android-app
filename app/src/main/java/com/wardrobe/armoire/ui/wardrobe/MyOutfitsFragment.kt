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
import com.wardrobe.armoire.databinding.FragmentMyOutfitsBinding
import com.wardrobe.armoire.ui.outfit.OutfitAdapter
import com.wardrobe.armoire.ui.outfit.OutfitViewmodel

class MyOutfitsFragment : Fragment() {


    private var _binding: FragmentMyOutfitsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OutfitViewmodel by activityViewModels()
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

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@MyOutfitsFragment.adapter
        }

        when (outfitStatus) {
            "my_outfit" -> {
                viewModel.outfitMyOutfits.observe(viewLifecycleOwner) {
                    adapter.updateData(it)
                    binding.outfitTitle.text = "MyOutfits (${it.size})"
                }
            }
            "my_saved" -> {
                viewModel.outfitMySaved.observe(viewLifecycleOwner) {
                    adapter.updateData(it)
                    binding.outfitTitle.text = "MySaved (${it.size})"
                }
            }
        }

        outfitStatus?.let { viewModel.fetchOutfitByStatus(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(status: String): MyOutfitsFragment {
            val fragment = MyOutfitsFragment()
            val args = Bundle()
            args.putString("status", status)
            fragment.arguments = args
            return fragment
        }
    }
}