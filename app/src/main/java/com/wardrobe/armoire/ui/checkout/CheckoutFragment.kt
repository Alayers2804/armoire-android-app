package com.wardrobe.armoire.ui.checkout

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.cart.CartModel
import java.text.NumberFormat
import java.util.Locale

class CheckoutFragment : Fragment() {

    private lateinit var checkoutVM: CheckoutViewmodel
    private lateinit var adapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkoutVM = ViewModelProvider(
            this,
            BaseViewModelFactory { CheckoutViewmodel(requireActivity().application) }
        )[CheckoutViewmodel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_checkout_items)
        adapter = CheckoutAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        observeViewModel(view)

        val items = arguments?.getParcelableArrayList<CartModel>("checkoutItems") ?: emptyList()
        checkoutVM.setSelectedItems(items)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_checkout)

        checkoutVM.isOrderingDone.observe(viewLifecycleOwner) { done ->
            if (done == true) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Order placed", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.orderFragment)
            }
        }

        val backButton = view.findViewById<ImageView>(R.id.button_back)

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel(view: View) {
        val subtotalText = view.findViewById<TextView>(R.id.text_subtotal)
        val totalText = view.findViewById<TextView>(R.id.text_total)
        val shippingCostText = view.findViewById<TextView>(R.id.text_shipping_subtotal)

        checkoutVM.selectedCartItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        checkoutVM.subtotal.observe(viewLifecycleOwner) { subtotal ->
            subtotalText?.text = "Item Subtotal:\t\t${formatRupiah(subtotal)}"
        }

        checkoutVM.total.observe(viewLifecycleOwner) { total ->
            totalText?.text = "Total Payment:\t\t${formatRupiah(total)}"
        }

        checkoutVM.shippingCost.observe(viewLifecycleOwner) { shippingCost ->
            shippingCostText?.text = "Shipping Cost:		${formatRupiah(shippingCost)}"
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

    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp. ${formatter.format(amount)},-"
    }

}
