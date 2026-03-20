package com.example.mvp_autorskiy_start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.data.ArgumentsRepository
import com.example.mvp_autorskiy_start.data.FavoritesRepository
import com.example.mvp_autorskiy_start.databinding.FragmentLibraryBinding
import com.example.mvp_autorskiy_start.ui.arguments.ArgumentsAdapter   // Изменён импорт

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allArguments = ArgumentsRepository.getArguments(requireContext())
        val favoriteIds = FavoritesRepository.getFavoriteArguments()

        binding.rvArguments.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ArgumentsAdapter(   // Изменено имя адаптера
            arguments = allArguments,
            favoriteIds = favoriteIds,
            onItemClick = { argument ->
                Toast.makeText(requireContext(), argument.title, Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { argument, isFavorite ->
                if (isFavorite) {
                    FavoritesRepository.addFavoriteArgument(argument.id)
                } else {
                    FavoritesRepository.removeFavoriteArgument(argument.id)
                }
                refreshAdapter()   // обновляем список
            }
        )
        binding.rvArguments.adapter = adapter
    }

    private fun refreshAdapter() {
        val allArguments = ArgumentsRepository.getArguments(requireContext())
        val favoriteIds = FavoritesRepository.getFavoriteArguments()
        val adapter = ArgumentsAdapter(   // Исправлено имя адаптера
            arguments = allArguments,
            favoriteIds = favoriteIds,
            onItemClick = { argument ->
                Toast.makeText(requireContext(), argument.title, Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { argument, isFavorite ->
                if (isFavorite) {
                    FavoritesRepository.addFavoriteArgument(argument.id)
                } else {
                    FavoritesRepository.removeFavoriteArgument(argument.id)
                }
                refreshAdapter()
            }
        )
        binding.rvArguments.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}