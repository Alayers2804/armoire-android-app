package com.wardrobe.armoire.ui.checkout

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.cart.CartModel
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore

class CheckoutFragment : Fragment() {

    private lateinit var checkoutVM: CheckoutViewmodel
    private lateinit var preferences: Preferences
    private lateinit var adapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = Preferences.getInstance(requireContext().dataStore)


        checkoutVM = ViewModelProvider(
            this,
            BaseViewModelFactory { CheckoutViewmodel(requireActivity().application) }
        )[CheckoutViewmodel::class.java]
        checkoutVM.loadSelectedCartItems()

        // RecyclerView setup
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_checkout_items)
        adapter = CheckoutAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        // Observe LiveData
        observeViewModel(view)
    }

    private fun observeViewModel(view: View) {
        val subtotalText = view.findViewById<TextView>(R.id.text_subtotal)
        val totalText = view.findViewById<TextView>(R.id.text_total)

        checkoutVM.selectedCartItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        checkoutVM.subtotal.observe(viewLifecycleOwner) { subtotal ->
            subtotalText?.text = "Item Subtotal\t\t\t\tRp ${subtotal}"
        }

        checkoutVM.total.observe(viewLifecycleOwner) { total ->
            totalText?.text = "Total Payment\t\t\t\tRp ${total}"
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_checkout)
        val placeOrderButton = view.findViewById<Button>(R.id.button_place_order)

        placeOrderButton.setOnClickListener {
            placeOrderButton.isEnabled = false
            progressBar.visibility = View.VISIBLE


            checkoutVM.placeOrder("Siwalankerto Timur VI/AE-12, Surabaya")

            Handler(Looper.getMainLooper()).postDelayed({
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Order placed", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.orderFragment)
            }, 2000)
        }

    }
}
