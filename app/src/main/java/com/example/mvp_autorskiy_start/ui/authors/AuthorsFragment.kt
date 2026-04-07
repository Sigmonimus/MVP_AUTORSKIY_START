package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorsBinding
import com.example.mvp_autorskiy_start.data.repository.AuthorsRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class AuthorsFragment : BaseFragment<FragmentAuthorsBinding>(FragmentAuthorsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authors = AuthorsRepository.getAuthors(requireContext())
        binding.rvAuthors.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AuthorsAdapter(authors) { author ->
            val fragment = AuthorDetailFragment.newInstance(author)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvAuthors.adapter = adapter
    }
}