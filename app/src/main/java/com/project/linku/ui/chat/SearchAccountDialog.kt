package com.project.linku.ui.chat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.ui.utils.GlideApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.linku.data.local.FriendModel
import kotlinx.android.synthetic.main.dialog_search_account.*

class SearchAccountDialog(
    context: Context,
    private val account: String,
    private val friendList: List<FriendModel>,
    private val addFriend: (String) -> Unit,
    private val showMessage: (String) -> Unit
) : Dialog(context), View.OnClickListener {
    val TAG = "ev_" + javaClass.simpleName

    override fun onClick(v: View) {
        if (v.id == R.id.btn_addfriend) {
            val currentUser: FirebaseUser? = Firebase.auth.currentUser
            friendList.find {
                it.email == account
            }?.let {
                showMessage.invoke(context.resources.getString(R.string.friend_exist))
                dismiss()
                return
            }

            Log.i(TAG, "$currentUser, ${currentUser?.email}")
            if (currentUser != null && currentUser.email != account) {
                addFriend.invoke(account)
//                chatViewModel.addFriend(acc)
            } else {
                showMessage.invoke(context.resources.getString(R.string.cant_add_yourself))
            }
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search_account)
        btn_addfriend.setOnClickListener(this)
        txv_introduction.text = MainActivity.userkeySet[account]?.userintroduction
        GlideApp.with(context).load(MainActivity.userkeySet[account]?.useruri).placeholder(R.drawable.cat).circleCrop().into(img_avatar)
    }
}