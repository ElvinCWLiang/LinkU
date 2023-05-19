package com.project.linku

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.local.UserModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.project.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _connected = MutableLiveData<Boolean>()
    val isConnected : LiveData<Boolean> = _connected
    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin : LiveData<Boolean> = _isLogin.distinctUntilChanged()

    fun isLogin(){
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                _isLogin.value = true
            }
            override fun onFail() { _isLogin.value = false }
        }).signIn(
            Save.getInstance().getUserAccount(this.application),
            Save.getInstance().getUserPassword(this.application)
        )

        FirebaseAuth.getInstance().addAuthStateListener {
            _isLogin.value = it.currentUser != null
        }
    }

    fun isConnected() {
        val mNetworkRequest =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        val mConnectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        mConnectivityManager.registerNetworkCallback(mNetworkRequest, object : NetworkCallback() {
                override fun onAvailable(network: Network) { _connected.postValue(true) }
                override fun onLost(network: Network) { _connected.postValue(false) }
                override fun onUnavailable() { _connected.postValue(false) }
            })
    }

    fun syncLocalUserData(): HashMap<String, UserModel> {
        val userWithKeySet = HashMap<String, UserModel>()
        runBlocking {
            val userModelListRou = async { LocalRepository(LocalDatabase.getInstance(application)).getAllUser() }
            val userModelList = userModelListRou.await()
            for (i in userModelList.indices) {
                userWithKeySet[userModelList[i].email] = userModelList[i]
            }
        }
        return userWithKeySet
    }
}