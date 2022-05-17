package com.example.linku.ui.dashboard

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.MainActivity
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var mapplication = application
    private val TAG = "ev_" + javaClass.simpleName
    val _userAccount = MutableLiveData<String>().apply { value = FirebaseAuth.getInstance().currentUser?.email }
    val userAccount : LiveData<String> = _userAccount
    val _updateRespond = MutableLiveData<String>()
    val updateRespond : LiveData<String> = _updateRespond
    val _isAvatarChanged = MutableLiveData<Uri>()
    val isAvatarChanged : LiveData<Uri> = _isAvatarChanged

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
            }
            override fun onFail() {
                Log.i(TAG, "signIn onFail")
                _updateRespond.value = "Fail"
            }
        }).signIn(acc, pwd)
    }

    fun logout() {
        FireBaseRepository(null).signOut()
        Save.getInstance().deleteUser(mapplication)
        _userAccount.value = null
    }

    fun login() {
        _userAccount.value = FirebaseAuth.getInstance().currentUser?.email
    }

    fun updateAvatar(imagePath: Uri) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                _isAvatarChanged.value = t as Uri
                Save.getInstance().saveUserAvatarUri(mapplication, FirebaseAuth.getInstance().currentUser?.email.toString(), t as Uri)
            }
            override fun onFail() {  }
        }).updateAvatar(imagePath)
    }
}