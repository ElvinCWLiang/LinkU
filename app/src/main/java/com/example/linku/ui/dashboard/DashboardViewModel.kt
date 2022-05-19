package com.example.linku.ui.dashboard

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.MainActivity
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
import com.example.linku.data.local.UserModel
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var mapplication = application
    private val TAG = "ev_" + javaClass.simpleName
    private val _userAccount = MutableLiveData<String>().apply { value = FirebaseAuth.getInstance().currentUser?.email }
    val userAccount : LiveData<String> = _userAccount
    private val _updateRespond = MutableLiveData<String>()
    val updateRespond : LiveData<String> = _updateRespond
    private val _isAvatarChanged = MutableLiveData<Uri>()
    val isAvatarChanged : LiveData<Uri> = _isAvatarChanged
    val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val _introduction = MutableLiveData<String>().apply {
        GlobalScope.launch(Dispatchers.IO) {
            postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getUser(currentUser).userintroduction)
        }
    }
    val introduction : LiveData<String> = _introduction

    fun signUp(acc: String, pwd: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                _updateRespond.value = "Success"
                signIn(acc, pwd)
            }
            override fun onFail() { _updateRespond.value = "Fail" }
        }).signUp(acc, pwd)
    }

    fun signIn(acc: String, pwd: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                Save.getInstance().saveUser(mapplication, acc, pwd)
                Log.i(TAG, acc + "")
                _userAccount.value = acc
                _updateRespond.value = "Success"
                _isAvatarChanged.value = Uri.parse(Save.getInstance().getUserAvatarUri(mapplication, acc))
                GlobalScope.launch(Dispatchers.IO) {
                    syncUser(acc)
                }
            }
            override fun onFail() {
                Log.i(TAG, "signIn onFail")
                _updateRespond.value = "Fail"
            }
        }).signIn(acc, pwd)
    }

    suspend fun syncUser(acc: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                //save current user
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList((t as DataSnapshot).getValue(UserModel::class.java))
            }
            override fun onFail() { }
        }).syncUser(acc)
    }

    fun updateAvatar(imagePath: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            val mUserModel = LocalRepository(LocalDatabase.getInstance(mapplication)).getUser(currentUser)
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

    fun updateUserIntroduction(intro: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val mUserModel = LocalRepository(LocalDatabase.getInstance(mapplication)).getUser(currentUser)
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

    fun logout() {
        FireBaseRepository(null).signOut()
        Save.getInstance().deleteUser(mapplication)
        _userAccount.value = null
    }

    fun login() {
        _userAccount.value = FirebaseAuth.getInstance().currentUser?.email
    }
}