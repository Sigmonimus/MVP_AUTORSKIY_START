package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.View
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorDetailBinding
import com.example.mvp_autorskiy_start.data.models.Author
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class AuthorDetailFragment : BaseFragment<FragmentAuthorDetailBinding>(FragmentAuthorDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val author = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("author", Author::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Author>("author")
        } ?: return

        binding.ivAuthorImage.setImageResource(author.imageRes)
        binding.tvAuthorName.text = author.name

        val pagerAdapter = AuthorPagerAdapter(requireActivity(), author)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Биография"
                1 -> "Произведения (${author.works.size})"
                2 -> "Аргументы (${author.works.sumOf { it.arguments.size }})"
                else -> ""
            }
        }.attach()
    }

    companion object {
        fun newInstance(author: Author): AuthorDetailFragment {
            val fragment = AuthorDetailFragment()
            val args = Bundle()
            args.putParcelable("author", author)
            fragment.arguments = args
            return fragment
        }
    }
}