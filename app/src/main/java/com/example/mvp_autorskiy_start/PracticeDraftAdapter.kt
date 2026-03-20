package com.example.mvp_autorskiy_start.ui.practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import java.text.SimpleDateFormat
import java.util.*

class PracticeDraftAdapter(
    private val drafts: List<PracticeDraft>,
    private val onItemClick: (PracticeDraft) -> Unit,
    private val onDeleteClick: (PracticeDraft) -> Unit
) : RecyclerView.Adapter<PracticeDraftAdapter.DraftViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvDraftTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvDraftDate)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteDraft)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_practice_draft, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val draft = drafts[position]
        holder.tvTitle.text = draft.title
        holder.tvDate.text = dateFormat.format(Date(draft.lastModified))
        holder.itemView.setOnClickListener { onItemClick(draft) }
        holder.btnDelete.setOnClickListener { onDeleteClick(draft) }
    }

    override fun getItemCount() = drafts.size
}