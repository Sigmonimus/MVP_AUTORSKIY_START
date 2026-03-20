package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorsBinding
import com.example.mvp_autorskiy_start.data.AuthorsRepository

class AuthorsFragment : Fragment() {

    private var _binding: FragmentAuthorsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorsBinding.inflate(inflater, container, false)
        return binding.root
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}