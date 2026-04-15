package com.example.mvp_autorskiy_start.ui.arguments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.databinding.FragmentArgumentsLibraryBinding
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.data.repository.ArgumentsRepository
import com.example.mvp_autorskiy_start.data.models.Category
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ArgumentsLibraryFragment : BaseFragment<FragmentArgumentsLibraryBinding>(FragmentArgumentsLibraryBinding::inflate) {

    private lateinit var allCategories: List<Category>
    private lateinit var allArguments: List<Argument>
    private var currentCategoryId: Int = -1
    private var currentQuery: String = ""

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
        lifecycleScope.launch {
            var filtered = allArguments
            if (currentCategoryId != -1) {
                filtered = filtered.filter { it.categoryIds.contains(currentCategoryId) }
            }
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
                onItemClick = { argument -> showArgumentDialog(argument) },
                onFavoriteClick = { argument, isFavorite ->
                    lifecycleScope.launch {
                        if (isFavorite) {
                            FavoritesRepository.addFavoriteArgument(argument.id)
                        } else {
                            FavoritesRepository.removeFavoriteArgument(argument.id)
                        }
                        updateArgumentsList()
                    }
                }
            )
            binding.rvArguments.adapter = adapter
        }
    }

    private fun showArgumentDialog(argument: Argument) {
        val dialog = ArgumentReaderDialogFragment.newInstance(argument)
        dialog.show(parentFragmentManager, ArgumentReaderDialogFragment.TAG)
    }
}