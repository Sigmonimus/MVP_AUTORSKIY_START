package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Difficulty
import com.example.mvp_autorskiy_start.data.models.Question
import com.example.mvp_autorskiy_start.data.models.Quiz
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object QuizRepository {

    private const val PREFS_NAME = "quiz_prefs"
    private const val KEY_SCORE_PREFIX = "quiz_score_"
    private const val KEY_COMPLETED_PREFIX = "quiz_completed_"
    private const val KEY_UNLOCKED_PREFIX = "quiz_unlocked_"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun loadQuizzes(context: Context): List<Quiz> {
        val json = context.resources.openRawResource(R.raw.quizzes).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<QuizJson>>() {}.type
        val quizzesJson: List<QuizJson> = gson.fromJson(json, type)

        val quizzes = quizzesJson.map { jsonQuiz ->
            val questions = jsonQuiz.questions.map { it.toQuestion() }
            val difficulty = when (jsonQuiz.difficulty) {
                "easy" -> Difficulty.EASY
                "medium" -> Difficulty.MEDIUM
                "hard" -> Difficulty.HARD
                else -> Difficulty.MEDIUM
            }
            Quiz(
                id = jsonQuiz.id,
                title = jsonQuiz.title,
                description = jsonQuiz.description,
                difficulty = difficulty,
                questions = questions,
                passingScore = jsonQuiz.passingScore,
                bestScore = getQuizScore(jsonQuiz.id),
                isCompleted = isQuizCompleted(jsonQuiz.id),
                isUnlocked = isQuizUnlocked(jsonQuiz.id)
            )
        }

        // Первый всегда разблокирован
        if (quizzes.isNotEmpty() && !isQuizUnlocked(quizzes[0].id)) {
            setQuizUnlocked(quizzes[0].id, true)
        }

        // Последовательная разблокировка: i-й тест открыт, если i-1 пройден с passingScore
        for (i in 1 until quizzes.size) {
            val prev = quizzes[i - 1]
            val prevPassed = prev.isCompleted && prev.bestScore >= prev.passingScore
            if (prevPassed) {
                if (!isQuizUnlocked(quizzes[i].id)) {
                    setQuizUnlocked(quizzes[i].id, true)
                }
            } else {
                if (isQuizUnlocked(quizzes[i].id)) {
                    setQuizUnlocked(quizzes[i].id, false)
                }
            }
        }

        // Обновляем объекты актуальными значениями unlocked
        return quizzes.map { quiz ->
            quiz.copy(isUnlocked = isQuizUnlocked(quiz.id))
        }
    }

    fun updateQuizProgress(quizId: Int, score: Int, totalQuestions: Int) {
        val percent = (score.toFloat() / totalQuestions * 100).toInt()
        Log.d("QuizRepo", "update: id=$quizId, score=$score/$totalQuestions, percent=$percent")

        val currentBest = getQuizScore(quizId)
        if (percent > currentBest) {
            setQuizScore(quizId, percent)
            Log.d("QuizRepo", "Saved best: $percent")
        }

        if (percent >= getQuizPassingScore(quizId) && !isQuizCompleted(quizId)) {
            setQuizCompleted(quizId, true)
            Log.d("QuizRepo", "Marked completed: $quizId")
        }

        if (isQuizCompleted(quizId)) {
            val next = quizId + 1
            Log.d("QuizRepo", "Trying to unlock next: $next")
            if (next <= 10) {
                setQuizUnlocked(next, true)
                Log.d("QuizRepo", "Unlocked: $next")
            }
        }
    }

    private fun getQuizScore(quizId: Int): Int =
        prefs.getInt("$KEY_SCORE_PREFIX$quizId", 0)

    private fun setQuizScore(quizId: Int, score: Int) {
        prefs.edit().putInt("$KEY_SCORE_PREFIX$quizId", score).apply()
    }

    private fun isQuizCompleted(quizId: Int): Boolean =
        prefs.getBoolean("$KEY_COMPLETED_PREFIX$quizId", false)

    private fun setQuizCompleted(quizId: Int, completed: Boolean) {
        prefs.edit().putBoolean("$KEY_COMPLETED_PREFIX$quizId", completed).apply()
    }

    private fun isQuizUnlocked(quizId: Int): Boolean =
        prefs.getBoolean("$KEY_UNLOCKED_PREFIX$quizId", false)

    private fun setQuizUnlocked(quizId: Int, unlocked: Boolean) {
        prefs.edit().putBoolean("$KEY_UNLOCKED_PREFIX$quizId", unlocked).apply()
    }

    private fun getQuizPassingScore(quizId: Int): Int = 70

    // Вспомогательные классы для парсинга JSON
    private data class QuizJson(
        val id: Int,
        val title: String,
        val description: String,
        val difficulty: String,
        val passingScore: Int,
        val questions: List<QuestionJson>
    )

    private data class QuestionJson(
        val id: Int,
        val text: String,
        val options: List<String>,
        val correctAnswerIndex: Int
    ) {
        fun toQuestion() = Question(id, text, options, correctAnswerIndex)
    }

}