package com.example.mvp_autorskiy_start.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentProfileBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import com.example.mvp_autorskiy_start.utils.MusicPlayerManager
import com.example.mvp_autorskiy_start.data.repository.ResourceMapper
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private var currentUserName: String = ""
    private var currentUserEmail: String = ""
    private var isSpinnerInitialized = false
    private var currentAvatarUri: String = ""
    private var currentAvatarRes: String = ""

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val newUri = it.toString()
                if (newUri != currentAvatarUri) {
                    App.dataStoreManager.setAvatarUri(newUri)
                    App.dataStoreManager.setAvatarResName("")
                    currentAvatarUri = newUri
                    loadAvatar()
                }
            }
            requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfileData()
        setupListeners()
    }

    private fun loadProfileData() {
        lifecycleScope.launch {
            val prefs = App.dataStoreManager
            currentUserName = prefs.getUserName().ifEmpty { "Гость" }
            currentUserEmail = prefs.getUserEmail().ifEmpty { "email@example.com" }
            binding.tvUserName.text = currentUserName
            binding.tvEmail.text = currentUserEmail
            binding.switchNotifications.isChecked = prefs.isNotificationsEnabled()
            binding.switchMusic.isChecked = prefs.isMusicEnabled()

            if (!isSpinnerInitialized) {
                val trackNames = resources.getStringArray(R.array.music_track_names)
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, trackNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTrack.adapter = adapter
                isSpinnerInitialized = true
            }
            val currentTrack = prefs.getMusicTrack()
            binding.spinnerTrack.setSelection(currentTrack)

            updateStatistics()
            loadAvatar()
        }
    }

    private fun setupListeners() {
        binding.tvUserName.setOnClickListener { showEditNameDialog() }
        binding.llEmail.setOnClickListener { showEditEmailDialog() }
        binding.ivAvatar.setOnClickListener { showAvatarDialog() }
        binding.btnAbout.setOnClickListener { showAboutDialog() }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch { App.dataStoreManager.setNotificationsEnabled(isChecked) }
        }

        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                App.dataStoreManager.setMusicEnabled(isChecked)
                if (isChecked) {
                    val trackResId = getTrackResId(App.dataStoreManager.getMusicTrack())
                    MusicPlayerManager.start(requireContext(), trackResId)
                } else {
                    MusicPlayerManager.pause()
                }
            }
        }

        binding.spinnerTrack.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                lifecycleScope.launch {
                    App.dataStoreManager.setMusicTrack(position)
                    if (App.dataStoreManager.isMusicEnabled()) {
                        MusicPlayerManager.start(requireContext(), getTrackResId(position))
                    }
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.btnClearDrafts.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Очистить черновики")
                .setMessage("Вы уверены, что хотите удалить все черновики?")
                .setPositiveButton("Да") { _, _ ->
                    lifecycleScope.launch {
                        App.dataStoreManager.setPracticeDrafts(emptyMap())
                        App.dataStoreManager.setTotalDraftsCount(0)
                        updateStatistics()
                        Toast.makeText(requireContext(), "Черновики очищены", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Нет", null)
                .show()
        }
    }

    private fun showEditNameDialog() {
        val editText = EditText(requireContext())
        editText.setText(currentUserName)
        editText.hint = "Введите имя"
        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать имя")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty() && newName != currentUserName) {
                    lifecycleScope.launch {
                        App.dataStoreManager.setUserName(newName)
                        currentUserName = newName
                        binding.tvUserName.text = newName
                        Toast.makeText(requireContext(), "Имя сохранено", Toast.LENGTH_SHORT).show()
                    }
                } else if (newName.isEmpty()) {
                    Toast.makeText(requireContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditEmailDialog() {
        val editText = EditText(requireContext())
        editText.setText(currentUserEmail)
        editText.hint = "email@example.com"
        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать email")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newEmail = editText.text.toString().trim()
                if (newEmail.isNotEmpty() && newEmail.contains('@') && newEmail != currentUserEmail) {
                    lifecycleScope.launch {
                        App.dataStoreManager.setUserEmail(newEmail)
                        currentUserEmail = newEmail
                        binding.tvEmail.text = newEmail
                        Toast.makeText(requireContext(), "Email сохранён", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Введите корректный email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAvatarDialog() {
        val avatars = arrayOf("Пушкин", "Толстой", "Достоевский", "Чехов", "Выбрать из галереи")
        AlertDialog.Builder(requireContext())
            .setTitle("Выберите аватар")
            .setItems(avatars) { _, which ->
                when (which) {
                    0 -> setPresetAvatar("pushkin_portrait")
                    1 -> setPresetAvatar("tolstoy_portrait")
                    2 -> setPresetAvatar("dostoevsky_portrait")
                    3 -> setPresetAvatar("chekhov_portrait")
                    4 -> pickImageLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun setPresetAvatar(resName: String) {
        lifecycleScope.launch {
            if (resName != currentAvatarRes) {
                App.dataStoreManager.setAvatarResName(resName)
                App.dataStoreManager.setAvatarUri("")
                currentAvatarRes = resName
                currentAvatarUri = ""
                loadAvatar()
            }
        }
    }

    private suspend fun loadAvatar() {
        val prefs = App.dataStoreManager
        val uriString = prefs.getAvatarUri()
        val resName = prefs.getAvatarResName()

        if (uriString.isNotEmpty() && uriString != currentAvatarUri) {
            currentAvatarUri = uriString
            Glide.with(this@ProfileFragment)
                .load(Uri.parse(uriString))
                .transform(CircleCrop())
                .into(binding.ivAvatar)
        } else if (resName.isNotEmpty() && resName != currentAvatarRes) {
            currentAvatarRes = resName
            val resId = ResourceMapper.getDrawableResId(resName)
            Glide.with(this@ProfileFragment)
                .load(resId)
                .transform(CircleCrop())
                .into(binding.ivAvatar)
        }
    }

    private suspend fun updateStatistics() {
        val draftsCount = App.dataStoreManager.getTotalDraftsCount()
        val wordsCount = App.dataStoreManager.getTotalWordsCount()
        binding.tvDraftsCount.text = draftsCount.toString()
        binding.tvTotalWords.text = wordsCount.toString()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Об авторских правах")
            .setMessage("Музыка: 'The Fallen' by Caleb Bryant (используется по лицензии Creative Commons Attribution)")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun getTrackResId(position: Int): Int = when (position) {
        0 -> R.raw.lofiroomcafe_blooming_serenity_lofi_chill_beat_352429
        1 -> R.raw.paulyudin_inspiring_485937
        2 -> R.raw.purrplecat_after_the_rain_360275
        3 -> R.raw.universfield_quiet_reverie_268020
        4 -> R.raw.vicatestudio_relaxing_chillhop_main_vrsion_173929
        else -> R.raw.lofiroomcafe_blooming_serenity_lofi_chill_beat_352429
    }
}