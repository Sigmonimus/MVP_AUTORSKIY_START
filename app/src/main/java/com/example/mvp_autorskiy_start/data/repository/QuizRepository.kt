package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Quiz
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking

object QuizRepository {

    private var quizzes: List<Quiz> = emptyList()

    fun loadQuizzes(context: Context): List<Quiz> {
        if (quizzes.isNotEmpty()) return quizzes
        val jsonString = context.resources.openRawResource(R.raw.quizzes)
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Quiz>>() {}.type
        quizzes = Gson().fromJson(jsonString, type)
        return quizzes
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
            App.dataStoreManager.setQuizCompleted(quizId, true)
            if (percentage >= 70) {
                App.dataStoreManager.setQuizUnlocked(quizId + 1, true)
            }
        }
    }

    fun getBestScore(quizId: Int): Int = runBlocking { App.dataStoreManager.getQuizScore(quizId) }

    fun isQuizCompleted(quizId: Int): Boolean = runBlocking { App.dataStoreManager.isQuizCompleted(quizId) }

    fun isQuizUnlocked(quizId: Int): Boolean = runBlocking { App.dataStoreManager.isQuizUnlocked(quizId) }
}