package com.example.mvp_autorskiy_start.ui.authors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Author

class AuthorsAdapter(
    private val authors: List<Author>,
    private val onItemClick: (Author) -> Unit
) : RecyclerView.Adapter<AuthorsAdapter.AuthorViewHolder>() {

    class AuthorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivAuthorImage)
        val tvName: TextView = itemView.findViewById(R.id.tvAuthorName)
        val tvYears: TextView = itemView.findViewById(R.id.tvAuthorYears)
        val tvBio: TextView = itemView.findViewById(R.id.tvAuthorBio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author, parent, false)
        return AuthorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        val author = authors[position]
        holder.tvName.text = author.name
        holder.tvYears.text = author.years
        holder.tvBio.text = author.bio
        holder.ivImage.setImageResource(author.imageRes)
        holder.itemView.setOnClickListener { onItemClick(author) }
    }

    override fun getItemCount() = authors.size
}