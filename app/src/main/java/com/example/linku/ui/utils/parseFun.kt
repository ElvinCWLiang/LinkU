package com.example.linku.ui.utils

import android.util.Log
import java.text.SimpleDateFormat
import kotlin.jvm.internal.Intrinsics

class parseFun {
    private val TAG = "ev_" + javaClass.simpleName

    fun parseEmailasAccount(email: String?): String {
        Log.i(TAG, email!!)
        return email.replace(".","__dot__")
    }

    fun parseAccountasEmail(email: String?): String {
        Log.i(TAG, email!!)
        return email.replace("__dot__",".")
    }

    fun parseSecondsToDate(seconds: Long?): String {
        val format: String = SimpleDateFormat("EEE, d MMM yyyy").format(seconds)
        return format
    }

}