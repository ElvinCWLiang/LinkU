package com.project.linku.ui.chat

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.linku.MainActivity
import com.project.linku.data.local.*
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.project.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _friendModelList = MutableStateFlow<List<FriendModel>>(listOf())
    val friendModelList = _friendModelList.asStateFlow()

    private val TAG = "ev_" + javaClass.simpleName

    fun searchAccount(account: String) = callbackFlow {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                val userUri: Uri = Uri.parse((t as DataSnapshot).getValue(UserModel::class.java)?.useruri)
                Save.getInstance().saveUserAvatarUri(application, account, userUri)
                trySend(account)
            }

            override fun onFail() {}
        }).searchAccount(account)
    }

    fun addFriend(acc: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                syncFriendList()
                Log.i(TAG, "onSuccess")
            }
            override fun onFail() {
                Log.i(TAG, "onFail")
            }
        }).addFriend(acc)
    }

    /* get friend list from Firebase and put the returning data into Room db >> */
    fun syncFriendList() {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                if (t != null) {
                    val cacheUser = ArrayList<String>()
                    val mDataSnapshot = t as DataSnapshot
                    for (chatmaterial in mDataSnapshot.children) {
                        Log.i(TAG,"${chatmaterial.value}")
                        for (n in chatmaterial.children) {
                            try {
                                val m = n.getValue(FriendModel::class.java)
                                if (m != null) {
                                    m.id = n.key.toString()
                                }
                                m?.email?.let {
                                    if (!cacheUser.contains(it)) {
                                        syncUser(it)
                                        cacheUser.add(it)
                                    }
                                }
                                viewModelScope.launch {
                                    LocalRepository(LocalDatabase.getInstance(application)).insertFriendList(m)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "error = $e")
                            }
                        }
                    }
                    syncLocalFriendList()
                }
            }
            override fun onFail() {
                syncLocalFriendList()
            }
        }).syncFriendList()
    }

    /* get friend list from Firebase and put the returning data into Room db << */
    fun syncLocalFriendList() {
        viewModelScope.launch(Dispatchers.Main) {
            _friendModelList.emit(
                LocalRepository(LocalDatabase.getInstance(application))
                    .getFriendList(FirebaseAuth.getInstance().currentUser?.email.toString())
            )
        }
    }

    fun syncUser(acc: String) {
        viewModelScope.launch {
            FireBaseRepository(object : IFireOperationCallBack {
                override fun <T> onSuccess(t: T) {
                    //save current user
                    val userModel = (t as DataSnapshot).getValue(UserModel::class.java)
                    viewModelScope.launch {
                        LocalRepository(LocalDatabase.getInstance(application)).insertUserList(userModel)
                    }
                    userModel?.let {
                        MainActivity.userkeySet.put(userModel.email, userModel)
                    }
                }
                override fun onFail() { }
            }).syncUser(acc)
        }
    }
}