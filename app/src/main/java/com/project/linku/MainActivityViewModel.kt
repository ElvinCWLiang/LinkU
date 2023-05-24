package com.project.linku

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.local.UserModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.project.linku.data.remote.FirebaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository : FireBaseRepository,
    private val application: Application
) : ViewModel() {

    fun isLogin() = callbackFlow {
        repository.signIn(
            Save.getUserAccount(application),
            Save.getUserPassword(application)
        ).collectLatest {
            when (it) {
                is FirebaseResult.Success -> {
                    trySend(true)
                }
                else -> {
                    trySend(false)
                }
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener {
            trySend(it.currentUser != null)
        }

        awaitClose{}
    }

    fun isConnected() = callbackFlow {
        val mNetworkRequest =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        val mConnectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        mConnectivityManager.registerNetworkCallback(mNetworkRequest, object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
            override fun onUnavailable() {
                trySend(false)
            }
        })

        awaitClose{}
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