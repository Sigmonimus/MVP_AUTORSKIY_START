package com.example.mvp_autorskiy_start.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.QuizRepository
import com.example.mvp_autorskiy_start.databinding.FragmentTestMenuBinding

class TestMenuFragment : Fragment() {

    private var _binding: FragmentTestMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var pathMapView: PathMapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            val fragment = TestFragment.Companion.newInstance(ArrayList(quiz.questions), quiz.id)
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_scale_up,   // анимация входа нового фрагмента (тест)
                    R.anim.exit_scale_down,  // анимация выхода старого (карта)
                    R.anim.enter_scale_up,   // при возврате назад (карта появляется масштабированием)
                    R.anim.exit_scale_down   // при возврате назад (тест исчезает масштабированием)
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
                _binding?.scrollView?.smoothScrollTo(0, (pathMapView.getPointY(firstUnlocked) - 200).toInt())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadQuizzes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}