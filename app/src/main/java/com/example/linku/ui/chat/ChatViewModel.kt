package com.example.linku.ui.chat

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.MainActivity
import com.example.linku.data.local.*
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChatViewModel(application: Application) : AndroidViewModel(application) {

    val _shouldshowSearchAccountDialog = MutableLiveData<String>()
    val shouldshowSearchAccountDialog: LiveData<String> = _shouldshowSearchAccountDialog

    val _chatAdapterMaterial = MutableLiveData<List<FriendModel>>()
    val chatAdapterMaterial: LiveData<List<FriendModel>> = _chatAdapterMaterial

    private val mapplication: Application = application

    private val TAG = "ev_" + javaClass.simpleName

    var acc = ""

    fun showDialog() {
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.chat.ChatViewModel$showDialog$1
            // com.example.linku.data.remote.IFireOperationCallBack
            override fun <T> onSuccess(t: T) {
                val userUri: Uri = Uri.parse((t as DataSnapshot).getValue(UserModel::class.java)?.useruri)
                Save.getInstance().saveUserAvatarUri(mapplication, acc, userUri)
                _shouldshowSearchAccountDialog.value = acc
                Log.i(TAG, "onSuccess")
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {
                Log.i(TAG, "onFail")
            }
        }).searchAccount(acc)
    }

    fun addfriend() {
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
                            LocalRepository(LocalDatabase.getInstance(mapplication)).insertFriendList(m)
                        }
                    }
                    synclocalFriendList()
                }
            }
            override fun onFail() {}
        }).syncFriendList()

        synclocalFriendList()
    }
    /* get friend list from Firebase and put the returning data into Room db << */

    fun synclocalFriendList() {
        GlobalScope.launch(Dispatchers.IO) {
            _chatAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getFriendList(FirebaseAuth.getInstance().currentUser?.email.toString()))
        }
    }

    fun syncUser(acc: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                //save current user
                val userModel = (t as DataSnapshot).getValue(UserModel::class.java)
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList(userModel)
                userModel?.let {
                    MainActivity.userkeySet.put(userModel.email, userModel)
                }
            }
            override fun onFail() { }
        }).syncUser(acc)
    }
}