package com.example.mvp_autorskiy_start.ui.profile

import android.R
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mvp_autorskiy_start.data.models.PracticeDraft
import com.example.mvp_autorskiy_start.databinding.FragmentProfileBinding
import com.example.mvp_autorskiy_start.utils.MusicPlayerManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy { requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    private val gson = Gson()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            saveAvatarUri(it)
            loadAvatarFromUri(it)
        } ?: run {
            Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ---------- МУЗЫКА ----------
        val switchMusic = binding.switchMusic
        var isSwitchInitialized = false
        switchMusic.isChecked = MusicPlayerManager.isEnabled()
        switchMusic.setOnCheckedChangeListener { _, isChecked ->
            if (isSwitchInitialized) {
                MusicPlayerManager.setEnabled(isChecked)
            } else {
                isSwitchInitialized = true
            }
        }

        // Настройка спиннера выбора трека
        val trackNames = MusicPlayerManager.getTracks().map { it.name }
        if (trackNames.isNotEmpty()) {
            val trackAdapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, trackNames)
            trackAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerTrack.adapter = trackAdapter
            binding.spinnerTrack.setSelection(MusicPlayerManager.getCurrentTrackIndex())

            var isSpinnerInitialized = false
            binding.spinnerTrack.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (isSpinnerInitialized) {
                        MusicPlayerManager.setTrack(position)
                    } else {
                        isSpinnerInitialized = true
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            MusicPlayerManager.setOnTrackChanged { index, _ ->
                binding.spinnerTrack.setSelection(index)
            }
        } else {
            // Нет треков – скрываем спиннер
            binding.spinnerTrack.visibility = View.GONE
        }

        // ---------- ЗАГРУЗКА ДАННЫХ ----------
        loadUserInfo()
        loadAvatar()
        loadStatistics()
        loadSettings()

        // Редактирование имени по клику на текст
        binding.tvUserName.setOnClickListener {
            showEditNameDialog()
        }

        // Редактирование email по клику на блок
        binding.llEmail.setOnClickListener { showEditEmailDialog() }

        // Выбор аватара из галереи
        binding.ivAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Остальные настройки
        binding.llLanguage.setOnClickListener { showLanguageDialog() }
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply()
        }
        binding.btnClearDrafts.setOnClickListener { showClearDraftsConfirmation() }
    }

    // ---------- ЗАГРУЗКА ДАННЫХ ----------
    private fun loadUserInfo() {
        val userName = prefs.getString("user_name", "Александр Сергеевич")
        binding.tvUserName.text = userName

        val email = prefs.getString("user_email", "pushkin@example.com")
        binding.tvEmail.text = email
    }

    private fun loadAvatar() {
        val avatarUriString = prefs.getString("avatar_uri", null)
        if (!avatarUriString.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(avatarUriString)
                loadAvatarFromUri(uri)
                return
            } catch (e: Exception) {
                prefs.edit().remove("avatar_uri").apply()
            }
        }

        val avatar = prefs.getString("avatar", "pushkin")
        setAvatarImage(avatar ?: "pushkin")
    }

    private fun loadAvatarFromUri(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .placeholder(com.example.mvp_autorskiy_start.R.drawable.outline_account_circle_24)
            .into(binding.ivAvatar)
    }

    private fun saveAvatarUri(uri: Uri) {
        val uriString = uri.toString()
        prefs.edit().putString("avatar_uri", uriString).apply()
        prefs.edit().remove("avatar").apply()
    }

    private fun setAvatar(avatarName: String) {
        prefs.edit().putString("avatar", avatarName).apply()
        prefs.edit().remove("avatar_uri").apply()
        setAvatarImage(avatarName)
    }

    private fun setAvatarImage(avatarName: String) {
        val resId = when (avatarName) {
            "pushkin" -> com.example.mvp_autorskiy_start.R.drawable.ic_pushkin
            "tolstoy" -> com.example.mvp_autorskiy_start.R.drawable.ic_tolstoy
            "dostoevsky" -> com.example.mvp_autorskiy_start.R.drawable.ic_dostoevsky
            "chekhov" -> com.example.mvp_autorskiy_start.R.drawable.ic_chekhov
            else -> com.example.mvp_autorskiy_start.R.drawable.outline_account_circle_24
        }
        binding.ivAvatar.setImageResource(resId)
    }

    private fun loadStatistics() {
        val drafts = getDraftsFromPrefs()
        binding.tvDraftsCount.text = drafts.size.toString()

        val totalWords = drafts.sumOf { draft ->
            draft.content.split(Regex("\\s+")).count { it.isNotEmpty() }
        }
        binding.tvTotalWords.text = totalWords.toString()
    }

    private fun loadSettings() {
        val notifications = prefs.getBoolean("notifications_enabled", true)
        binding.switchNotifications.isChecked = notifications
    }

    private fun getDraftsFromPrefs(): List<PracticeDraft> {
        val prefsPractice = requireContext().getSharedPreferences("practice_prefs", Context.MODE_PRIVATE)
        val json = prefsPractice.getString("drafts", null)
        return if (json != null) {
            val trimmed = json.trim()
            if (trimmed.startsWith("[")) {
                // Старый формат: список
                val listType = object : TypeToken<MutableList<PracticeDraft>>() {}.type
                gson.fromJson(json, listType) ?: emptyList()
            } else {
                // Новый формат: Map
                val mapType = object : TypeToken<MutableMap<String, PracticeDraft>>() {}.type
                val map: MutableMap<String, PracticeDraft> = gson.fromJson(json, mapType) ?: mutableMapOf()
                map.values.toList()
            }
        } else {
            emptyList()
        }
    }

    // ---------- ДИАЛОГИ ----------
    private fun showEditNameDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.example.mvp_autorskiy_start.R.layout.dialog_edit_name, null)
        val editText = dialogView.findViewById<EditText>(com.example.mvp_autorskiy_start.R.id.etName)
        editText.setText(prefs.getString("user_name", ""))

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать имя")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    prefs.edit().putString("user_name", newName).apply()
                    binding.tvUserName.text = newName
                    Toast.makeText(requireContext(), "Имя сохранено", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditEmailDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.example.mvp_autorskiy_start.R.layout.dialog_edit_email, null)
        val editText = dialogView.findViewById<EditText>(com.example.mvp_autorskiy_start.R.id.etEmail)
        editText.setText(prefs.getString("user_email", ""))

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать email")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newEmail = editText.text.toString().trim()
                if (newEmail.isNotEmpty() && newEmail.contains("@") && newEmail.contains(".")) {
                    prefs.edit().putString("user_email", newEmail).apply()
                    binding.tvEmail.text = newEmail
                    Toast.makeText(requireContext(), "Email сохранён", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Введите корректный email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showLanguageDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выберите язык")
            .setItems(arrayOf("Русский")) { _, _ ->
                // пока только русский
            }
            .show()
    }

    private fun showClearDraftsConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистить все черновики")
            .setMessage("Вы уверены? Все сохранённые работы будут удалены без возможности восстановления.")
            .setPositiveButton("Удалить") { _, _ ->
                val prefsPractice = requireContext().getSharedPreferences("practice_prefs", Context.MODE_PRIVATE)
                prefsPractice.edit().remove("drafts").apply()
                loadStatistics()
                Toast.makeText(requireContext(), "Все черновики удалены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}