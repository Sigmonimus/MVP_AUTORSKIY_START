package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentWorkDetailBinding
import com.example.mvp_autorskiy_start.data.models.Work
import com.example.mvp_autorskiy_start.data.repository.WordRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.ui.vocabulary.VocabularyFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class WorkDetailFragment : BaseFragment<FragmentWorkDetailBinding>(FragmentWorkDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val work = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("work", Work::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Work>("work")
        } ?: return

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = work.title

        (requireActivity() as AppCompatActivity).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_work_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_vocabulary -> {
                        val fragment = VocabularyFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    R.id.action_add_word -> {
                        showAddWordDialog(work)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        val pagerAdapter = WorkPagerAdapter(requireActivity(), work)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Краткое"
                1 -> "Полный текст"
                2 -> "Аргументы (${work.arguments.size})"
                else -> ""
            }
        }.attach()
    }

    private fun showAddWordDialog(work: Work) {
        val input = EditText(requireContext())
        input.hint = "Введите слово"
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Добавить слово в словарь")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val word = input.text.toString().trim()
                if (word.isNotEmpty()) {
                    lifecycleScope.launch {
                        WordRepository.addWord(word)   // используем addWord, если есть
                    }
                    Toast.makeText(requireContext(), "Слово «$word» сохранено", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    companion object {
        fun newInstance(work: Work): WorkDetailFragment {
            val fragment = WorkDetailFragment()
            val args = Bundle()
            args.putParcelable("work", work)
            fragment.arguments = args
            return fragment
        }
    }
}