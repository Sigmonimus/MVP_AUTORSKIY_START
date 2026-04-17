package com.example.mvp_autorskiy_start.ui.vocabulary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R

class WordAdapter(
    private val onDefinition: (String) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    private var words: List<String> = emptyList()

    fun submitList(list: List<String>) {
        words = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vocabulary_word, parent, false)
        return WordViewHolder(view, onDefinition, onDelete)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount() = words.size

    class WordViewHolder(
        itemView: View,
        private val onDefinition: (String) -> Unit,
        private val onDelete: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        private val btnDefinition: View = itemView.findViewById(R.id.btnDefinition)
        private val btnDelete: View = itemView.findViewById(R.id.btnDelete)

        fun bind(word: String) {
            tvWord.text = word
            btnDefinition.setOnClickListener { onDefinition(word) }
            btnDelete.setOnClickListener { onDelete(word) }
        }
    }
}