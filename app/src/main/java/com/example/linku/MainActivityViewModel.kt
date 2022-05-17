package com.example.linku

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ev_" + javaClass.simpleName
    private val _connected = MutableLiveData<Boolean>()
    val isConnected : LiveData<Boolean> = _connected
    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin : LiveData<Boolean> = _isLogin
    private val mApplication = application

    fun isLogin(){
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) { _isLogin.value = true }
            override fun onFail() { _isLogin.value = false }
        }).signIn(
            Save.getInstance().getUserAccount(this.mApplication),
            Save.getInstance().getUserPassword(this.mApplication)
        )
    }

    fun isConnected() {
        if (this.mApplication != null && Build.VERSION.SDK_INT >= 21) {
            val mNetworkRequest =
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
            val mConnectivityManager = mApplication.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            mConnectivityManager.registerNetworkCallback(mNetworkRequest, object : NetworkCallback() {
                    override fun onAvailable(network: Network) { _connected.postValue(true) }
                    override fun onLost(network: Network) { _connected.postValue(false) }
                    override fun onUnavailable() { _connected.postValue(false) }
                })
        }
    }
}