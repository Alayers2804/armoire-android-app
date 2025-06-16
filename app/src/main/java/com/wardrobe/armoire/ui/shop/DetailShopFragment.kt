package com.wardrobe.armoire.ui.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.cart.CartModel
import com.wardrobe.armoire.model.shop.ShopModel
import com.wardrobe.armoire.ui.cart.CartViewmodel
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DetailShopFragment : Fragment() {

    private lateinit var item: ShopModel
    private lateinit var cartViewModel: CartViewmodel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            item = it.getParcelable("shopItem")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val image = view.findViewById<ImageView>(R.id.imageItem)
        val name = view.findViewById<TextView>(R.id.textName)
        val price = view.findViewById<TextView>(R.id.textPrice)
        val buttonAddToCart = view.findViewById<Button>(R.id.buttonAddToCart)
        val description = view.findViewById<TextView>(R.id.textDescription)



        name.text = item.name
        price.text = "Rp${item.price}"
        description.text = item.description

        val context = view.context
        val imageResId = context.resources.getIdentifier(
            item.path, "drawable", context.packageName
        )
        if (imageResId != 0) {
            image.setImageResource(imageResId)
        } else {
            image.setImageResource(R.drawable.search_svgrepo_com) // fallback image
        }

        buttonAddToCart.setOnClickListener {
            val preferences = Preferences.getInstance(requireContext().dataStore)
            lifecycleScope.launch {
                val userUid = preferences.getUserUid().firstOrNull() ?: return@launch

                val itemToCart = CartModel(
                    productUid = item.uid,
                    name = item.name,
                    imageUrl = item.path,
                    price = item.price,
                    userUid = userUid
                )

                val dao = AppDatabase.getDatabase(requireContext()).cartDao()
                val cartVM = CartViewmodel(dao, preferences)
                cartVM.insert(itemToCart)

                Toast.makeText(requireContext(), "Added to Cart", Toast.LENGTH_SHORT).show()
            }
        }

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}

