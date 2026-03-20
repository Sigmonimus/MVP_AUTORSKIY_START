package com.example.mvp_autorskiy_start.ui.arguments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.Argument   // <-- добавлен импорт

class ArgumentAdapter(
    private val arguments: List<Argument>,
    private val favoriteIds: Set<Int>,
    private val onItemClick: (Argument) -> Unit,
    private val onFavoriteClick: (Argument, Boolean) -> Unit
) : RecyclerView.Adapter<ArgumentAdapter.ArgumentViewHolder>() {

    class ArgumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivArgumentImage)   // если есть
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvWorkAuthor: TextView = itemView.findViewById(R.id.tvWorkAuthor)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArgumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_argument, parent, false)
        return ArgumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArgumentViewHolder, position: Int) {
        val argument = arguments[position]
        holder.tvTitle.text = argument.title
        holder.tvWorkAuthor.text = "${argument.workTitle} – ${argument.author}"
        holder.tvDescription.text = argument.description

        // Устанавливаем изображение, если поле imageRes есть (в модели Argument должно быть)
        // Если у вас нет поля imageRes, уберите эту часть
        try {
            if (argument.imageRes != 0) {
                holder.ivImage.setImageResource(argument.imageRes)
                holder.ivImage.visibility = View.VISIBLE
            } else {
                holder.ivImage.setImageResource(R.drawable.ic_default_argument)
                holder.ivImage.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            // Если поле imageRes отсутствует, просто скрываем ImageView
            holder.ivImage.visibility = View.GONE
        }

        val isFavorite = favoriteIds.contains(argument.id)
        holder.ivFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border
        )

        holder.itemView.setOnClickListener { onItemClick(argument) }
        holder.ivFavorite.setOnClickListener {
            onFavoriteClick(argument, !isFavorite)
        }
    }

    override fun getItemCount() = arguments.size
}