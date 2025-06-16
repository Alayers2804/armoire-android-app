package com.wardrobe.armoire.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrderPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val statuses = listOf("Unpaid", "Packing", "On delivery", "Rate")

    override fun getItemCount(): Int = statuses.size

    override fun createFragment(position: Int): Fragment {
        return OrderStatusFragment.newInstance(statuses[position])
    }
}