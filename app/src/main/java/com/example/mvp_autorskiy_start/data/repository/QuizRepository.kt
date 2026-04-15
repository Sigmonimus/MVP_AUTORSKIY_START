package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Difficulty
import com.example.mvp_autorskiy_start.data.models.Quiz
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking

object QuizRepository {

    private var quizzes: List<Quiz>? = null

    fun loadQuizzes(context: Context): List<Quiz> {
        quizzes?.let { return it }

        val jsonString = context.resources.openRawResource(R.raw.quizzes)
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Quiz>>() {}.type
        val parsed: List<Quiz> = Gson().fromJson(jsonString, type) ?: emptyList()

        // Загружаем сохранённые состояния и создаём новые объекты без copy
        val loadedQuizzes = mutableListOf<Quiz>()
        for (quiz in parsed) {
            val best = getBestScore(quiz.id)
            val completed = isQuizCompleted(quiz.id)
            val unlocked = isQuizUnlocked(quiz.id)
            val safeDifficulty = quiz.difficulty ?: Difficulty.MEDIUM
            loadedQuizzes.add(
                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    description = quiz.description,
                    difficulty = safeDifficulty,
                    questions = quiz.questions,
                    passingScore = quiz.passingScore,
                    bestScore = best,
                    isCompleted = completed,
                    isUnlocked = unlocked
                )
            )
        }

        quizzes = loadedQuizzes

        // Разблокируем первый тест, если он ещё не разблокирован
        if (quizzes!!.isNotEmpty() && !quizzes!![0].isUnlocked) {
            setQuizUnlocked(quizzes!![0].id, true)
            quizzes = quizzes!!.mapIndexed { index, q ->
                if (index == 0) q.copy(isUnlocked = true) else q
            }
        }

        return quizzes!!
    }

    fun getQuizById(context: Context, quizId: Int): Quiz? {
        return loadQuizzes(context).find { it.id == quizId }
    }

    fun saveQuizResult(context: Context, quizId: Int, score: Int, totalQuestions: Int) {
        val percentage = (score * 100) / totalQuestions
        runBlocking {
            val bestScore = App.dataStoreManager.getQuizScore(quizId)
            if (percentage > bestScore) {
                App.dataStoreManager.setQuizScore(quizId, percentage)
            }
            if (percentage >= 70) {
                App.dataStoreManager.setQuizCompleted(quizId, true)
                // Разблокируем следующий тест только если текущий успешно пройден
                App.dataStoreManager.setQuizUnlocked(quizId + 1, true)
            } else {
                // Не пройден – следующий не разблокируется
                App.dataStoreManager.setQuizCompleted(quizId, false)
                // Убедимся, что следующий тест не разблокирован
                App.dataStoreManager.setQuizUnlocked(quizId + 1, false)
            }
        }
    }

    fun getBestScore(quizId: Int): Int = runBlocking { App.dataStoreManager.getQuizScore(quizId) }

    fun isQuizCompleted(quizId: Int): Boolean = runBlocking { App.dataStoreManager.isQuizCompleted(quizId) }

    fun isQuizUnlocked(quizId: Int): Boolean = runBlocking { App.dataStoreManager.isQuizUnlocked(quizId) }

    private fun setQuizUnlocked(quizId: Int, unlocked: Boolean) {
        runBlocking { App.dataStoreManager.setQuizUnlocked(quizId, unlocked) }
    }
}