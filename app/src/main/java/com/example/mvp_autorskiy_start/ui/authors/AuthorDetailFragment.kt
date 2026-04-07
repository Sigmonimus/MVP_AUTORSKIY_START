package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorDetailBinding
import com.example.mvp_autorskiy_start.data.models.Author
import com.google.android.material.tabs.TabLayoutMediator

class AuthorDetailFragment : Fragment() {

    private var _binding: FragmentAuthorDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val author = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("author", Author::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Author>("author")
        } ?: return

        binding.ivAuthorImage.setImageResource(author.imageRes)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.collapsingToolbar.title = shortenName(author.name)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun shortenName(fullName: String): String {
        val parts = fullName.split(" ")
        return when (parts.size) {
            1 -> fullName
            2 -> "${parts[0].first()}. ${parts[1]}"
            3 -> "${parts[0].first()}.${parts[1].first()}. ${parts[2]}"
            else -> fullName
        }
    }
}