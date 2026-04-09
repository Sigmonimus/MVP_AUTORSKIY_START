package com.example.mvp_autorskiy_start.ui.calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mvp_autorskiy_start.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private val prefs by lazy { requireContext().getSharedPreferences("streak_prefs", Context.MODE_PRIVATE) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val visitedDates = prefs.getStringSet("visited_dates", emptySet()) ?: emptySet()
        val calendarDays = generateCalendarDays(visitedDates)
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = CalendarAdapter(calendarDays) { day ->
            val formattedDate = try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
                val date = inputFormat.parse(day.date)
                outputFormat.format(date)
            } catch (e: Exception) {
                day.date
            }
            Toast.makeText(requireContext(), formattedDate, Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateCalendarDays(visitedDates: Set<String>): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val startDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -34) }
        val result = mutableListOf<CalendarDay>()
        val current = startDate.clone() as Calendar

        while (current <= calendar) {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(current.time)
            val dayOfMonth = current.get(Calendar.DAY_OF_MONTH)
            val visited = visitedDates.contains(dateStr)
            val isToday = dateStr == today
            result.add(CalendarDay(dateStr, dayOfMonth, visited, isToday))
            current.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }
}