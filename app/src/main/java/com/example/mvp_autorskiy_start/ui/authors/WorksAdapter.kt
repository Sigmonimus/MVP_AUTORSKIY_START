package com.example.mvp_autorskiy_start.ui.authors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Work

class WorksAdapter(
    private val works: List<Work>,
    private val onItemClick: (Work) -> Unit
) : RecyclerView.Adapter<WorksAdapter.WorkViewHolder>() {

    class WorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvWorkTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work, parent, false)
        return WorkViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        val work = works[position]
        holder.tvTitle.text = work.title
        holder.itemView.setOnClickListener { onItemClick(work) }
    }

    override fun getItemCount() = works.size
}