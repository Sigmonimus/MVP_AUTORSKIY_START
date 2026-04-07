package com.example.mvp_autorskiy_start.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentHomeBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.loadData(requireContext())

        homeViewModel.randomQuote.observe(viewLifecycleOwner) { quote ->
            binding.quoteText.text = "«${quote.text}»"
            binding.quoteAuthor.text = "— ${quote.author}"
        }
        homeViewModel.randomTip.observe(viewLifecycleOwner) { tip ->
            binding.tipText.text = tip
        }

        loadUserInfo()
        loadRandomIllustration()
        setupSettingsClick()
    }

    private fun loadUserInfo() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Гость")
        binding.greetingText.text = "С возвращением, \n$userName!"
    }

    private fun loadRandomIllustration() {
        val names = resources.getStringArray(R.array.illustration_names)
        if (names.isEmpty()) {
            binding.randomIllustration.visibility = View.GONE
            return
        }
        val randomName = names.random()
        val resId = resources.getIdentifier(randomName, "drawable", requireContext().packageName)
        if (resId != 0) {
            binding.randomIllustration.setImageResource(resId)
            binding.randomIllustration.visibility = View.VISIBLE
        } else {
            binding.randomIllustration.visibility = View.GONE
        }
    }

    private fun setupSettingsClick() {
        binding.settingsIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}