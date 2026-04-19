package com.example.mvp_autorskiy_start.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.HomeDataRepository
import com.example.mvp_autorskiy_start.databinding.FragmentHomeBinding
import com.example.mvp_autorskiy_start.ui.calendar.CalendarFragment
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        loadVisitedDates()

        homeViewModel.quoteText.observe(viewLifecycleOwner) { text ->
            binding.quoteText.text = text
        }
        homeViewModel.quoteAuthor.observe(viewLifecycleOwner) { author ->
            binding.quoteAuthor.text = author
        }
        homeViewModel.tip.observe(viewLifecycleOwner) { tip ->
            binding.tipText.text = tip
        }

        homeViewModel.showFireworks.observe(viewLifecycleOwner) { show ->
            if (show) showFireworks()
        }

        loadUserInfo()
        loadRandomIllustration()
        setupSettingsClick()
        updateStreak()
        setupStreakClickListener()
        startEntranceAnimation()
    }

    override fun onResume() {
        super.onResume()
        loadVisitedDates()
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter()
        binding.activityCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.activityCalendar.adapter = calendarAdapter
    }

    private fun loadVisitedDates() {
        lifecycleScope.launch {
            val visitedDates = HomeDataRepository.getVisitedDates()
            calendarAdapter.setVisitedDates(visitedDates)
        }
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

    private fun showFireworks() {
        val container = binding.fireworksContainer
        container.visibility = View.VISIBLE

        for (i in 0..20) {
            val circle = View(requireContext())
            circle.setBackgroundResource(R.drawable.circle_firework)
            container.addView(circle, 20, 20)

            val startX = container.width / 2f
            val startY = container.height / 2f
            circle.x = startX
            circle.y = startY

            val endX = startX + (Math.random() * 400 - 200).toFloat()
            val endY = startY - 300 + (Math.random() * 200).toFloat()

            val animatorX = ObjectAnimator.ofFloat(circle, "x", startX, endX)
            val animatorY = ObjectAnimator.ofFloat(circle, "y", startY, endY)
            val alphaAnimator = ObjectAnimator.ofFloat(circle, "alpha", 1f, 0f)

            val set = AnimatorSet()
            set.playTogether(animatorX, animatorY, alphaAnimator)
            set.duration = 1500
            set.interpolator = AccelerateDecelerateInterpolator()
            set.start()
        }

        container.postDelayed({
            container.visibility = View.GONE
            container.removeAllViews()
        }, 2000)
    }
}

// Адаптер и модель вынесены из класса для избежания проблем с видимостью
private data class CalendarDay(val date: String, val displayText: String)

private class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val days = mutableListOf<CalendarDay>()
    private var visitedDates: Set<String> = emptySet()

    init {
        generateDays()
    }

    fun setVisitedDates(visited: Set<String>) {
        visitedDates = visited
        notifyDataSetChanged()
    }

    private fun generateDays() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -34)
        days.clear()
        for (i in 0..34) {
            val date = dateFormat.format(calendar.time)
            days.add(CalendarDay(date, calendar.get(Calendar.DAY_OF_MONTH).toString()))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day, visitedDates.contains(day.date))
    }

    override fun getItemCount(): Int = days.size

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)

        fun bind(day: CalendarDay, isVisited: Boolean) {
            tvDay.text = day.displayText
            val today = dateFormat.format(Date())
            val backgroundColor = when {
                day.date == today -> R.color.calendar_today
                isVisited -> R.color.calendar_visited
                else -> R.color.calendar_default
            }
            tvDay.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        }
    }
}