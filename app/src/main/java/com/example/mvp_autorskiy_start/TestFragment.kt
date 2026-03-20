package com.example.mvp_autorskiy_start.ui.test

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.Question
import com.example.mvp_autorskiy_start.databinding.FragmentTestBinding
import com.google.android.material.card.MaterialCardView

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private var score = 0
    private var selectedOptionIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("questions", Question::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList<Question>("questions") ?: emptyList()
        }

        if (questions.isEmpty()) {
            // Можно показать сообщение об ошибке
            return
        }

        displayQuestion()

        binding.btnCheck.setOnClickListener { checkAnswer() }
        binding.btnNext.setOnClickListener { nextQuestion() }
    }

    private fun displayQuestion() {
        val question = questions[currentIndex]
        binding.tvQuestionNumber.text = "Вопрос ${currentIndex + 1}/${questions.size}"
        binding.progressBar.max = questions.size
        binding.progressBar.progress = currentIndex + 1

        binding.tvQuestionText.text = question.text

        // Очищаем контейнер и создаём карточки для вариантов
        binding.optionsContainer.removeAllViews()
        val letters = arrayOf("А", "Б", "В", "Г")
        question.options.forEachIndexed { index, option ->
            val card = layoutInflater.inflate(R.layout.item_option, binding.optionsContainer, false) as MaterialCardView
            val tvLetter = card.findViewById<TextView>(R.id.tvOptionLetter)
            val tvText = card.findViewById<TextView>(R.id.tvOptionText)

            tvLetter.text = letters[index]
            tvText.text = option

            card.tag = index
            card.setOnClickListener { onOptionSelected(index) }

            binding.optionsContainer.addView(card)
        }

        selectedOptionIndex = -1
        binding.btnCheck.isEnabled = true
        binding.btnNext.isEnabled = false
    }

    private fun onOptionSelected(index: Int) {
        // Сбрасываем выделение у всех карточек
        for (i in 0 until binding.optionsContainer.childCount) {
            val card = binding.optionsContainer.getChildAt(i) as MaterialCardView
            card.isChecked = false
            card.strokeColor = ContextCompat.getColor(requireContext(), R.color.primary_light)
            card.strokeWidth = 1
        }
        // Выделяем выбранную
        val selectedCard = binding.optionsContainer.getChildAt(index) as MaterialCardView
        selectedCard.isChecked = true
        selectedCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.primary)
        selectedCard.strokeWidth = 2

        selectedOptionIndex = index
    }

    private fun checkAnswer() {
        if (selectedOptionIndex == -1) {
            // Показать сообщение, что нужно выбрать ответ
            return
        }

        val question = questions[currentIndex]
        val isCorrect = selectedOptionIndex == question.correctAnswerIndex

        // Визуальная обратная связь
        for (i in 0 until binding.optionsContainer.childCount) {
            val card = binding.optionsContainer.getChildAt(i) as MaterialCardView
            if (i == question.correctAnswerIndex) {
                // Правильный ответ зелёный
                animateCardColor(card, ContextCompat.getColor(requireContext(), R.color.correct_green))
            } else if (i == selectedOptionIndex && !isCorrect) {
                // Неправильный выбранный ответ красный
                animateCardColor(card, ContextCompat.getColor(requireContext(), R.color.wrong_red))
            } else {
                // Остальные серые
                card.strokeColor = ContextCompat.getColor(requireContext(), R.color.primary_light)
                card.strokeWidth = 1
            }
        }

        if (isCorrect) {
            score++
        }

        binding.btnCheck.isEnabled = false
        binding.btnNext.isEnabled = true
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
    }

    private fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            displayQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {
        val resultFragment = TestResultFragment.newInstance(score, questions.size)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, resultFragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(questions: ArrayList<Question>): TestFragment {
            val fragment = TestFragment()
            val args = Bundle()
            args.putParcelableArrayList("questions", questions)
            fragment.arguments = args
            return fragment
        }
    }
}