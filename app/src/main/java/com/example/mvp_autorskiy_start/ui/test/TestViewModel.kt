package com.example.mvp_autorskiy_start.ui.test

import androidx.lifecycle.ViewModel
import com.example.mvp_autorskiy_start.data.models.Question
import com.example.mvp_autorskiy_start.data.models.ShuffledQuestion

class TestViewModel : ViewModel() {

    private var _shuffledQuestions: List<ShuffledQuestion>? = null
    val shuffledQuestions: List<ShuffledQuestion>
        get() = _shuffledQuestions ?: emptyList()

    fun initShuffledQuestions(questions: List<Question>): List<ShuffledQuestion> {
        if (_shuffledQuestions == null) {
            _shuffledQuestions = questions.map { question ->
                val shuffled = question.options.toMutableList()
                shuffled.shuffle()
                val newCorrectIndex = shuffled.indexOf(question.options[question.correctAnswerIndex])
                ShuffledQuestion(question, shuffled, newCorrectIndex)
            }.shuffled()
        }
        return _shuffledQuestions!!
    }

    fun resetShuffledQuestions() {
        _shuffledQuestions = null
    }
}