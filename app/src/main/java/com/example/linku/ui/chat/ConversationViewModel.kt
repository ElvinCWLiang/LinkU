package com.example.linku.ui.chat

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.local.FriendModel
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireBaseApiService
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.Parsefun
import com.example.linku.ui.utils.Save
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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