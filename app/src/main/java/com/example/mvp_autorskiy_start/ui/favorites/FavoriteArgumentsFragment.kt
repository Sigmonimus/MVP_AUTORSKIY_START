package com.example.mvp_autorskiy_start.ui.favorites

import android.os.Bundle
import android.view.View
import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.ArgumentsRepository
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.databinding.FragmentFavoriteArgumentsBinding
import com.example.mvp_autorskiy_start.ui.arguments.ArgumentsAdapter
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class FavoriteArgumentsFragment : BaseFragment<FragmentFavoriteArgumentsBinding>(FragmentFavoriteArgumentsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val allArguments = ArgumentsRepository.getArguments(requireContext())
        val favoriteIds = FavoritesRepository.getFavoriteArguments()
        val favoriteArguments = allArguments.filter { it.id in favoriteIds }

        if (favoriteArguments.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvArguments.visibility = View.GONE
            binding.tvCount.text = getString(R.string.arguments_count, 0)
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvArguments.visibility = View.VISIBLE
            binding.tvCount.text = getString(R.string.arguments_count, favoriteArguments.size)

            binding.rvArguments.layoutManager = LinearLayoutManager(requireContext())
            val adapter = ArgumentsAdapter(
                arguments = favoriteArguments,
                favoriteIds = favoriteIds,
                onItemClick = { argument -> showArgumentDialog(argument) },
                onFavoriteClick = { argument, isFavorite ->
                    if (!isFavorite) {
                        FavoritesRepository.removeFavoriteArgument(argument.id)
                        loadData()
                    }
                }
            )
            binding.rvArguments.adapter = adapter
        }
    }

    private fun showArgumentDialog(argument: Argument) {
        val fullText = argument.fullText.trim()
        val isPlaceholder = fullText.startsWith("Полный текст") || fullText == "..." || fullText == "Полный текст..."
        val message = if (fullText.isNotBlank() && !isPlaceholder) fullText else argument.description
        AlertDialog.Builder(requireContext())
            .setTitle(argument.title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}