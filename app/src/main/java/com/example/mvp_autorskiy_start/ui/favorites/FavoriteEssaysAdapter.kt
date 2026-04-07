package com.example.mvp_autorskiy_start.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.SavedEssay
import java.text.SimpleDateFormat
import java.util.*

class FavoriteEssaysAdapter(
    private val essays: List<SavedEssay>,
    private val onItemClick: (SavedEssay) -> Unit,
    private val onDeleteClick: (SavedEssay) -> Unit
) : RecyclerView.Adapter<FavoriteEssaysAdapter.EssayViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    class EssayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvEssayTitle)
        val tvTheme: TextView = itemView.findViewById(R.id.tvEssayTheme)
        val tvDate: TextView = itemView.findViewById(R.id.tvEssayDate)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteEssay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_essay, parent, false)
        return EssayViewHolder(view)
    }

    override fun onBindViewHolder(holder: EssayViewHolder, position: Int) {
        val essay = essays[position]
        holder.tvTitle.text = essay.title
        holder.tvTheme.text = essay.theme.ifEmpty { "Без темы" }
        holder.tvDate.text = dateFormat.format(Date(essay.date))
        holder.itemView.setOnClickListener { onItemClick(essay) }
        holder.btnDelete.setOnClickListener { onDeleteClick(essay) }
    }

    override fun getItemCount() = essays.size
}