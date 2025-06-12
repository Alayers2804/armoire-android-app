package com.wardrobe.armoire.ui.browse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.api.gpt.RecommendationViewmodel
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.ui.outfit.OutfitAdapter
import com.wardrobe.armoire.ui.outfit.OutfitViewmodel

class BrowseFragment : Fragment() {

    private lateinit var searchEdit: EditText
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    private var isSearching = false
    private var fullList = listOf<OutfitModel>()
    private var filteredList = listOf<OutfitModel>()
    private lateinit var adapter: OutfitAdapter

    private val browseViewModel: BrowseViewmodel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recommendationVM = ViewModelProvider(this)[RecommendationViewmodel::class.java]

        recommendationVM.recommendedShopItems.observe(viewLifecycleOwner) { items ->
            // Show recommended shop items
        }

        recommendationVM.recommendedOutfits.observe(viewLifecycleOwner) { outfits ->
            // Show recommended outfits
        }

        recommendationVM.fetchRecommendations(userId = "some_user_id")
        searchEdit = view.findViewById(R.id.edit_search)
        backButton = view.findViewById(R.id.btn_back)
        recyclerView = view.findViewById(R.id.recycler_results)

        adapter = OutfitAdapter(emptyList()) { outfitId ->
            // handle click if needed
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        browseViewModel.outfitBrowser.observe(viewLifecycleOwner) { outfits ->
            fullList = outfits
            adapter.updateData(fullList)
        }


        // Search input listener
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                isSearching = query.isNotEmpty()
                backButton.visibility = if (isSearching) View.VISIBLE else View.GONE

                val filtered = if (isSearching) {
                    fullList.filter { it.style.contains(query, ignoreCase = true) }
                } else {
                    fullList
                }
                adapter.updateData(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Back button clears search
        backButton.setOnClickListener {
            searchEdit.setText("")
            it.visibility = View.GONE
            adapter.updateData(fullList)
        }

        browseViewModel.getAllWardrobe()
    }
}
