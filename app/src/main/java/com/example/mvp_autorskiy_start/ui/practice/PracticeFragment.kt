package com.example.mvp_autorskiy_start.ui.practice

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.PracticeDraft
import com.example.mvp_autorskiy_start.data.models.SavedEssay
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.databinding.FragmentPracticeBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PracticeFragment : BaseFragment<FragmentPracticeBinding>(FragmentPracticeBinding::inflate) {

    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 90 * 60 * 1000L
    private var isTimerRunning = false
    private var currentDraftId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDraftsList()
        setupTextWatcher()
        setupButtons()
    }

    private fun setupTextWatcher() {
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCounters()
                updateChecklist()
            }
        })
    }

    private fun updateCounters() {
        val text = binding.etContent.text.toString()
        val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val chars = text.length
        binding.tvStats.text = "$words слов · $chars знаков"
    }

    private fun updateChecklist() {
        val text = binding.etContent.text.toString()
        val thesis = Regex("(я считаю|по моему мнению|на мой взгляд)", RegexOption.IGNORE_CASE).containsMatchIn(text)
        val argument = Regex("(например|так|потому что|во-первых)", RegexOption.IGNORE_CASE).containsMatchIn(text)
        val conclusion = Regex("(таким образом|итак|следовательно|в заключение)", RegexOption.IGNORE_CASE).containsMatchIn(text)
        val wordCount = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size >= 250
        val structure = text.contains(Regex(".{10,}\n.{10,}\n.{10,}"))

        val missing = mutableListOf<String>()
        if (!thesis) missing.add("тезис во вступлении")
        if (!argument) missing.add("литературный аргумент")
        if (!conclusion) missing.add("заключение")
        if (!wordCount) missing.add("объём ≥250 слов")
        if (!structure) missing.add("чёткая структура")

        val message = if (missing.isNotEmpty()) {
            "⚠️ По критериям ФИПИ не хватает: ${missing.joinToString(", ")}"
        } else {
            "✅ Отлично! Сочинение соответствует основным требованиям."
        }
        if (binding.tvHint.text != message) {
            binding.tvHint.text = message
            binding.tvHint.visibility = View.VISIBLE
        }
    }

    private fun setupButtons() {
        binding.btnStartPause.setOnClickListener {
            if (isTimerRunning) pauseTimer() else startTimer()
        }
        binding.btnReset.setOnClickListener { resetTimer() }
        binding.btnHelp.setOnClickListener { showHelpDialog() }
        binding.btnSave.setOnClickListener { saveDraft() }
        binding.btnClear.setOnClickListener { newDraft() }
        binding.btnFavorite.setOnClickListener { addToFavorites() }
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            timer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimerDisplay()
                }
                override fun onFinish() {
                    isTimerRunning = false
                    binding.tvTimer.text = "00:00"
                    binding.btnStartPause.text = "Старт"
                    Toast.makeText(requireContext(), "Время вышло!", Toast.LENGTH_SHORT).show()
                }
            }.start()
            isTimerRunning = true
            binding.btnStartPause.text = "Пауза"
        }
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        binding.btnStartPause.text = "Старт"
    }

    private fun resetTimer() {
        timer?.cancel()
        timeLeftInMillis = 90 * 60 * 1000L
        updateTimerDisplay()
        isTimerRunning = false
        binding.btnStartPause.text = "Старт"
    }

    private fun updateTimerDisplay() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Справка")
            .setMessage("""
                Структура итогового сочинения:
                1. Вступление (тезис)
                2. Аргумент 1
                3. Аргумент 2
                4. Заключение
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun saveDraft() {
        val title = binding.etTheme.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Введите тему и текст", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val draftId = currentDraftId ?: UUID.randomUUID().toString()
            val draft = PracticeDraft(draftId, title, content, System.currentTimeMillis())
            val prefs = App.dataStoreManager
            val drafts = prefs.getPracticeDrafts().toMutableMap()
            drafts[draftId] = draft
            prefs.setPracticeDrafts(drafts)
            prefs.setTotalDraftsCount(drafts.size)

            currentDraftId = draftId
            Toast.makeText(requireContext(), "Черновик сохранён", Toast.LENGTH_SHORT).show()
            loadDraftsList()
        }
    }

    private fun newDraft() {
        binding.etTheme.text?.clear()
        binding.etContent.text?.clear()
        currentDraftId = null
        resetTimer()
    }

    private fun loadDraftsList() {
        lifecycleScope.launch {
            val drafts = App.dataStoreManager.getPracticeDrafts().values.toList()
            val adapter = DraftsAdapter(drafts) { draft ->
                binding.etTheme.setText(draft.title)
                binding.etContent.setText(draft.content)
                currentDraftId = draft.id
            }
            binding.rvDrafts.adapter = adapter
        }
    }

    private fun addToFavorites() {
        val title = binding.etTheme.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Нет текста для добавления", Toast.LENGTH_SHORT).show()
            return
        }
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val essay = SavedEssay(title, content, dateFormat.format(Date()))
        lifecycleScope.launch {
            FavoritesRepository.saveEssay(essay)
            val wordsCount = content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            App.dataStoreManager.setTotalWordsCount(App.dataStoreManager.getTotalWordsCount() + wordsCount)
        }
        Toast.makeText(requireContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }
}