package com.example.mvp_autorskiy_start.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.HomeDataRepository
import com.example.mvp_autorskiy_start.databinding.FragmentHomeBinding
import com.example.mvp_autorskiy_start.ui.calendar.CalendarFragment
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment
import kotlinx.coroutines.launch

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
        updateStreak()
        setupStreakClickListener()
        startEntranceAnimation()
    }

    private fun loadUserInfo() {
        lifecycleScope.launch {
            val userName = App.dataStoreManager.getUserName()
            val displayName = if (userName.isBlank()) "Гость" else userName
            binding.greetingText.text = "С возвращением, \n$displayName!"
        }
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

    private fun updateStreak() {
        lifecycleScope.launch {
            HomeDataRepository.updateStreak()
            val current = HomeDataRepository.getCurrentStreak()
            val best = HomeDataRepository.getBestStreak()
            binding.currentStreak.text = "🔥 $current"
            binding.bestStreak.text = "🏆 $best"
        }
    }

    private fun setupStreakClickListener() {
        var clickCount = 0
        val handler = Handler(Looper.getMainLooper())
        binding.currentStreak.setOnClickListener {
            clickCount++
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                clickCount = 0
            }, 500)
            if (clickCount == 3) {
                clickCount = 0
                val fragment = CalendarFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun startEntranceAnimation() {
        val content = binding.root
        content.alpha = 0f
        content.visibility = View.VISIBLE
        content.animate().alpha(1f).setDuration(500).start()
    }
}