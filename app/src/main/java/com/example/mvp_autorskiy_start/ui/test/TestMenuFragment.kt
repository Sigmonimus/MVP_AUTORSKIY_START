package com.example.mvp_autorskiy_start.ui.test

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentTestMenuBinding
import com.example.mvp_autorskiy_start.data.repository.QuizRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

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

        pathMapView.setOnQuizClickListener { quiz ->
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

    private fun loadQuizzes() {
        val quizzes = QuizRepository.loadQuizzes(requireContext())
        pathMapView.setQuizzes(quizzes)
        scrollToActivePoint()
    }

    private fun scrollToActivePoint() {
        val quizzes = QuizRepository.loadQuizzes(requireContext())
        val firstUnlocked = quizzes.indexOfFirst { it.isUnlocked && !it.isCompleted }
        if (firstUnlocked != -1) {
            pathMapView.post {
                if (isAdded && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    binding.scrollView.smoothScrollTo(0, (pathMapView.getPointY(firstUnlocked) - 200).toInt())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadQuizzes()
    }
}