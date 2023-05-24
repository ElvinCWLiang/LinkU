package com.project.linku.ui.chat

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.linku.MainActivity
import com.project.linku.data.local.*
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.project.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.project.linku.data.remote.FirebaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository : FireBaseRepository,
    private val application: Application
) : ViewModel() {
    private val _friendModelList = MutableStateFlow<List<FriendModel>>(listOf())
    val friendModelList = _friendModelList.asStateFlow()

    private val tag = "ev_" + javaClass.simpleName

    fun searchAccount(account: String) = callbackFlow {
        repository.searchAccount(account).collectLatest {
            when (it) {
                is FirebaseResult.Success -> {
                    val userUri: Uri = Uri.parse(it.data.getValue(UserModel::class.java)?.useruri)
                    Save.saveUserAvatarUri(application, account, userUri)
                    trySend(account)
                }
                else -> {}
            }
        }
        awaitClose()
    }

    fun addFriend(account: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.addFriend(account).collectLatest {
            when (it) {
                is FirebaseResult.Success -> {
                    syncFriendList()
                }
                else -> {}
            }
        }
    }

    /* get friend list from Firebase and put the returning data into Room db >> */
    fun syncFriendList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncFriendList().collectLatest {
                when (it) {
                    is FirebaseResult.Success -> {
                        val cacheUser = ArrayList<String>()
                        for (chatMaterial in it.data.children) {
                            Log.i(tag, "${chatMaterial.value}")
                            for (n in chatMaterial.children) {
                                try {
                                    val friendModel = n.getValue(FriendModel::class.java)
                                    if (friendModel != null) {
                                        friendModel.id = n.key.toString()
                                    }
                                    friendModel?.email?.let { email ->
                                        if (!cacheUser.contains(email)) {
                                            syncUser(email)
                                            cacheUser.add(email)
                                        }
                                    }
                                    viewModelScope.launch {
                                        LocalRepository(LocalDatabase.getInstance(application)).insertFriendList(friendModel)
                                    }
                                } catch (e: Exception) {
                                    Log.e(tag, "error = $e")
                                }
                            }
                        }
                        syncLocalFriendList()
                    }
                    else -> {}
                }
            }
        }
    }

    /* get friend list from Firebase and put the returning data into Room db << */
    private fun syncLocalFriendList() {
        viewModelScope.launch(Dispatchers.Main) {
            _friendModelList.emit(
                LocalRepository(LocalDatabase.getInstance(application))
                    .getFriendList(FirebaseAuth.getInstance().currentUser?.email.toString())
            )
        }
    }

    private fun syncUser(acc: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.syncUser(acc).collectLatest {
            when (it) {
                is FirebaseResult.Success -> {
                    //save current user
                    val userModel = it.data.getValue(UserModel::class.java)
                    LocalRepository(LocalDatabase.getInstance(application)).insertUserList(userModel)
                    userModel?.let {
                        MainActivity.userkeySet[userModel.email] = userModel
                    }
                }
                else -> {}
            }
        }
    }
}