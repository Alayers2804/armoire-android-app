package com.wardrobe.armoire.ui.browse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.api.gpt.RecommendationViewmodel
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.ui.outfit.OutfitAdapter
import com.wardrobe.armoire.ui.outfit.OutfitViewmodel
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import com.wardrobe.armoire.utils.preferenceDefaultValue
import kotlinx.coroutines.launch

class BrowseFragment : Fragment() {

    private lateinit var searchEdit: EditText
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var aiButton: Button
    private lateinit var progressBar: ProgressBar

    private val browseViewModel: BrowseViewmodel by activityViewModels()
    private val recommendationVM: RecommendationViewmodel by activityViewModels()
    private lateinit var preferences: Preferences

    private lateinit var adapter: OutfitAdapter
    private var fullList = listOf<OutfitModel>()
    private var isShowingRecommendations = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preferences = Preferences.getInstance(requireContext().dataStore)
        searchEdit = view.findViewById(R.id.edit_search)
        backButton = view.findViewById(R.id.btn_back)
        recyclerView = view.findViewById(R.id.recycler_results)
        aiButton = view.findViewById(R.id.button_ai_recommend)
        progressBar = view.findViewById(R.id.progress_ai_outfit)

        adapter = OutfitAdapter(
            emptyList(),
            onClick = { uid ->
                Toast.makeText(requireContext(), "Clicked: $uid", Toast.LENGTH_SHORT).show()
            }
        ) {}
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        // Observe data
        browseViewModel.outfitBrowser.observe(viewLifecycleOwner) { outfits ->
            if (!isShowingRecommendations) {
                fullList = outfits
                adapter.updateData(outfits)
            }
        }

        recommendationVM.recommendedOutfits.observe(viewLifecycleOwner) { outfits ->
            if (outfits.isNullOrEmpty()) {
                isShowingRecommendations = false
                adapter.updateData(fullList)
                aiButton.isEnabled = true
                backButton.visibility = View.GONE
            } else {
                isShowingRecommendations = true
                adapter.updateData(outfits)
                backButton.visibility = View.VISIBLE
                aiButton.isEnabled = false
            }
        }

        recommendationVM.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Search functionality
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    adapter.updateData(fullList)
                    backButton.visibility = View.GONE
                } else {
                    val filtered = fullList.filter {
                        it.style.contains(query, ignoreCase = true)
                    }
                    adapter.updateData(filtered)
                    backButton.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        backButton.setOnClickListener {
            searchEdit.setText("")
            backButton.visibility = View.GONE
            adapter.updateData(fullList)
            isShowingRecommendations = false
            aiButton.isEnabled = true
        }

        // AI Recommend Button
        aiButton.setOnClickListener {
            lifecycleScope.launch {
                preferences.getUserUid().collect { uid ->
                    if (uid != preferenceDefaultValue) {
                        recommendationVM.fetchOutfitRecommendations(uid)
                    }
                }
            }
        }

        browseViewModel.getAllWardrobe()
    }
}


