package com.example.mvp_autorskiy_start.ui.theory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentTheoryBinding

class TheoryFragment : Fragment() {

    private var _binding: FragmentTheoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTheoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subsections = listOf(
            SubsectionItem("Структура", R.drawable.structure, "structure"),
            SubsectionItem("Клише", R.drawable.cliche, "cliche"),
            SubsectionItem("Примеры", R.drawable.examples, "examples"),
            SubsectionItem("Критерии", R.drawable.criteria, "criteria"),
            SubsectionItem("Ошибки", R.drawable.mistakes, "mistakes")
        )

        binding.rvSubsections.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SubsectionAdapter(subsections) { item ->
            val fragment = TheoryDetailFragment.newInstance(item.key)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvSubsections.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}