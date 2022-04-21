package com.example.linku.ui.lottery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LotteryViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is mainpage fragment"
    }

    val text: LiveData<String> = _text


}