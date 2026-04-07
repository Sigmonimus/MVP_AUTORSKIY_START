package com.example.mvp_autorskiy_start.ui.favorites

import android.os.Bundle
import android.view.View
import com.example.mvp_autorskiy_start.databinding.FragmentFavoritesBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(FragmentFavoritesBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = FavoritesPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Аргументы"
                1 -> "Мои сочинения"
                else -> ""
            }
        }.attach()
    }
}