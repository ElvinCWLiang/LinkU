package com.example.linku.ui.chat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.linku.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_search_account.*

class SearchAccountDialog(context: Context, _chatViewModel: ChatViewModel) :
    Dialog(context), View.OnClickListener {
    val TAG = "ev_" + javaClass.simpleName
    private val chatViewModel = _chatViewModel

    override fun onClick(v: View) {
        if (v.getId() === R.id.btn_addfriend) {
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