package com.example.mvp_autorskiy_start.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp_autorskiy_start.data.repository.HomeDataRepository
import kotlinx.coroutines.launch

data class Quote(val text: String, val author: String)

class HomeViewModel : ViewModel() {

    private val _randomQuote = MutableLiveData<Quote>()
    val randomQuote: LiveData<Quote> = _randomQuote

    private val _randomTip = MutableLiveData<String>()
    val randomTip: LiveData<String> = _randomTip

    fun loadData(context: android.content.Context) {
        viewModelScope.launch {
            val quoteText = HomeDataRepository.getRandomQuote(context)
            val parts = quoteText.split("\n— ")
            val quote = if (parts.size == 2) Quote(parts[0].trim('"'), parts[1]) else Quote(quoteText, "")
            _randomQuote.postValue(quote)

            val tip = HomeDataRepository.getRandomTip(context)
            _randomTip.postValue(tip)
        }
    }
}