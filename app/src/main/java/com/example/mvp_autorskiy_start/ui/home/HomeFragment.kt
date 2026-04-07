package com.example.mvp_autorskiy_start.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentHomeBinding
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private val illustrations = arrayOf(
        R.drawable.illustration_war_and_peace,                // 1. Война и мир
        R.drawable.illustration_crime_and_punishment,         // 2. Преступление и наказание
        R.drawable.illustration_master_and_margarita,         // 3. Мастер и Маргарита
        R.drawable.illustration_eugene_onegin,                // 4. Евгений Онегин
        R.drawable.illustration_anna_karenina,                // 5. Анна Каренина
        R.drawable.illustration_fathers_and_sons,             // 6. Отцы и дети
        R.drawable.illustration_dead_souls,                   // 7. Мёртвые души
        R.drawable.illustration_a_hero_of_our_time,           // 8. Герой нашего времени
        R.drawable.illustration_the_storm,                    // 9. Гроза
        R.drawable.illustration_oblomov,                      // 10. Обломов
        R.drawable.illustration_quiet_don,                    // 11. Тихий Дон
        R.drawable.illustration_heart_of_a_dog,               // 12. Собачье сердце
        R.drawable.illustration_the_captains_daughter,        // 13. Капитанская дочка
        R.drawable.illustration_at_the_bottom,                // 14. На дне
        R.drawable.illustration_the_twelve,                   // 15. Двенадцать
        R.drawable.illustration_who_lives_well_in_russia,     // 16. Кому на Руси жить хорошо
        R.drawable.illustration_lefty,                        // 17. Левша
        R.drawable.illustration_ward_no_6,                    // 18. Палата №6
        R.drawable.illustration_the_cherry_orchard,           // 19. Вишнёвый сад
        R.drawable.illustration_poor_liza                      // 20. Бедная Лиза
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Запускаем асинхронную загрузку данных
        homeViewModel.loadData(requireContext())

        // Наблюдаем за результатами
        homeViewModel.randomQuote.observe(viewLifecycleOwner) { quote ->
            binding.quoteText.text = "«${quote.text}»"
            binding.quoteAuthor.text = "— ${quote.author}"
        }
        homeViewModel.randomTip.observe(viewLifecycleOwner) { tip ->
            binding.tipText.text = tip
        }

        // Быстрые операции (чтение SharedPreferences, установка картинки)
        loadUserInfo()
        loadRandomIllustration()
        setupSettingsClick()
        setupCalendar() // если календарь уже реализован и не тормозит
    }

    private fun loadUserInfo() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Гость")
        binding.greetingText.text = "С возвращением, \n$userName!"
    }

    private fun loadRandomIllustration() {
        if (illustrations.isNotEmpty()) {
            val randomIndex = Random().nextInt(illustrations.size)
            binding.randomIllustration.setImageResource(illustrations[randomIndex])
            binding.randomIllustration.visibility = View.VISIBLE
        } else {
            binding.randomIllustration.visibility = View.GONE
        }
    }

    private fun setupSettingsClick() {
        binding.settingsIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupCalendar() {
        // Если календарь активности тормозит, его тоже нужно вынести в ViewModel
        // Пример:
        // homeViewModel.activeDates.observe(viewLifecycleOwner) { dates ->
        //     binding.activityCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        //     binding.activityCalendar.adapter = CalendarAdapter(dates)
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}