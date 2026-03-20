package com.example.mvp_autorskiy_start.ui.favorites

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FavoritesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteArgumentsFragment()
            1 -> FavoriteEssaysFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}