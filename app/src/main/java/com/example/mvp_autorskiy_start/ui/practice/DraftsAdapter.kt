package com.example.mvp_autorskiy_start.ui.practice

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.PracticeDraft

class DraftsAdapter(
    private val drafts: List<PracticeDraft>,
    private val onItemClick: (PracticeDraft) -> Unit
) : RecyclerView.Adapter<DraftsAdapter.ViewHolder>() {

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvDraftTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvDraftDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_practice_draft, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val draft = drafts[position]
        holder.tvTitle.text = draft.title
        holder.tvDate.text = android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", draft.lastModified).toString()
        holder.itemView.setOnClickListener { onItemClick(draft) }
    }

    override fun getItemCount() = drafts.size
}