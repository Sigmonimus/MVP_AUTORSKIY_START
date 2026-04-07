package com.example.mvp_autorskiy_start.ui.arguments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.databinding.FragmentWorkArgumentsBinding
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class ArgumentsListFragment : BaseFragment<FragmentWorkArgumentsBinding>(FragmentWorkArgumentsBinding::inflate) {

    private var argumentsList: List<Argument> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        argumentsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("arguments", Argument::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList<Argument>("arguments") ?: emptyList()
        }

        binding.rvArguments.layoutManager = LinearLayoutManager(requireContext())
        updateAdapter()
    }

    private fun updateAdapter() {
        val favoriteIds = FavoritesRepository.getFavoriteArguments()
        val adapter = ArgumentsAdapter(
            arguments = argumentsList,
            favoriteIds = favoriteIds,
            onItemClick = { argument -> showArgumentDialog(argument) },
            onFavoriteClick = { argument, isFavorite ->
                if (isFavorite) {
                    FavoritesRepository.addFavoriteArgument(argument.id)
                } else {
                    FavoritesRepository.removeFavoriteArgument(argument.id)
                }
                updateAdapter()
            }
        )
        binding.rvArguments.adapter = adapter
    }

    private fun showArgumentDialog(argument: Argument) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(argument.title)
            .setMessage(argument.description)
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        fun newInstance(arguments: List<Argument>): ArgumentsListFragment {
            val fragment = ArgumentsListFragment()
            val args = Bundle()
            args.putParcelableArrayList("arguments", ArrayList(arguments))
            fragment.arguments = args
            return fragment
        }
    }
}