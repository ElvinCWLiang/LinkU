package com.project.linku.ui.dashboard

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.local.UserModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.project.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.project.linku.ui.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var mapplication = application
    private val TAG = "ev_" + javaClass.simpleName
    private val _userAccount = MutableLiveData<String>().apply { value = FirebaseAuth.getInstance().currentUser?.email }
    val userAccount : LiveData<String> = _userAccount
    private val _updateRespond = MutableLiveData<Event<String>>()
    val updateRespond : LiveData<Event<String>> = _updateRespond.distinctUntilChanged()
    private val _isAvatarChanged = MutableLiveData<Uri>()
    val isAvatarChanged : LiveData<Uri> = _isAvatarChanged
    private val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val _introduction = MutableLiveData<String>().apply {
        GlobalScope.launch(Dispatchers.IO) {
            postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getUser(currentUser)?.userintroduction)
        }
    }
    val introduction : LiveData<String> = _introduction

    fun signUp(acc: String, pwd: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                Log.i(TAG, acc + "signUp")
                _updateRespond.value = Event("Success")
                signIn(acc, pwd)
            }
            override fun onFail() { _updateRespond.value = Event("Fail") }
        }).signUp(acc, pwd)
    }

    fun signIn(acc: String, pwd: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                Save.getInstance().saveUser(mapplication, acc, pwd)
                Log.i(TAG, acc + "signIn")
                _userAccount.value = acc
                _updateRespond.value = Event("Success")
                _isAvatarChanged.value = Uri.parse(Save.getInstance().getUserAvatarUri(mapplication, acc))
                GlobalScope.launch(Dispatchers.IO) {
                    syncUser(acc)
                }
            }
            override fun onFail() {
                Log.i(TAG, "signIn onFail")
                _updateRespond.value = Event("Fail")
            }
        }).signIn(acc, pwd)
    }

    suspend fun syncUser(acc: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                //save current user
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList((t as DataSnapshot).getValue(UserModel::class.java))
                _introduction.value = (t as DataSnapshot).getValue(UserModel::class.java)?.userintroduction
            }
            override fun onFail() { }
        }).syncUser(acc)
    }

    fun updateAvatar(imagePath: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            val mUserModel = LocalRepository(LocalDatabase.getInstance(mapplication)).getUser(currentUser)
            mUserModel?.let {
                mUserModel.useruri = imagePath.toString()
                FireBaseRepository(object : IFireOperationCallBack {
                    override fun <T> onSuccess(t: T) {
                        LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList(t as UserModel)
                        _isAvatarChanged.value = Uri.parse((t as UserModel).useruri)
                        Save.getInstance().saveUserAvatarUri(mapplication, currentUser, Uri.parse((t as UserModel).useruri))
                    }
                    override fun onFail() {  }
                }).updateAvatar(mUserModel, imagePath)
            }
        }
    }

    fun updateUserIntroduction(intro: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val mUserModel = LocalRepository(LocalDatabase.getInstance(mapplication)).getUser(currentUser)
            mUserModel?.let {
                mUserModel.userintroduction = intro
                FireBaseRepository(object : IFireOperationCallBack {
                    override fun <T> onSuccess(t: T) {
                        _introduction.value = (t as UserModel).userintroduction
                        LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList(t as UserModel)
                    }
                    override fun onFail() {  }
                }).updateUserIntroduction(mUserModel)
            }
        }
    }

    fun logout() {
        FireBaseRepository(null).signOut()
        GlobalScope.launch(Dispatchers.IO) {
            LocalRepository(LocalDatabase.getInstance(mapplication)).deleteFriendList()
        }
        Save.getInstance().deleteUser(mapplication)
        _userAccount.value = null
    }

    fun login() {
        FirebaseAuth.getInstance().addAuthStateListener {
            _userAccount.value = it.currentUser.toString()
        }
    }
}