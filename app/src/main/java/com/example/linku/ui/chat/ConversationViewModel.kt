package com.example.linku.ui.chat

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.local.FriendModel
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.ui.utils.Parsefun
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Intrinsics


class ConversationViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ev_" + javaClass.simpleName
    val _conversationAdapterMaterial = MutableLiveData<List<FriendModel>>()
    val conversationAdapterMaterial : LiveData<List<FriendModel>> = _conversationAdapterMaterial
    var macc: String? = null
    val mapplication = application
    var remoteAccount = ""
    val userMessage = MutableLiveData<String>().apply { value = "" }

    fun syncConversation(acc: String) {
        remoteAccount = acc
        FireBaseRepository(null).syncConversation(acc, object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(FriendModel::class.java)
                m?.let {
                    m.id = snapshot.key.toString()
                    m.email = Parsefun.getInstance().parseAccountasEmail(m.email)
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

    fun fetchlocalConversation(acc: String?) {
        GlobalScope.launch(IO) {
            _conversationAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getConversaion(acc))
        }
    }

    fun send() {
        FireBaseRepository(null).send(userMessage.value!!, remoteAccount)
        Log.i(TAG,"userReply = ${userMessage!!.value}, remoteAccount = $remoteAccount")
        userMessage.value = ""
    }
}