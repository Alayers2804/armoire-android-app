package com.wardrobe.armoire.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.R
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewmodel
    private lateinit var preferences: Preferences
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = Preferences.getInstance(requireContext().dataStore)
        val cartDao = AppDatabase.getDatabase(requireContext()).cartDao()
        cartViewModel = ViewModelProvider(
            this,
            BaseViewModelFactory { CartViewmodel(cartDao, preferences) }
        )[CartViewmodel::class.java]

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_cart)
        cartAdapter = CartAdapter(onCheckChanged = { updatedItem ->
            cartViewModel.update(updatedItem)
        })
        recyclerView.adapter = cartAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupObservers()

        val buttonCheckout = view.findViewById<Button>(R.id.button_checkout)
        buttonCheckout.setOnClickListener {
            val selectedItems = cartViewModel.cartItems.value?.filter { it.isChecked } ?: emptyList()
            if (selectedItems.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putParcelableArrayList("checkoutItems", ArrayList(selectedItems))
                }
                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "No item selected", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonClearAll = view.findViewById<Button>(R.id.button_clear_all)
        buttonClearAll.setOnClickListener {
            val selectedItems = cartViewModel.cartItems.value?.filter { it.isChecked } ?: emptyList()
            if (selectedItems.isNotEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Remove Selected")
                    .setMessage("Remove ${selectedItems.size} selected items from the cart?")
                    .setPositiveButton("Yes") { _, _ ->
                        selectedItems.forEach { cartViewModel.delete(it) }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Clear Cart")
                    .setMessage("Are you sure you want to remove all items from the cart?")
                    .setPositiveButton("Yes") { _, _ ->
                        cartViewModel.clearCart()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

    }

    private fun setupObservers() {
        val textItemCount = view?.findViewById<TextView>(R.id.text_item_count)
        val textTotal = view?.findViewById<TextView>(R.id.text_total)
        val buttonClearAll = view?.findViewById<Button>(R.id.button_clear_all)

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            cartAdapter.submitList(cartList)
            textItemCount?.text = "(${cartList.size})"

            val selectedItems = cartList.filter { it.isChecked }
            val totalPrice = selectedItems.sumOf { it.price }
            textTotal?.text = "Total Rp${String.format("%,d", totalPrice)}"

            // Update button text and visibility
            buttonClearAll?.apply {
                visibility = if (cartList.isEmpty()) View.GONE else View.VISIBLE
                text = if (selectedItems.isNotEmpty()) {
                    "Remove Selected (${selectedItems.size})"
                } else {
                    "Clear All"
                }
            }
        }
    }


}


