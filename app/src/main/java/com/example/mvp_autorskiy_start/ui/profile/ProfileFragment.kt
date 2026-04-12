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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                App.dataStoreManager.setAvatarUri(it.toString())
                App.dataStoreManager.setAvatarResName("")
                loadAvatar()
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
            currentUserName = prefs.getUserName()
            currentUserEmail = prefs.getUserEmail()
            binding.etName.setText(currentUserName)
            binding.etEmail.setText(currentUserEmail)
            binding.switchNotifications.isChecked = prefs.isNotificationsEnabled()
            binding.switchMusic.isChecked = prefs.isMusicEnabled()

            // Используем массив из ресурсов
            val trackNames = resources.getStringArray(R.array.music_track_names)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, trackNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMusicTrack.adapter = adapter
            val currentTrack = prefs.getMusicTrack()
            binding.spinnerMusicTrack.setSelection(currentTrack)

            updateStatistics()
            loadAvatar()
        }
    }

    private fun setupListeners() {
        binding.btnEditName.setOnClickListener { showEditNameDialog() }
        binding.btnEditEmail.setOnClickListener { showEditEmailDialog() }
        binding.ivAvatar.setOnClickListener { showAvatarDialog() }

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

        binding.spinnerMusicTrack.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
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
    }

    private fun showEditNameDialog() {
        val editText = EditText(requireContext())
        editText.setText(currentUserName)
        AlertDialog.Builder(requireContext())
            .setTitle("Введите имя")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = editText.text.toString().trim()
                lifecycleScope.launch {
                    App.dataStoreManager.setUserName(newName)
                    currentUserName = newName
                    binding.etName.setText(newName)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditEmailDialog() {
        val editText = EditText(requireContext())
        editText.setText(currentUserEmail)
        AlertDialog.Builder(requireContext())
            .setTitle("Введите email")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newEmail = editText.text.toString().trim()
                lifecycleScope.launch {
                    App.dataStoreManager.setUserEmail(newEmail)
                    currentUserEmail = newEmail
                    binding.etEmail.setText(newEmail)
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
            App.dataStoreManager.setAvatarResName(resName)
            App.dataStoreManager.setAvatarUri("")
            loadAvatar()
        }
    }

    private suspend fun loadAvatar() {
        val prefs = App.dataStoreManager
        val uriString = prefs.getAvatarUri()
        val resName = prefs.getAvatarResName()

        if (uriString.isNotEmpty()) {
            Glide.with(this@ProfileFragment)
                .load(Uri.parse(uriString))
                .transform(CircleCrop())
                .into(binding.ivAvatar)
        } else {
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
        binding.tvStats.text = "Черновиков: $draftsCount\nВсего слов: $wordsCount"
    }

    private fun getTrackResId(position: Int): Int = when (position) {
        0 -> R.raw.lofiroomcafe_blooming_serenity_lofi_chill_beat_352429
        1 -> R.raw.paulyudin_inspiring_485937
        else -> R.raw.purrplecat_after_the_rain_360275
    }
}