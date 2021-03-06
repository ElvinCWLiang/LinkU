package com.project.linku.ui.chat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.ui.utils.GlideApp
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
        if (v.id == R.id.btn_addfriend) {
            val currentUser: FirebaseUser? = Firebase.auth.currentUser
            val modellist = chatViewModel.chatAdapterMaterial.value
            modellist?.let {
                for (i in modellist.indices) {
                    if (modellist[i].email == acc) {
                        Toast.makeText(mContext, context.resources.getString(R.string.friend_exist), Toast.LENGTH_SHORT).show()
                        dismiss()
                        return
                    }
                }
            }
            Log.i(TAG, "$currentUser, ${currentUser?.email}")
            if (currentUser != null && currentUser.email != acc) {
                chatViewModel.addfriend(acc)
            } else {
                Toast.makeText(mContext, context.resources.getString(R.string.cant_add_yourself), Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search_account)
        btn_addfriend.setOnClickListener(this)
        txv_introduction.text = MainActivity.userkeySet.get(acc)?.userintroduction
        GlideApp.with(context).load(MainActivity.userkeySet.get(acc)?.useruri).placeholder(R.drawable.cat).circleCrop().into(img_avatar)
    }
}