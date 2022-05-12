package com.example.linku.ui.chat

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.linku.ui.dashboard.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_search_account.*
import kotlin.jvm.internal.Intrinsics

class SearchAccountDialog(context: Context?, _chatViewModel: ChatViewModel, _acc: String) :
    Dialog(context!!), View.OnClickListener {
    val acc: String
    val chatViewModel: ChatViewModel
    val TAG = "ev_" + javaClass.simpleName

    init {
        acc = _acc
        chatViewModel = _chatViewModel
    }

    override fun onClick(v: View) {
        if (v.getId() === R.id.btn_addfriend) {
            val str = acc
            val currentUser: FirebaseUser? = Firebase.auth.currentUser
            if (currentUser != null) {
                chatViewModel.addfriend()
            } else {
                Toast.makeText(getContext(), "can't add friend by yourself", Toast.LENGTH_SHORT)
            }
            Log.i(TAG, "btn_addfriend")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search_account)
        btn_addfriend.setOnClickListener(this)
    }


}