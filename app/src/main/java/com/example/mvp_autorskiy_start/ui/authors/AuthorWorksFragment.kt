package com.example.mvp_autorskiy_start.ui.authors

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorWorksBinding
import com.example.mvp_autorskiy_start.data.models.Work

class AuthorWorksFragment : Fragment() {

    private var _binding: FragmentAuthorWorksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorWorksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val works = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("works", Work::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList<Work>("works") ?: emptyList()
        }

        // Логирование для отладки
        Log.d("AuthorWorks", "Received works count: ${works.size}")
        works.forEach { Log.d("AuthorWorks", "Work: ${it.title}") }

        // Важно: устанавливаем LayoutManager
        binding.rvWorks.layoutManager = LinearLayoutManager(requireContext())

        val adapter = WorksAdapter(works) { work ->
            val fragment = WorkDetailFragment.newInstance(work)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvWorks.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(works: List<Work>): AuthorWorksFragment {
            val fragment = AuthorWorksFragment()
            val args = Bundle()
            args.putParcelableArrayList("works", ArrayList(works))
            fragment.arguments = args
            return fragment
        }
    }
}