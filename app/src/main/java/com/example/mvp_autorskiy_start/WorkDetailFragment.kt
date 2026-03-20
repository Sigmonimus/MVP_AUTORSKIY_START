package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.databinding.FragmentWorkDetailBinding
import com.example.mvp_autorskiy_start.data.Work
import com.google.android.material.tabs.TabLayoutMediator

class WorkDetailFragment : Fragment() {

    private var _binding: FragmentWorkDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val work = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("work", Work::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Work>("work")
        } ?: return

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = work.title

        val pagerAdapter = WorkPagerAdapter(requireActivity(), work)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Краткое"
                1 -> "Полный текст"
                2 -> "Аргументы (${work.arguments.size})"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(work: Work): WorkDetailFragment {
            val fragment = WorkDetailFragment()
            val args = Bundle()
            args.putParcelable("work", work)
            fragment.arguments = args
            return fragment
        }
    }
}