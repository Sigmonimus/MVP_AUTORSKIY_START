package com.example.mvp_autorskiy_start.ui.arguments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.databinding.FragmentArgumentsLibraryBinding
import com.example.mvp_autorskiy_start.data.Argument
import com.example.mvp_autorskiy_start.data.ArgumentsRepository
import com.example.mvp_autorskiy_start.data.Category
import com.example.mvp_autorskiy_start.data.FavoritesRepository
import com.google.android.material.chip.Chip

class ArgumentsLibraryFragment : Fragment() {

    private var _binding: FragmentArgumentsLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var allCategories: List<Category>
    private lateinit var allArguments: List<Argument>
    private var currentCategoryId: Int = -1 // -1 означает все
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArgumentsLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        setupSearch()
        setupChips()
        setupRecyclerView()
    }

    private fun loadData() {
        val (categories, arguments) = ArgumentsRepository.loadData(requireContext())
        allCategories = categories
        allArguments = arguments
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query?.trim() ?: ""
                updateArgumentsList()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText?.trim() ?: ""
                updateArgumentsList()
                return true
            }
        })
    }

    private fun setupChips() {
        binding.chipGroup.removeAllViews()
        // Добавляем чип "Все"
        val allChip = Chip(requireContext()).apply {
            text = "Все"
            isCheckable = true
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentCategoryId = -1
                    updateArgumentsList()
                }
            }
        }
        binding.chipGroup.addView(allChip)

        // Добавляем чипы для каждой категории
        allCategories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = category.name
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        currentCategoryId = category.id
                        updateArgumentsList()
                    }
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        binding.rvArguments.layoutManager = LinearLayoutManager(requireContext())
        updateArgumentsList()
    }

    private fun updateArgumentsList() {
        var filtered = allArguments

        // Фильтр по категории
        if (currentCategoryId != -1) {
            filtered = filtered.filter { it.categoryIds.contains(currentCategoryId) }
        }

        // Фильтр по поисковому запросу
        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(currentQuery, ignoreCase = true) ||
                        it.author.contains(currentQuery, ignoreCase = true) ||
                        it.workTitle.contains(currentQuery, ignoreCase = true)
            }
        }

        val favoriteIds = FavoritesRepository.getFavoriteArguments()
        val adapter = ArgumentsAdapter(
            arguments = filtered,
            favoriteIds = favoriteIds,
            onItemClick = { argument ->
                // Показать детали аргумента (можно диалог или новый экран)
                showArgumentDialog(argument)
            },
            onFavoriteClick = { argument, isFavorite ->
                if (isFavorite) {
                    FavoritesRepository.addFavoriteArgument(argument.id)
                } else {
                    FavoritesRepository.removeFavoriteArgument(argument.id)
                }
                updateArgumentsList() // обновить список (чтобы обновились звёздочки)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}