package com.project.linku.ui.chat

import android.app.Application
import android.net.Uri
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.linku.data.local.FriendModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.remote.FireBaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import com.project.linku.data.remote.FirebaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val repository : FireBaseRepository,
    private val application: Application
) : ViewModel() {
    private val tag = "ev_" + javaClass.simpleName
    private val _conversationAdapterMaterial = MutableLiveData<List<FriendModel>>()
    val conversationAdapterMaterial : LiveData<List<FriendModel>> = _conversationAdapterMaterial
    private val _callStatus = MutableLiveData<Pair<Int, String>>()
    val callStatus : LiveData<Pair<Int, String>> = _callStatus
    var remoteAccount = ""
    private var localAccount = Firebase.auth.currentUser?.email!!
    val userMessage = MutableLiveData<String>().apply { value = "" }

    /* sync specific account conversation on Firebase */
    fun syncConversation(acc: String) {
        remoteAccount = acc
        repository.syncConversation(acc, object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val friendModel = snapshot.getValue(FriendModel::class.java)
                    friendModel?.let {
                        friendModel.id = snapshot.key.toString()
                    }
                    viewModelScope.launch {
                        LocalRepository(LocalDatabase.getInstance(application)).insertFriendList(friendModel)
                    }
                    fetchLocalConversation(remoteAccount)
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
        )
        fetchLocalConversation(remoteAccount)
    }

    fun onTextChange(editable: Editable?) {
        userMessage.value = editable.toString()
    }

    /* fetch account conversation on local db */
    fun fetchLocalConversation(remoteAccount: String) {
        viewModelScope.launch(IO) {
            _conversationAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(application)).getConversaion(remoteAccount, localAccount))
        }
    }

    /* type 0 = text, type 1 = image, type 2 = voice call, type 3 = video call */
    fun send(type: Int) {
        userMessage.value?.let {
            repository.send(it, remoteAccount, type)
            Log.i(tag,"userReply = ${userMessage.value}, remoteAccount = $remoteAccount")
            userMessage.value = ""
        }
    }

    /* type 0 = text, type 1 = image, type 2 = voice call, type 3 = video call */
    fun send(type: Int, channel: String) = viewModelScope.launch(IO) {
        repository.send(channel, remoteAccount, type).collectLatest {
            if (it is FirebaseResult.Success) _callStatus.postValue(Pair(type, channel))
        }
        Log.i(tag,"channel = $channel, remoteAccount = $remoteAccount")
    }

    fun send(imagePath: Uri) = viewModelScope.launch(IO)  {
        repository.send(imagePath).collectLatest {
            when (it) {
                is FirebaseResult.Success -> {
                    repository.send(it.toString(), remoteAccount, 1 /* image */)
                }
                else -> {}
            }
        }
    }
}