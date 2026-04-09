package com.example.mvp_autorskiy_start.ui.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R

data class CalendarDay(
    val date: String,
    val dayOfMonth: Int,
    val visited: Boolean,
    val isToday: Boolean
)

class CalendarAdapter(
    private val days: List<CalendarDay>,
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val root: View = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.tvDay.text = day.dayOfMonth.toString()
        holder.root.setBackgroundColor(
            if (day.isToday) Color.parseColor("#FF9800")
            else if (day.visited) Color.parseColor("#4CAF50")
            else Color.parseColor("#E0E0E0")
        )
        holder.root.setOnClickListener { onDayClick(day) }
    }

    override fun getItemCount() = days.size
}