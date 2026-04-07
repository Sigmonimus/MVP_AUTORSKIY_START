package com.example.mvp_autorskiy_start.ui.practice

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.data.models.PracticeDraft
import com.example.mvp_autorskiy_start.data.models.SavedEssay
import com.example.mvp_autorskiy_start.databinding.FragmentPracticeBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class PracticeFragment : BaseFragment<FragmentPracticeBinding>(FragmentPracticeBinding::inflate) {

    private var timerSeconds = 0
    private var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val prefs by lazy { requireContext().getSharedPreferences("practice_prefs", Context.MODE_PRIVATE) }
    private val gson = Gson()
    private var draftsMap = mutableMapOf<String, PracticeDraft>()
    private var currentDraftId: String? = null

    private data class ChecklistItem(val title: String, var isChecked: Boolean)
    private lateinit var checklistItems: MutableList<ChecklistItem>
    private val checklistViews = mutableListOf<View>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimer()
        setupListeners()
        loadDrafts()
        updateStats()
        setupChecklist()
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
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateStats()
                updateChecklist(binding.etContent.text?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnHelp.setOnClickListener { showHelpDialog() }
        binding.btnSave.setOnClickListener { saveDraft() }
        binding.btnFavorite.setOnClickListener { addCurrentEssayToFavorites() }
        binding.btnClear.setOnClickListener {
            binding.etContent.text?.clear()
            binding.etTheme.text?.clear()
            Toast.makeText(requireContext(), "Поля очищены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStats() {
        val text = binding.etContent.text?.toString() ?: ""
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
        val text = binding.etContent.text?.toString()?.trim() ?: ""
        val theme = binding.etTheme.text?.toString()?.trim() ?: ""
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "Напишите хотя бы несколько слов", Toast.LENGTH_SHORT).show()
            return
        }

        val title = if (theme.isNotEmpty()) {
            theme
        } else {
            if (text.length > 30) text.substring(0, 30) + "…" else text
        }

        val now = System.currentTimeMillis()
        val draft = PracticeDraft(
            id = currentDraftId ?: UUID.randomUUID().toString(),
            title = title,
            content = text,
            theme = theme,
            lastModified = now
        )

        draftsMap[draft.id] = draft
        saveDraftsToPrefs()
        updateDraftsList()

        Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
    }

    private fun addCurrentEssayToFavorites() {
        val text = binding.etContent.text?.toString()?.trim() ?: ""
        val theme = binding.etTheme.text?.toString()?.trim() ?: ""
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
            theme = theme,
            date = System.currentTimeMillis()
        )

        FavoritesRepository.addSavedEssay(essay)
        Toast.makeText(requireContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show()
    }

    private fun loadDrafts() {
        val json = prefs.getString("drafts", null)
        draftsMap = if (json != null) {
            val trimmed = json.trim()
            if (trimmed.startsWith("[")) {
                val listType = object : TypeToken<MutableList<PracticeDraft>>() {}.type
                val list: MutableList<PracticeDraft> = gson.fromJson(json, listType) ?: mutableListOf()
                list.associateBy { it.id }.toMutableMap()
            } else {
                val mapType = object : TypeToken<MutableMap<String, PracticeDraft>>() {}.type
                gson.fromJson(json, mapType) ?: mutableMapOf()
            }
        } else {
            mutableMapOf()
        }
        updateDraftsList()
    }

    private fun updateDraftsList() {
        val draftsList = draftsMap.values.toList()
        val adapter = PracticeDraftAdapter(
            drafts = draftsList,
            onItemClick = { draft ->
                currentDraftId = draft.id
                binding.etContent.setText(draft.content)
                binding.etTheme.setText(draft.theme)
                binding.etContent.setSelection(draft.content.length)
                updateChecklist(draft.content)
            },
            onDeleteClick = { draft ->
                draftsMap.remove(draft.id)
                saveDraftsToPrefs()
                updateDraftsList()
                if (currentDraftId == draft.id) {
                    currentDraftId = null
                    binding.etContent.text?.clear()
                    binding.etTheme.text?.clear()
                }
            }
        )
        binding.rvDrafts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDrafts.adapter = adapter
    }

    private fun saveDraftsToPrefs() {
        val json = gson.toJson(draftsMap)
        prefs.edit().putString("drafts", json).apply()
    }

    private fun setupChecklist() {
        checklistItems = mutableListOf(
            ChecklistItem("1. Тезис во вступлении (главная мысль)", false),
            ChecklistItem("2. Литературный аргумент (минимум одно произведение)", false),
            ChecklistItem("3. Заключение (обобщение, вывод)", false),
            ChecklistItem("4. Объём не менее 250 слов", false),
            ChecklistItem("5. Композиция (вступление → аргумент → заключение)", false)
        )

        binding.llChecklist.removeAllViews()
        checklistViews.clear()
        for (item in checklistItems) {
            val itemView = layoutInflater.inflate(R.layout.item_checklist, binding.llChecklist, false)
            val tvTitle = itemView.findViewById<TextView>(R.id.tvChecklistTitle)
            val ivCheck = itemView.findViewById<ImageView>(R.id.ivChecklistIcon)
            tvTitle.text = item.title
            ivCheck.setImageResource(if (item.isChecked) R.drawable.ic_check_circle else R.drawable.ic_check_circle_outline)
            binding.llChecklist.addView(itemView)
            checklistViews.add(itemView)
        }
    }

    private fun updateChecklist(text: String) {
        val intro = text.take(500)
        val thesisPatterns = listOf(
            Regex("""(я (считаю|думаю|уверен|полагаю|убеждён))""", RegexOption.IGNORE_CASE),
            Regex(""".*?—\s+это\s+.*?""", RegexOption.IGNORE_CASE),
            Regex("""(что такое|почему|как вы понимаете).*?[?.!]\s*([А-ЯЁ][^.!?]+[.!?])""", RegexOption.IGNORE_CASE),
            Regex("""(несомненно|безусловно|действительно|на мой взгляд|по моему мнению)""", RegexOption.IGNORE_CASE),
            Regex("""(обращусь к (примерам|аргументам)|доказать свою точку зрения)""", RegexOption.IGNORE_CASE)
        )
        val hasThesis = thesisPatterns.any { it.containsMatchIn(intro) }
        checklistItems[0].isChecked = hasThesis

        val literaryPatterns = listOf(
            Regex("""[А-ЯЁ][а-яё]+\s+[А-ЯЁ][а-яё]+(?:ович|евна|на|ов)"""),
            Regex("""(Война и мир|Евгений Онегин|Преступление и наказание|Отцы и дети|Капитанская дочка|Герой нашего времени|Вельд)""", RegexOption.IGNORE_CASE),
            Regex("""(например|во-первых|во-вторых|в качестве примера|обратимся к|вспомним)""", RegexOption.IGNORE_CASE)
        )
        val hasLiteraryMention = literaryPatterns.any { it.containsMatchIn(text) }
        val hasArgumentStructure = Regex("""(тезис|аргумент|доказывает|подтверждает|иллюстрирует)""", RegexOption.IGNORE_CASE).containsMatchIn(text)
        checklistItems[1].isChecked = hasLiteraryMention && hasArgumentStructure

        val ending = text.takeLast(500)
        val conclusionPatterns = listOf(
            Regex("""(таким образом|итак|подводя итог|в заключение|следовательно|поэтому|как видим)""", RegexOption.IGNORE_CASE),
            Regex("""(можно сделать вывод|хочется сказать|подводя черту)""", RegexOption.IGNORE_CASE)
        )
        val hasConclusion = conclusionPatterns.any { it.containsMatchIn(ending) }
        checklistItems[2].isChecked = hasConclusion

        val wordCount = text.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        checklistItems[3].isChecked = wordCount >= 250

        val hasIntro = intro.length > 50
        val hasBody = text.length > 200
        val hasFullStructure = hasIntro && hasBody && hasConclusion && text.length > 300
        checklistItems[4].isChecked = hasFullStructure

        checklistItems.forEachIndexed { index, item ->
            val view = checklistViews[index]
            val ivCheck = view.findViewById<ImageView>(R.id.ivChecklistIcon)
            ivCheck.setImageResource(if (item.isChecked) R.drawable.ic_check_circle else R.drawable.ic_check_circle_outline)
        }

        val missing = mutableListOf<String>()
        if (!checklistItems[0].isChecked) missing.add("тезис во вступлении")
        if (!checklistItems[1].isChecked) missing.add("литературный аргумент (хотя бы один)")
        if (!checklistItems[2].isChecked) missing.add("заключение")
        if (!checklistItems[3].isChecked) missing.add("объём ≥250 слов")
        if (!checklistItems[4].isChecked) missing.add("чёткая структура (вступление-аргумент-заключение)")

        binding.tvHint.text = if (missing.isNotEmpty()) {
            "⚠️ По критериям ФИПИ не хватает: ${missing.joinToString(", ")}"
        } else {
            "✅ Отлично! Сочинение соответствует основным требованиям ФИПИ. Проверьте грамотность (≤5 ошибок на 100 слов)."
        }
        binding.tvHint.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
    }
}