package com.example.linku.ui.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.linku.MainActivity
import com.example.linku.databinding.FragmentDashboardBinding

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var mContext: Context? = application

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val _text = MutableLiveData<Boolean>().apply {
        value = true
    }
    var text: MutableLiveData<Boolean> = _text

    fun change () {
        text.value = false
    }




}