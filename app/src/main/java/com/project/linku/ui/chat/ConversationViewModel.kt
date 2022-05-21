package com.project.linku.ui.chat

import android.app.Application
import android.net.Uri
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.linku.data.local.FriendModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ConversationViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ev_" + javaClass.simpleName
    val _conversationAdapterMaterial = MutableLiveData<List<FriendModel>>()
    val conversationAdapterMaterial : LiveData<List<FriendModel>> = _conversationAdapterMaterial
    var macc: String? = null
    val mapplication = application
    var remoteAccount = ""
    var localAccount = Firebase.auth.currentUser?.email!!
    val userMessage = MutableLiveData<String>().apply { value = "" }

    /* sync specific account conversation on Firebase */
    fun syncConversation(acc: String) {
        remoteAccount = acc
        FireBaseRepository(null).syncConversation(acc, object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(FriendModel::class.java)
                m?.let {
                    m.id = snapshot.key.toString()
                    Log.i(TAG,"id = ${m.id},  emailto = ${m.email}, emailfrom = ${m.emailfrom},  content = ${m.content}")
                }
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertFriendList(m)
                fetchlocalConversation(remoteAccount)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        fetchlocalConversation(remoteAccount)
    }

    fun onTextChange(editable: Editable?) {
        userMessage.value = editable.toString()
    }

    /* fetch account conversation on local db */
    fun fetchlocalConversation(remoteAccount: String) {
        Log.i(TAG, "remoteAccount = $remoteAccount")
        GlobalScope.launch(IO) {
            _conversationAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getConversaion(remoteAccount, localAccount))
        }
    }

    // type 0 = text, type 1 = image
    fun send(type: Int) {
        FireBaseRepository(null).send(userMessage.value!!, remoteAccount, type)
        Log.i(TAG,"userReply = ${userMessage!!.value}, remoteAccount = $remoteAccount")
        userMessage.value = ""
    }

    fun send(imagePath: Uri) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                FireBaseRepository(null).send(t.toString(), remoteAccount, 1)
            }
            override fun onFail() {}
        }).send(imagePath)
    }

}