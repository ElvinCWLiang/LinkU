package com.example.linku.ui.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.local.FriendModel
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.parseFun
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Intrinsics


class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val text = MutableLiveData<String>().apply {
        value = "This is chat Fragment"
    }
    val _text: LiveData<String> = text

    lateinit var shouldshowSearchAccountDialog : MutableLiveData<Boolean>
    var _shouldshowSearchAccountDialog: LiveData<Boolean> = shouldshowSearchAccountDialog

    private val chatAdapterMaterial = MutableLiveData<List<FriendModel>>()

    private val mapplication: Application = application


    private val TAG = "ev_" + javaClass.simpleName
    private var userAccount = ""

    fun showDialog(acc: String?) {
        Intrinsics.checkNotNullParameter(acc, "acc")
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.chat.ChatViewModel$showDialog$1
            // com.example.linku.data.remote.IFireOperationCallBack
            override fun <T> onSuccess(t: T) {
                shouldshowSearchAccountDialog.setValue(true)
                Log.i(TAG, "onSuccess")
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {
                Log.i(TAG, "onFail")
                shouldshowSearchAccountDialog.setValue(false)
            }
        }).searchAccount(acc)
    }

    fun afterAccountChange(s: CharSequence) {
        userAccount = s.toString()
    }

    fun addfriend() {
        Log.i(TAG, "addfriend")
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.chat.ChatViewModel$addfriend$1
            // com.example.linku.data.remote.IFireOperationCallBack
            override fun <T> onSuccess(t: T) {
                syncFriendList()
                Log.i(TAG, "onSuccess")
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {
                Log.i(TAG, "onFail")
            }
        }).addFriend(userAccount)
    }

    fun syncFriendList() {
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.chat.ChatViewModel$syncFriendList$1
            // com.example.linku.data.remote.IFireOperationCallBack
            override fun <T> onSuccess(t: T) {
                if (t != null) {
                    val mDataSnapshot = t as DataSnapshot
                    for (chatmaterial in mDataSnapshot.children) {
                        for (n in chatmaterial.children) {
                            val m = n.getValue(FriendModel::class.java)
                            if (m != null) {
                                m.id = n.key.toString()
                            }
                            if (m != null) {
                                val parsefun = parseFun()
                                val email = m.email
                                m.email = parsefun.parseAccountasEmail(email)
                            }
                            LocalRepository(LocalDatabase.getInstance(mapplication)).insertFriendList(m)
                        }
                    }
                    fetchlocalFriendList()
                    return
                }
                throw NullPointerException("null cannot be cast to non-null type com.google.firebase.database.DataSnapshot")
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {}
        }).syncFriendList()
        fetchlocalFriendList()
    }

    fun fetchlocalFriendList() {
        LocalRepository(LocalDatabase.getInstance(mapplication)).getFreindList()
    }


}