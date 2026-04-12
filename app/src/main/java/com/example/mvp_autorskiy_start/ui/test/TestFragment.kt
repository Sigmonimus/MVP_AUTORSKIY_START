package com.example.mvp_autorskiy_start.ui.test

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Question
import com.example.mvp_autorskiy_start.data.repository.QuizRepository
import com.example.mvp_autorskiy_start.databinding.FragmentTestBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.utils.SoundPlayer
import com.google.android.material.card.MaterialCardView

class TestFragment : BaseFragment<FragmentTestBinding>(FragmentTestBinding::inflate) {

    private val viewModel: TestViewModel by viewModels()
    private var currentIndex = 0
    private var score = 0
    private var selectedOptionIndex = -1
    private var quizId: Int = -1
    private var totalQuestions: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("questions", Question::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList("questions") ?: emptyList()
        }
        quizId = arguments?.getInt("quizId", -1) ?: -1

        if (questions.isEmpty()) return

        val shuffled = viewModel.initShuffledQuestions(questions)
        totalQuestions = shuffled.size

        displayQuestion()

        binding.btnCheck.setOnClickListener { checkAnswer() }
        binding.btnNext.setOnClickListener { nextQuestion() }
    }

    private fun displayQuestion() {
        val question = viewModel.shuffledQuestions[currentIndex]
        binding.tvQuestionNumber.text = "Вопрос ${currentIndex + 1}/$totalQuestions"
        binding.progressBar.max = totalQuestions
        binding.progressBar.progress = currentIndex + 1
        binding.tvQuestionText.text = question.originalQuestion.text

        binding.optionsContainer.removeAllViews()
        val letters = arrayOf("А", "Б", "В", "Г")
        question.shuffledOptions.forEachIndexed { index, option ->
            val card = layoutInflater.inflate(R.layout.item_option, binding.optionsContainer, false) as MaterialCardView
            val tvLetter = card.findViewById<android.widget.TextView>(R.id.tvOptionLetter)
            val tvText = card.findViewById<android.widget.TextView>(R.id.tvOptionText)
            tvLetter.text = letters[index]
            tvText.text = option
            card.tag = index
            card.setOnClickListener { onOptionSelected(index) }
            binding.optionsContainer.addView(card)
        }

        for (i in 0 until binding.optionsContainer.childCount) {
            val card = binding.optionsContainer.getChildAt(i) as MaterialCardView
            card.isEnabled = true
            card.isClickable = true
            card.alpha = 1f
        }
        selectedOptionIndex = -1
        binding.btnCheck.isEnabled = true
        binding.btnNext.isEnabled = false
    }

    private fun onOptionSelected(index: Int) {
        for (i in 0 until binding.optionsContainer.childCount) {
            val card = binding.optionsContainer.getChildAt(i) as MaterialCardView
            card.isChecked = false
            card.strokeColor = ContextCompat.getColor(requireContext(), R.color.primary_light)
            card.strokeWidth = 1
            card.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        }
        val selectedCard = binding.optionsContainer.getChildAt(index) as MaterialCardView
        selectedCard.isChecked = true
        selectedCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.primary)
        selectedCard.strokeWidth = 3
        selectedCard.animate()
            .scaleX(1.02f)
            .scaleY(1.02f)
            .setDuration(150)
            .withEndAction {
                selectedCard.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            .start()
        selectedOptionIndex = index
    }

    private fun checkAnswer() {
        if (selectedOptionIndex == -1) return
        val question = viewModel.shuffledQuestions[currentIndex]
        val isCorrect = selectedOptionIndex == question.correctAnswerIndex

        for (i in 0 until binding.optionsContainer.childCount) {
            val card = binding.optionsContainer.getChildAt(i) as MaterialCardView
            if (i == question.correctAnswerIndex) {
                animateCardColor(card, ContextCompat.getColor(requireContext(), R.color.correct_green))
            } else if (i == selectedOptionIndex && !isCorrect) {
                animateCardColor(card, ContextCompat.getColor(requireContext(), R.color.wrong_red))
            } else {
                card.strokeColor = ContextCompat.getColor(requireContext(), R.color.primary_light)
                card.strokeWidth = 1
            }
        }

        if (isCorrect) {
            SoundPlayer.playCorrect()
            score++
        } else {
            SoundPlayer.playWrong()
        }

        for (i in 0 until binding.optionsContainer.childCount) {
            val card = binding.optionsContainer.getChildAt(i) as MaterialCardView
            card.isEnabled = false
            card.isClickable = false
            card.alpha = 0.6f
        }
        binding.btnCheck.isEnabled = false
        binding.btnNext.isEnabled = true
    }

    private fun nextQuestion() {
        if (currentIndex < totalQuestions - 1) {
            currentIndex++
            displayQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {
        if (quizId != -1) {
            QuizRepository.updateQuizProgress(quizId, score, totalQuestions)
        }
        val resultFragment = TestResultFragment.newInstance(score, totalQuestions, quizId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, resultFragment)
            .commit()
    }

    private fun animateCardColor(card: MaterialCardView, targetColor: Int) {
        val startColor = card.strokeColor
        ValueAnimator.ofObject(ArgbEvaluator(), startColor, targetColor).apply {
            duration = 300
            addUpdateListener { animator ->
                card.strokeColor = animator.animatedValue as Int
                card.strokeWidth = 3
            }
            start()
        }
        if (targetColor == ContextCompat.getColor(requireContext(), R.color.wrong_red)) {
            card.animate().rotationBy(8f).setDuration(50).withEndAction {
                card.animate().rotationBy(-16f).setDuration(50).withEndAction {
                    card.animate().rotationBy(8f).setDuration(50).start()
                }.start()
            }.start()
        } else if (targetColor == ContextCompat.getColor(requireContext(), R.color.correct_green)) {
            card.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).withEndAction {
                card.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            }.start()
        }
    }

    companion object {
        fun newInstance(questions: ArrayList<Question>, quizId: Int): TestFragment {
            val fragment = TestFragment()
            val args = Bundle()
            args.putParcelableArrayList("questions", questions)
            args.putInt("quizId", quizId)
            fragment.arguments = args
            return fragment
        }
    }
}