package com.example.mvp_autorskiy_start.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentTestMenuBinding
import com.example.mvp_autorskiy_start.data.Question

class TestMenuFragment : Fragment() {

    private var _binding: FragmentTestMenuBinding? = null
    private val binding get() = _binding!!

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

        // Создаём список тестов
        val tests = listOf(
            TestItem(
                id = 1,
                title = "Теория",
                description = "Проверьте знание объёма, времени и структуры",
                iconRes = R.drawable.ic_book,
                questionCount = getTheoryQuestions().size
            ),
            TestItem(
                id = 2,
                title = "Понимание",
                description = "Найдите ошибки в предложениях",
                iconRes = R.drawable.ic_comprehension,
                questionCount = getComprehensionQuestions().size
            ),
            TestItem(
                id = 3,
                title = "Аргументы",
                description = "Подберите произведение к теме",
                iconRes = R.drawable.ic_arguments,
                questionCount = getArgumentsQuestions().size
            )
        )

        binding.rvTests.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TestAdapter(tests) { test ->
            val questions = when (test.id) {
                1 -> getTheoryQuestions()
                2 -> getComprehensionQuestions()
                3 -> getArgumentsQuestions()
                else -> emptyList()
            }
            val fragment = TestFragment.newInstance(ArrayList(questions))
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvTests.adapter = adapter
    }

    private fun getTheoryQuestions(): List<Question> {
        return listOf(
            Question(1, "Каков рекомендуемый объём итогового сочинения?",
                listOf("150-200 слов", "250-300 слов", "350+ слов", "Любой"), 2),
            Question(2, "Сколько времени даётся на написание?",
                listOf("1 час 30 минут", "3 часа 55 минут", "4 часа", "2 часа"), 1),
            Question(3, "Сколько абзацев должно быть в сочинении?",
                listOf("2-3", "3-4", "4-5", "Не важно"), 1),
            Question(4, "Что обязательно должно быть во вступлении?",
                listOf("Тезис", "Аргумент", "Цитата", "Вывод"), 0),
            Question(5, "Какой критерий оценивания самый важный?",
                listOf("Соответствие теме", "Грамотность", "Оригинальность", "Объём"), 0)
        )
    }

    private fun getComprehensionQuestions(): List<Question> {
        return listOf(
            Question(1, "Найдите ошибку в предложении: 'Пьер Безухов и Андрей Болконский они были друзьями.'",
                listOf("Нет ошибки", "Лишнее местоимение 'они'", "Неправильный падеж", "Пунктуация"), 1),
            Question(2, "В каком предложении нарушена логика?",
                listOf("Утром пошёл дождь, поэтому мы взяли зонты.",
                    "Он любил читать книги, особенно фантастику.",
                    "Сдав экзамен, у него началась новая жизнь.",
                    "Солнце село, и стало темно."), 2),
            Question(3, "Какой фрагмент содержит речевую ошибку?",
                listOf("Благодаря помощи друзей я справился.",
                    "Он надел пальто и вышел.",
                    "Ихняя собака громко лает.",
                    "В течение дня мы всё сделали."), 2),
            Question(4, "Выберите правильный вариант:",
                listOf("Согласно приказа", "Согласно приказу", "Согласно приказом", "Согласно приказе"), 1),
            Question(5, "В каком предложении нет грамматической ошибки?",
                listOf("По приезду в город мы заселились в отель.",
                    "Вопреки предсказаний погода наладилась.",
                    "Оплатив за проезд, выйдите на следующей остановке.",
                    "Уделять внимание на детали важно."), 0)
        )
    }

    private fun getArgumentsQuestions(): List<Question> {
        return listOf(
            Question(1, "Какое произведение лучше всего подходит к теме 'Любовь'?",
                listOf("Война и мир", "Преступление и наказание", "Муму", "Ревизор"), 0),
            Question(2, "Какое произведение иллюстрирует проблему 'Отцы и дети'?",
                listOf("Гроза", "Отцы и дети", "Вишнёвый сад", "Герой нашего времени"), 1),
            Question(3, "В каком произведении поднимается тема совести?",
                listOf("Герой нашего времени", "Капитанская дочка", "Преступление и наказание", "Евгений Онегин"), 2),
            Question(4, "Какое произведение подходит к теме 'Дружба'?",
                listOf("Война и мир (Андрей и Пьер)", "Мёртвые души", "Недоросль", "Горе от ума"), 0),
            Question(5, "Тема 'Маленького человека' раскрыта в...",
                listOf("Шинель", "Ревизор", "Гроза", "Кому на Руси жить хорошо"), 0)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}