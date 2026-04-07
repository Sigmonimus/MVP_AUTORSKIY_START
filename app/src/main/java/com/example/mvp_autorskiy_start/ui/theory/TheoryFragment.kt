package com.example.mvp_autorskiy_start.ui.theory

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentTheoryBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class TheoryFragment : BaseFragment<FragmentTheoryBinding>(FragmentTheoryBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subsections = listOf(
            SubsectionItem("Структура", R.drawable.ic_structure, "structure"),
            SubsectionItem("Клише", R.drawable.ic_cliche, "cliche"),
            SubsectionItem("Примеры", R.drawable.ic_examples, "examples"),
            SubsectionItem("Критерии", R.drawable.ic_criteria, "criteria"),
            SubsectionItem("Ошибки", R.drawable.ic_mistakes, "mistakes"),
            SubsectionItem("Чек-лист", R.drawable.ic_checklist, "checklist"),
            SubsectionItem("Глоссарий", R.drawable.ic_glossary, "glossary")
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
}