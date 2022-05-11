package com.example.linku.ui.chat

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.linku.ui.dashboard.ChatViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlin.jvm.internal.Intrinsics

class SearchAccountDialog(context: Context?, _chatViewModel: ChatViewModel, _acc: String) :
    Dialog(context!!), View.OnClickListener {
    val acc: String
    val chatViewModel: ChatViewModel
    val TAG = "ev_" + javaClass.simpleName

    override fun onClick(v: View) {
        Intrinsics.checkNotNullParameter(v, "v")
        if (v.getId() === R.id.btn_addfriend) {
            val str = acc
            val currentUser: FirebaseUser = auth.getAuth(Firebase).getCurrentUser()
            if (!Intrinsics.areEqual(str, if (currentUser != null) currentUser.email else null)) {
                chatViewModel.addfriend()
            } else {
                Toast.makeText(getContext(), "can't add friend by yourself", Toast.LENGTH_SHORT)
            }
            Log.i(TAG, "btn_addfriend")
        }
    }

    // android.app.Dialog
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search_account)
        (findViewById(R.id.btn_addfriend) as Button).setOnClickListener(this)
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    init {
        Intrinsics.checkNotNullParameter(context, "context")
        Intrinsics.checkNotNullParameter(_chatViewModel, "_chatViewModel")
        Intrinsics.checkNotNullParameter(_acc, "_acc")
        acc = _acc
        chatViewModel = _chatViewModel
    }
}