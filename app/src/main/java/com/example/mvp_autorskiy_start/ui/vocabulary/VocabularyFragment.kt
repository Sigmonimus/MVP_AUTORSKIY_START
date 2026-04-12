package com.example.mvp_autorskiy_start.ui.vocabulary

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.databinding.FragmentVocabularyBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import kotlinx.coroutines.launch

class VocabularyFragment : BaseFragment<FragmentVocabularyBinding>(FragmentVocabularyBinding::inflate) {

    private lateinit var adapter: WordAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WordAdapter { word ->
            removeWord(word)
        }

        binding.rvWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWords.adapter = adapter

        loadWords()
    }

    private fun loadWords() {
        lifecycleScope.launch {
            val words = App.dataStoreManager.getSavedWords().toList().sorted()
            adapter.submitList(words)
            binding.tvEmpty.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun removeWord(word: String) {
        lifecycleScope.launch {
            val current = App.dataStoreManager.getSavedWords().toMutableSet()
            current.remove(word)
            App.dataStoreManager.setSavedWords(current)
            loadWords()
        }
    }
}