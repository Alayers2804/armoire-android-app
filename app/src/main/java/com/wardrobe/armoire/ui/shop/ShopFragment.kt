package com.wardrobe.armoire.ui.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.api.gpt.RecommendationViewmodel
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import com.wardrobe.armoire.utils.preferenceDefaultValue
import kotlinx.coroutines.launch


class ShopFragment : Fragment() {

    private lateinit var viewModel: ShopViewmodel
    private lateinit var adapter: ShopAdapter
    private lateinit var recommendationVM: RecommendationViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModels
        viewModel = ViewModelProvider(this)[ShopViewmodel::class.java]
        recommendationVM = ViewModelProvider(this)[RecommendationViewmodel::class.java]

        // RecyclerView setup
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_shop)
        adapter = ShopAdapter(emptyList()) { item ->
            val bundle = Bundle().apply {
                putParcelable("shopItem", item)
            }
            findNavController().navigate(R.id.action_shopFragment_to_detailShopFragment, bundle)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        // Observe all items
        viewModel.shopAllitems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        // Observe recommendation
        recommendationVM.recommendedShopItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        // Initial load
        viewModel.getAllShopItems()

        // Search field
        val searchEdit = view.findViewById<EditText>(R.id.edit_search)
        val preferences = Preferences.getInstance(requireContext().dataStore)

        lifecycleScope.launch {
            preferences.getUserUid().collect { uid ->
                if (uid != preferenceDefaultValue) {
                    searchEdit.setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            val query = searchEdit.text.toString().trim()

                            if (query.lowercase().contains("recommend")) {
                                recommendationVM.fetchCustomPromptRecommendations(uid, query)
                            } else {
                                viewModel.filterShopItems(query)
                            }
                            true
                        } else false
                    }
                }
            }
        }

        // AI Recommend Button
        val aiButton = view.findViewById<Button>(R.id.button_ai_recommend)
        aiButton.setOnClickListener {
            recommendationVM.fetchShopRecommendations("some_user_id") // use real ID
        }
    }
}

