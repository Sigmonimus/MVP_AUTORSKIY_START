package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorDetailBinding
import com.example.mvp_autorskiy_start.data.Author
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

        // Устанавливаем изображение
        binding.ivAuthorImage.setImageResource(author.imageRes)

        // Настраиваем тулбар
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.collapsingToolbar.title = author.name

        // Проверяем, есть ли в разметке ViewPager2 и TabLayout
        try {
            // Настройка ViewPager и TabLayout
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
        } catch (e: Exception) {
            Log.e("AuthorDetail", "ViewPager or TabLayout not found in layout. Please check fragment_author_detail.xml")
            // Если ViewPager отсутствует, можно отобразить биографию в contentContainer
            val bioFragment = AuthorBioFragment.newInstance(author.bio)
            childFragmentManager.beginTransaction()
                .replace(R.id.contentContainer, bioFragment)
                .commit()
        }
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
}