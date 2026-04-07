package com.example.mvp_autorskiy_start.ui.favorites

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.AlertDialog
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.databinding.FragmentFavoriteEssaysBinding
import com.example.mvp_autorskiy_start.data.models.SavedEssay
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class FavoriteEssaysFragment : BaseFragment<FragmentFavoriteEssaysBinding>(FragmentFavoriteEssaysBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEssays()
    }

    override fun onResume() {
        super.onResume()
        loadEssays()
    }

    private fun loadEssays() {
        val essays = FavoritesRepository.getSavedEssays()
        if (essays.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvEssays.visibility = View.GONE
            binding.tvCount.text = getString(R.string.essays_count, 0)
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvEssays.visibility = View.VISIBLE
            binding.tvCount.text = getString(R.string.essays_count, essays.size)

            binding.rvEssays.layoutManager = LinearLayoutManager(requireContext())
            val adapter = FavoriteEssaysAdapter(
                essays = essays,
                onItemClick = { essay -> showEssayDialog(essay) },
                onDeleteClick = { essay -> deleteEssay(essay) }
            )
            binding.rvEssays.adapter = adapter
        }
    }

    private fun deleteEssay(essay: SavedEssay) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить сочинение")
            .setMessage("Вы уверены, что хотите удалить «${essay.title}» из избранного?")
            .setPositiveButton("Удалить") { _, _ ->
                FavoritesRepository.removeSavedEssay(essay.id)
                loadEssays()
                Toast.makeText(requireContext(), "Сочинение удалено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEssayDialog(essay: SavedEssay) {
        val message = """
        Тема: ${essay.theme}
        
        Текст:
        ${essay.content}
    """.trimIndent()
        AlertDialog.Builder(requireContext())
            .setTitle(essay.title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}