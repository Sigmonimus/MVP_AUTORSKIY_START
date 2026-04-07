package com.example.mvp_autorskiy_start.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp_autorskiy_start.data.repository.HomeDataRepository
import com.example.mvp_autorskiy_start.data.repository.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val _randomQuote = MutableLiveData<Quote>()
    val randomQuote: LiveData<Quote> = _randomQuote

    private val _randomTip = MutableLiveData<String>()
    val randomTip: LiveData<String> = _randomTip

    fun loadData(context: Context) {
        viewModelScope.launch {
            val quote = withContext(Dispatchers.IO) {
                HomeDataRepository.getRandomQuote(context)
            }
            _randomQuote.postValue(quote)

            val tip = withContext(Dispatchers.IO) {
                HomeDataRepository.getRandomTip(context)
            }
            _randomTip.postValue(tip)
        }
    }
}