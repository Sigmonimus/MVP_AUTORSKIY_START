package com.example.mvp_autorskiy_start.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentHomeBinding
import com.example.mvp_autorskiy_start.ui.calendar.CalendarFragment
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()
    private val prefs by lazy { requireContext().getSharedPreferences("streak_prefs", Context.MODE_PRIVATE) }
    private val visitedDatesSet: MutableSet<String> by lazy {
        prefs.getStringSet("visited_dates", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    }

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
        startEntranceAnimation()
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

    private fun updateStreak() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        if (!visitedDatesSet.contains(today)) {
            visitedDatesSet.add(today)
            prefs.edit().putStringSet("visited_dates", visitedDatesSet).apply()
        }

        val lastOpenDate = prefs.getString("last_open_date", null)
        var currentStreak = prefs.getInt("current_streak", 0)
        var bestStreak = prefs.getInt("best_streak", 0)
        var streakIncreased = false

        if (lastOpenDate == null) {
            currentStreak = 1
            streakIncreased = true
        } else {
            val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
            when (lastOpenDate) {
                today -> { }
                yesterday -> {
                    currentStreak++
                    streakIncreased = true
                }
                else -> {
                    currentStreak = 1
                    streakIncreased = true
                }
            }
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
            streakIncreased = true
        }

        prefs.edit().putString("last_open_date", today).apply()
        prefs.edit().putInt("current_streak", currentStreak).apply()
        prefs.edit().putInt("best_streak", bestStreak).apply()

        binding.currentStreak.text = "🔥 $currentStreak"
        binding.bestStreak.text = "🏆 $bestStreak"

        if (streakIncreased) {
            showFireworks()
        }

        setupStreakClickListener()
    }

    private fun setupStreakClickListener() {
        var clickCount = 0
        val handler = Handler(Looper.getMainLooper())
        var resetRunnable: Runnable? = null

        binding.currentStreak.setOnClickListener {
            clickCount++
            resetRunnable?.let { handler.removeCallbacks(it) }
            resetRunnable = Runnable {
                clickCount = 0
            }
            handler.postDelayed(resetRunnable!!, 500)

            if (clickCount == 3) {
                clickCount = 0
                resetRunnable?.let { handler.removeCallbacks(it) }
                val fragment = CalendarFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun showFireworks() {
        val container = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            isClickable = false
            isFocusable = false
        }

        (requireActivity() as AppCompatActivity).addContentView(container, container.layoutParams)

        val colors = listOf(
            android.graphics.Color.RED,
            android.graphics.Color.YELLOW,
            android.graphics.Color.GREEN,
            android.graphics.Color.BLUE,
            android.graphics.Color.CYAN,
            android.graphics.Color.MAGENTA,
            android.graphics.Color.parseColor("#FFA500")
        )

        val random = java.util.Random()
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val startY = screenHeight - 200

        fun launchWave() {
            repeat(60) {
                val circle = View(requireContext()).apply {
                    val size = 15 + random.nextInt(30)
                    layoutParams = FrameLayout.LayoutParams(size, size).apply {
                        x = random.nextInt(screenWidth - size).toFloat()
                        y = startY.toFloat()
                    }
                    background = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.OVAL
                        setColor(colors[random.nextInt(colors.size)])
                    }
                    alpha = 1f
                }
                container.addView(circle)

                val translationY = ObjectAnimator.ofFloat(circle, View.TRANSLATION_Y, -screenHeight.toFloat())
                translationY.duration = 2000 + random.nextInt(1000).toLong()
                translationY.interpolator = AccelerateInterpolator()

                val alpha = ObjectAnimator.ofFloat(circle, View.ALPHA, 1f, 0f)
                alpha.duration = 1800 + random.nextInt(700).toLong()
                alpha.startDelay = 300

                val rotation = ObjectAnimator.ofFloat(circle, View.ROTATION, 0f, 360f * (random.nextInt(3) + 1))
                rotation.duration = translationY.duration
                rotation.interpolator = DecelerateInterpolator()

                val set = AnimatorSet()
                set.playTogether(translationY, alpha, rotation)
                set.start()

                set.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        container.removeView(circle)
                    }
                })
            }
        }

        launchWave()
        Handler(Looper.getMainLooper()).postDelayed({ launchWave() }, 1000)
        Handler(Looper.getMainLooper()).postDelayed({ launchWave() }, 2000)

        Handler(Looper.getMainLooper()).postDelayed({
            (container.parent as? ViewGroup)?.removeView(container)
        }, 7000)
    }

    private fun startEntranceAnimation() {
        val content = binding.root
        content.alpha = 0f
        content.visibility = View.VISIBLE

        val fadeIn = ObjectAnimator.ofFloat(content, View.ALPHA, 0f, 1f).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    content.alpha = 0f
                }
            })
        }
        fadeIn.start()
    }
}