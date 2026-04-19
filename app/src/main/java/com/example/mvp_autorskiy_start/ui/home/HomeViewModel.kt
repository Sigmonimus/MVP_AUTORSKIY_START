package com.example.mvp_autorskiy_start.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.data.repository.HomeDataRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val quoteText = MutableLiveData<String>()
    val quoteAuthor = MutableLiveData<String>()
    val tip = MutableLiveData<String>()
    val currentStreak = MutableLiveData<Int>()
    val bestStreak = MutableLiveData<Int>()
    val showFireworks = MutableLiveData<Boolean>()

    private val context = getApplication<Application>().applicationContext

    init {
        loadQuoteAndTip()
    }

    private fun loadQuoteAndTip() {
        val (text, author) = HomeDataRepository.getRandomQuote(context)
        quoteText.value = "«$text»"
        quoteAuthor.value = "— $author"
        tip.value = HomeDataRepository.getRandomTip(context)
    }

    fun updateStreak() {
        viewModelScope.launch {
            val shouldCelebrate = HomeDataRepository.updateStreak()
            val newStreak = App.dataStoreManager.getCurrentStreak()
            currentStreak.value = newStreak
            bestStreak.value = App.dataStoreManager.getBestStreak()

            if (shouldCelebrate) {
                showFireworks.value = true
            } else {
                showFireworks.value = false
            }
        }
    }
}