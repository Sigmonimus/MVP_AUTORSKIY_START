package com.example.mvp_autorskiy_start.ui.arguments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.ArgumentsRepository
import com.example.mvp_autorskiy_start.databinding.FragmentCategoriesBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>(FragmentCategoriesBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (categories, allArguments) = ArgumentsRepository.loadData(requireContext())

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = CategoriesAdapter(categories, allArguments) { category ->
            val filteredArguments = allArguments.filter { it.categoryIds.contains(category.id) }
            val fragment = ArgumentsListFragment.newInstance(filteredArguments)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvCategories.adapter = adapter
    }
}