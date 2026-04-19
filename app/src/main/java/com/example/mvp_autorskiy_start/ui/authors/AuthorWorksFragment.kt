package com.example.mvp_autorskiy_start.ui.authors

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorWorksBinding
import com.example.mvp_autorskiy_start.data.models.Work
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class AuthorWorksFragment : BaseFragment<FragmentAuthorWorksBinding>(FragmentAuthorWorksBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val works = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("works", Work::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList<Work>("works") ?: emptyList()
        }

        Log.d("AuthorWorks", "Received works count: ${works.size}")
        works.forEach { Log.d("AuthorWorks", "Work: ${it.title}") }

        binding.rvWorks.layoutManager = LinearLayoutManager(requireContext())

        val adapter = WorksAdapter(works) { work ->
            val fragment = WorkDetailFragment.newInstance(work)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvWorks.adapter = adapter
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