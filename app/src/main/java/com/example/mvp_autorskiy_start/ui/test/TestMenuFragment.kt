package com.example.mvp_autorskiy_start.ui.test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.QuizRepository
import com.example.mvp_autorskiy_start.databinding.FragmentTestMenuBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import kotlinx.coroutines.launch

class TestMenuFragment : BaseFragment<FragmentTestMenuBinding>(FragmentTestMenuBinding::inflate) {

    private lateinit var pathMapView: PathMapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pathMapView = PathMapView(requireContext())
        pathMapView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        binding.scrollViewContainer.removeAllViews()
        binding.scrollViewContainer.addView(pathMapView)

        loadQuizzes()
    }

    private fun loadQuizzes() {
        lifecycleScope.launch {
            val quizzes = QuizRepository.loadQuizzes(requireContext())
            Log.d("TestMenu", "Loaded ${quizzes.size} quizzes")
            if (quizzes.isNotEmpty()) {
                pathMapView.setQuizzes(quizzes)
                // Принудительно пересчитываем размеры View
                pathMapView.post {
                    pathMapView.requestLayout()
                    pathMapView.invalidate()
                }
            } else {
                Log.e("TestMenu", "No quizzes loaded")
            }
            pathMapView.setOnQuizClickListener { quiz ->
                Log.d("TestMenu", "Quiz clicked: ${quiz.title}")
                val fragment = TestFragment.newInstance(ArrayList(quiz.questions), quiz.id)
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.enter_scale_up,
                        R.anim.exit_scale_down,
                        R.anim.enter_scale_up,
                        R.anim.exit_scale_down
                    )
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadQuizzes()
    }
}