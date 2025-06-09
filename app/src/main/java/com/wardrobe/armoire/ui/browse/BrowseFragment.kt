package com.wardrobe.armoire.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.R

class BrowseFragment : Fragment() {

    private lateinit var searchEdit: EditText
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    // Sample state: your full and filtered data
    private var isSearching = false
    private var fullList = listOf<String>()  // Replace with your actual model
    private var filteredList = listOf<String>()
    private lateinit var adapter: YourAdapter  // Replace with your adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchEdit = view.findViewById(R.id.edit_search)
        backButton = view.findViewById(R.id.btn_back)
        recyclerView = view.findViewById(R.id.recycler_results)

        adapter = YourAdapter() // replace with actual init
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Populate with full list
        fullList = getAllData() // Load all items
        adapter.submitList(fullList)

        // Search input listener
        searchEdit.addTextChangedListener {
            val query = it.toString()
            isSearching = query.isNotBlank()
            backButton.visibility = if (isSearching) View.VISIBLE else View.GONE

            if (isSearching) {
                filteredList = fullList.filter { item -> item.contains(query, ignoreCase = true) }
                adapter.submitList(filteredList)
            } else {
                adapter.submitList(fullList)
            }
        }

        // Back button clears search
        backButton.setOnClickListener {
            searchEdit.setText("")
            it.visibility = View.GONE
            adapter.submitList(fullList)
        }
    }
}