package com.wardrobe.armoire.ui.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.wardrobe.armoire.R
import com.wardrobe.armoire.model.shop.ShopModel

class DetailShopFragment : Fragment() {

    private lateinit var item: ShopModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            item = it.getParcelable("shopItem")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_detail_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val image = view.findViewById<ImageView>(R.id.imageItem)
        val name = view.findViewById<TextView>(R.id.textName)
        val price = view.findViewById<TextView>(R.id.textPrice)
        val buttonAddToCart = view.findViewById<Button>(R.id.buttonAddToCart)

        name.text = item.name
        price.text = "Rp${item.price}"
        // image.load(item.path)

        buttonAddToCart.setOnClickListener {
            // Add to cart logic here
        }
    }
}

