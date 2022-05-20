package com.example.linku.ui.chat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.linku.MainActivity
import com.example.linku.R
import com.example.linku.ui.utils.GlideApp
import com.example.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_search_account.*

class SearchAccountDialog(context: Context, _chatViewModel: ChatViewModel, _acc: String) :
    Dialog(context), View.OnClickListener {
    val TAG = "ev_" + javaClass.simpleName
    private val chatViewModel = _chatViewModel
    private val acc = _acc
    private val mContext = context

    override fun onClick(v: View) {
        if (v.getId() === R.id.btn_addfriend) {
            val currentUser: FirebaseUser? = Firebase.auth.currentUser
            val modellist = chatViewModel.chatAdapterMaterial.value
            modellist?.let {
                for (i in modellist.indices) {
                    if (modellist[i].email == acc)
                        Toast.makeText(mContext, "Friend already exist", Toast.LENGTH_SHORT).show()
                        dismiss()
                        return
                }
            }
            if (currentUser != null && currentUser.email != acc) {
                Log.i(TAG,"if")
                chatViewModel.addfriend()
            } else {
                Log.i(TAG,"else")
                Toast.makeText(mContext, "Can't add yourself", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search_account)
        btn_addfriend.setOnClickListener(this)
        txv_introduction.text = MainActivity.userkeySet.get(acc)?.userintroduction
        GlideApp.with(context).load(MainActivity.userkeySet.get(acc)?.useruri).placeholder(R.drawable.cat).into(img_avatar)
    }


}