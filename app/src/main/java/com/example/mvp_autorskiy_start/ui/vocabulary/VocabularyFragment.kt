package com.example.mvp_autorskiy_start.ui.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.WordRepository
import com.example.mvp_autorskiy_start.data.repository.SavedWord
import com.example.mvp_autorskiy_start.databinding.FragmentVocabularyBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class VocabularyFragment : BaseFragment<FragmentVocabularyBinding>(FragmentVocabularyBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWords()
    }

    override fun onResume() {
        super.onResume()
        loadWords()
    }

    private fun loadWords() {
        val words = WordRepository.getWords()
        if (words.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvWords.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvWords.visibility = View.VISIBLE
            binding.rvWords.layoutManager = LinearLayoutManager(requireContext())
            binding.rvWords.adapter = WordAdapter(words) { word ->
                showDeleteDialog(word)
            }
        }
    }

    private fun showDeleteDialog(word: SavedWord) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить слово")
            .setMessage("Удалить «${word.word}» из словаря?")
            .setPositiveButton("Удалить") { _, _ ->
                WordRepository.removeWord(word)
                loadWords()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    inner class WordAdapter(
        private val words: List<SavedWord>,
        private val onDelete: (SavedWord) -> Unit
    ) : RecyclerView.Adapter<WordAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvWord: TextView = itemView.findViewById(R.id.tvWord)
            val tvSource: TextView = itemView.findViewById(R.id.tvSource)
            val btnDelete: View = itemView.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vocabulary_word, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val word = words[position]
            holder.tvWord.text = word.word
            holder.tvSource.text = "${word.workTitle} (id: ${word.workId})"
            holder.btnDelete.setOnClickListener { onDelete(word) }
        }

        override fun getItemCount() = words.size
    }
}