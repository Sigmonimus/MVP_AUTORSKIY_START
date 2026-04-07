package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Module
import com.example.mvp_autorskiy_start.data.models.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TestModuleRepository {

    private const val PREFS_NAME = "test_modules_prefs"
    private const val KEY_PREFIX_UNLOCKED = "module_"
    private const val KEY_PREFIX_COMPLETED = "module_completed_"
    private const val KEY_PREFIX_SCORE = "module_score_"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun loadModules(context: Context): List<Module> {
        // Загружаем модули из JSON
        val json = context.resources.openRawResource(R.raw.modules).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<ModuleJson>>() {}.type
        val modulesJson: List<ModuleJson> = gson.fromJson(json, type)

        val modules = modulesJson.map { jsonModule ->
            // Преобразуем JSON в Module
            val questions = jsonModule.questions.map { it.toQuestion() }
            val drawableId = ResourceMapper.getDrawableResId(jsonModule.iconRes)

            Module(
                id = jsonModule.id,
                title = jsonModule.title,
                description = jsonModule.description,
                iconRes = drawableId,
                questions = questions,
                passingScore = jsonModule.passingScore,
                isUnlocked = isModuleUnlocked(jsonModule.id),
                isCompleted = isModuleCompleted(jsonModule.id),
                bestScore = getModuleScore(jsonModule.id)
            )
        }

        // Убедимся, что первый модуль разблокирован
        if (modules.isNotEmpty() && !modules[0].isUnlocked) {
            setModuleUnlocked(modules[0].id, true)
            modules[0].isUnlocked = true
        }

        // Блокировка последующих: если предыдущий не пройден с порогом, блокируем
        for (i in 1 until modules.size) {
            val prev = modules[i - 1]
            val required = prev.passingScore
            val prevScore = prev.bestScore
            if (prevScore >= required) {
                if (!modules[i].isUnlocked) {
                    setModuleUnlocked(modules[i].id, true)
                    modules[i].isUnlocked = true
                }
            } else {
                if (modules[i].isUnlocked) {
                    setModuleUnlocked(modules[i].id, false)
                    modules[i].isUnlocked = false
                }
            }
        }

        return modules
    }

    fun updateModuleProgress(moduleId: Int, score: Int, totalQuestions: Int) {
        val percent = (score.toFloat() / totalQuestions * 100).toInt()
        val currentBest = getModuleScore(moduleId)
        if (percent > currentBest) {
            setModuleScore(moduleId, percent)
        }
        if (percent >= getModulePassingScore(moduleId) && !isModuleCompleted(moduleId)) {
            setModuleCompleted(moduleId, true)
        }
        // Примечание: разблокировка следующего модуля будет выполнена при следующей загрузке
    }

    private fun isModuleUnlocked(moduleId: Int): Boolean =
        prefs.getBoolean("$KEY_PREFIX_UNLOCKED$moduleId", false)

    private fun setModuleUnlocked(moduleId: Int, unlocked: Boolean) {
        prefs.edit().putBoolean("$KEY_PREFIX_UNLOCKED$moduleId", unlocked).apply()
    }

    private fun isModuleCompleted(moduleId: Int): Boolean =
        prefs.getBoolean("$KEY_PREFIX_COMPLETED$moduleId", false)

    private fun setModuleCompleted(moduleId: Int, completed: Boolean) {
        prefs.edit().putBoolean("$KEY_PREFIX_COMPLETED$moduleId", completed).apply()
    }

    private fun getModuleScore(moduleId: Int): Int =
        prefs.getInt("$KEY_PREFIX_SCORE$moduleId", 0)

    private fun setModuleScore(moduleId: Int, score: Int) {
        prefs.edit().putInt("$KEY_PREFIX_SCORE$moduleId", score).apply()
    }

    private fun getModulePassingScore(moduleId: Int): Int {
        // Проходной балл можно загрузить из кэша, но для простоты вернём 70.
        // В будущем можно хранить в мапе.
        return 70
    }

    // Вспомогательные классы для парсинга JSON
    private data class ModuleJson(
        val id: Int,
        val title: String,
        val description: String,
        val iconRes: String,
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