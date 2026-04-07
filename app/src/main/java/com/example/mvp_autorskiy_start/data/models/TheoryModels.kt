package com.example.mvp_autorskiy_start.data

// Для Клише
data class Cliche(
    val title: String,
    val subtitle: String,
    val sections: List<ClicheSection>
)
data class ClicheSection(
    val name: String,
    val count: Int,
    val items: List<ClicheItem>
)
data class ClicheItem(
    val text: String,
    val style: String
)

// Для Примеров
data class Examples(
    val title: String,
    val subtitle: String,
    val categories: List<String>,
    val essays: List<Essay>
)
data class Essay(
    val title: String,
    val description: String,
    val readTime: String,
    val author: String
)

// Для Ошибок
data class Mistakes(
    val title: String,
    val subtitle: String,
    val categories: List<MistakeCategory>,
    val userFrequent: List<FrequentMistake>
)
data class MistakeCategory(
    val name: String,
    val description: String
)
data class FrequentMistake(
    val error: String,
    val date: String
)

// Для Критериев
data class Criteria(
    val title: String,
    val subtitle: String,
    val conditions: List<Condition>,
    val mainCriteria: List<MainCriterion>
)
data class Condition(
    val name: String,
    val description: String
)
data class MainCriterion(
    val code: String,
    val name: String,
    val description: String
)

// Для Структуры (можно позже добавить, пока оставим заглушку)
data class Structure(
    val title: String,
    val content: String // или разбить на блоки, но для простоты можно Markdown
)