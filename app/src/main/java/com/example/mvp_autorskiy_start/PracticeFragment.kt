package com.example.mvp_autorskiy_start.ui.practice

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentPracticeBinding
import com.example.mvp_autorskiy_start.data.FavoritesRepository
import com.example.mvp_autorskiy_start.data.SavedEssay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class PracticeFragment : Fragment() {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private var timerSeconds = 0
    private var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val prefs by lazy { requireContext().getSharedPreferences("practice_prefs", Context.MODE_PRIVATE) }
    private val gson = Gson()
    private var drafts = mutableListOf<PracticeDraft>()
    private var currentDraftId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimer()
        setupListeners()
        loadDrafts()
        updateStats()
    }

    private fun setupTimer() {
        runnable = object : Runnable {
            override fun run() {
                timerSeconds++
                updateTimerDisplay()
                handler.postDelayed(this, 1000)
            }
        }

        binding.btnStartPause.setOnClickListener {
            if (isTimerRunning) {
                handler.removeCallbacks(runnable)
                binding.btnStartPause.text = "Старт"
            } else {
                handler.post(runnable)
                binding.btnStartPause.text = "Пауза"
            }
            isTimerRunning = !isTimerRunning
        }

        binding.btnReset.setOnClickListener {
            handler.removeCallbacks(runnable)
            isTimerRunning = false
            timerSeconds = 0
            updateTimerDisplay()
            binding.btnStartPause.text = "Старт"
        }
    }

    private fun updateTimerDisplay() {
        val hours = timerSeconds / 3600
        val minutes = (timerSeconds % 3600) / 60
        val seconds = timerSeconds % 60
        binding.tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun setupListeners() {
        binding.etContent.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateStats()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.btnHelp.setOnClickListener {
            showHelpDialog()
        }

        binding.btnSave.setOnClickListener {
            saveDraft()
        }

        binding.btnFavorite.setOnClickListener {
            addCurrentEssayToFavorites()
        }
    }

    private fun updateStats() {
        val text = binding.etContent.text.toString()
        val words = text.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        val chars = text.length
        binding.tvStats.text = "$words слов · $chars знаков"
    }

    private fun showHelpDialog() {
        val helpText = """
            Структура итогового сочинения:
            
            1. Вступление (тезис)
            2. Основная часть:
               - Аргумент 1
               - Аргумент 2
            3. Заключение (вывод)
            
            Рекомендации:
            • Объём: 350+ слов
            • Время: 3 часа 55 минут
            • Используйте клише для связи частей
        """.trimIndent()
        AlertDialog.Builder(requireContext())
            .setTitle("Справка")
            .setMessage(helpText)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun saveDraft() {
        val text = binding.etContent.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "Напишите хотя бы несколько слов", Toast.LENGTH_SHORT).show()
            return
        }

        val title = if (text.length > 30) text.substring(0, 30) + "…" else text
        val now = System.currentTimeMillis()
        val draft = PracticeDraft(
            id = currentDraftId ?: UUID.randomUUID().toString(),
            title = title,
            content = text,
            lastModified = now
        )

        if (currentDraftId == null) {
            drafts.add(draft)
        } else {
            val index = drafts.indexOfFirst { it.id == currentDraftId }
            if (index != -1) drafts[index] = draft
        }

        saveDraftsToPrefs()
        loadDrafts()

        Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
        // currentDraftId не сбрасываем, чтобы можно было продолжать редактирование
    }

    private fun addCurrentEssayToFavorites() {
        val text = binding.etContent.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "Нечего добавлять в избранное", Toast.LENGTH_SHORT).show()
            return
        }

        val title = if (text.length > 30) text.substring(0, 30) + "…" else "Без названия"
        val essay = SavedEssay(
            id = UUID.randomUUID().toString(),
            title = title,
            content = text,
            author = "Моё сочинение",
            theme = "", // пока пустая строка, позже можно добавить поле для ввода темы
            date = System.currentTimeMillis()
        )

        FavoritesRepository.addSavedEssay(essay)
        Log.d("PracticeFragment", "Essay saved: ${essay.title}")
        Toast.makeText(requireContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show()
    }

    private fun loadDrafts() {
        val json = prefs.getString("drafts", null)
        drafts = if (json != null) {
            val type = object : TypeToken<MutableList<PracticeDraft>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        val adapter = PracticeDraftAdapter(
            drafts = drafts,
            onItemClick = { draft ->
                currentDraftId = draft.id
                binding.etContent.setText(draft.content)
                binding.etContent.setSelection(draft.content.length)
            },
            onDeleteClick = { draft ->
                drafts.remove(draft)
                saveDraftsToPrefs()
                loadDrafts()
                if (currentDraftId == draft.id) {
                    currentDraftId = null
                    binding.etContent.setText("")
                }
            }
        )
        binding.rvDrafts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDrafts.adapter = adapter
    }

    private fun saveDraftsToPrefs() {
        val json = gson.toJson(drafts)
        prefs.edit().putString("drafts", json).apply()
    }

    override fun onDestroyView() {
        handler.removeCallbacks(runnable)
        super.onDestroyView()
        _binding = null
    }
}