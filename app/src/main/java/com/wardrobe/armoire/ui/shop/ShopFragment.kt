package com.wardrobe.armoire.ui.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.api.gpt.RecommendationViewmodel
import com.wardrobe.armoire.ui.cart.CartViewmodel
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import com.wardrobe.armoire.utils.preferenceDefaultValue
import kotlinx.coroutines.launch


class ShopFragment : Fragment() {

    private lateinit var viewModel: ShopViewmodel
    private lateinit var adapter: ShopAdapter
    private lateinit var recommendationVM: RecommendationViewmodel

    private lateinit var searchEdit: EditText
    private lateinit var aiButton: Button
    private lateinit var preferences: Preferences

    private var isShowingRecommendations = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageButton>(R.id.btn_back)

        // Init ViewModels
        viewModel = ViewModelProvider(this)[ShopViewmodel::class.java]
        recommendationVM = ViewModelProvider(this)[RecommendationViewmodel::class.java]
        preferences = Preferences.getInstance(requireContext().dataStore)

        // Init Views
        searchEdit = view.findViewById(R.id.edit_search)
        aiButton = view.findViewById(R.id.button_ai_recommend)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_shop)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_ai_recommendation)

        recommendationVM.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        adapter = ShopAdapter(emptyList()) { item ->
            val bundle = Bundle().apply {
                putParcelable("shopItem", item)
            }
            findNavController().navigate(R.id.action_shopFragment_to_detailShopFragment, bundle)
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        viewModel.getAllShopItems()

        viewModel.shopAllitems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        recommendationVM.recommendedShopItems.observe(viewLifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                isShowingRecommendations = false
                backButton.visibility = View.GONE
                aiButton.isEnabled = true
            } else {
                adapter.updateItems(items)
                isShowingRecommendations = true
                backButton.visibility = View.VISIBLE
                aiButton.isEnabled = false
            }
        }

        lifecycleScope.launch {
            preferences.getUserUid().collect { uid ->
                if (uid != preferenceDefaultValue) {
                    searchEdit.setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            val query = searchEdit.text.toString().trim()

                            if (query.isBlank()) {
                                viewModel.getAllShopItems()
                                return@setOnEditorActionListener true
                            }

                            if (query.lowercase().contains("recommend")) {
                                recommendationVM.fetchCustomPromptRecommendations(uid, query)
                            } else {
                                viewModel.filterShopItems(query)
                            }
                            true
                        } else false
                    }

                    // AI Mix Button manually triggered
                    aiButton.setOnClickListener {
                        recommendationVM.fetchShopRecommendations(uid)
                    }
                }
            }
        }

        backButton.setOnClickListener {
            isShowingRecommendations = false
            backButton.visibility = View.GONE
            searchEdit.setText("")
            aiButton.isEnabled = true
            viewModel.getAllShopItems()
        }

        aiButton.isEnabled = !isShowingRecommendations

        val cartButton = view.findViewById<ImageButton>(R.id.button_cart)
        val cartBadge = view.findViewById<TextView>(R.id.cart_badge)

        cartButton.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        val cartDao = AppDatabase.getDatabase(requireContext()).cartDao()
        val cartViewModel = ViewModelProvider(
            this,
            BaseViewModelFactory { CartViewmodel(cartDao, preferences) }
        )[CartViewmodel::class.java]

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            val count = cartItems.size
            if (count > 0) {
                cartBadge.visibility = View.VISIBLE
                cartBadge.text = count.toString()
            } else {
                cartBadge.visibility = View.GONE
            }
        }
    }
}


