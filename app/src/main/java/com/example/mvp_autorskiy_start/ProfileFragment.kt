package com.example.mvp_autorskiy_start.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mvp_autorskiy_start.ui.practice.PracticeDraft

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy { requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    private val gson = Gson()

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


        loadUserInfo()
        loadAvatar()
        loadStatistics()
        loadSettings()
        binding.btnEditEmail.setOnClickListener { showEditEmailDialog() }
        binding.btnEditName.setOnClickListener { showEditNameDialog() }

        binding.avatarPushkin.setOnClickListener { setAvatar("pushkin") }
        binding.avatarTolstoy.setOnClickListener { setAvatar("tolstoy") }
        binding.avatarDostoevsky.setOnClickListener { setAvatar("dostoevsky") }
        binding.avatarChekhov.setOnClickListener { setAvatar("chekhov") }

        binding.btnChangeLanguage.setOnClickListener { showLanguageDialog() }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply()
        }

        binding.btnClearDrafts.setOnClickListener { showClearDraftsConfirmation() }
    }

    private fun loadUserInfo() {
        val userName = prefs.getString("user_name", "Александр Сергеевич")
        binding.tvUserName.text = userName

        val email = prefs.getString("user_email", "pushkin@example.com")
        binding.tvEmail.text = email
    }

    private fun loadAvatar() {
        val avatar = prefs.getString("avatar", null) ?: "pushkin"
        setAvatarImage(avatar)
    }

    private fun setAvatar(avatarName: String) {
        prefs.edit().putString("avatar", avatarName).apply()
        setAvatarImage(avatarName)
    }

    private fun setAvatarImage(avatarName: String) {
        val resId = when (avatarName) {
            "pushkin" -> R.drawable.ic_pushkin
            "tolstoy" -> R.drawable.ic_tolstoy
            "dostoevsky" -> R.drawable.ic_dostoevsky
            "chekhov" -> R.drawable.ic_chekhov
            else -> R.drawable.outline_expand_circle_right_24
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
            val type = object : TypeToken<MutableList<PracticeDraft>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun showEditNameDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_name, null)
        val editText = dialogView.findViewById<EditText>(R.id.etName)
        editText.setText(prefs.getString("user_name", ""))

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать профиль")
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

    private fun showLanguageDialog() {
        // Пока только русский, но можно расширить
        AlertDialog.Builder(requireContext())
            .setTitle("Выберите язык")
            .setItems(arrayOf("Русский")) { _, _ ->
                // ничего не меняем
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
    private fun showEditEmailDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_email, null)
        val editText = dialogView.findViewById<EditText>(R.id.etEmail)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}