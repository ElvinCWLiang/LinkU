package com.example.linku.ui.dashboard

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.example.linku.ui.utils.Save


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var mapplication = application

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val TAG = "ev_" + javaClass.simpleName

    private val loginstatus: MutableLiveData<Boolean>? = null
    private val mApplication: Application? = null
    private val shouldshowLoginDialog = MutableLiveData<Boolean>().apply {
        value = false
    }

    private var userAccount: MutableLiveData<String?>? = null

    fun signUp(acc: String, pwd: String) {
        FireBaseRepository(null).signUp(acc, pwd)
    }


    fun signIn(acc: String, pwd: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.dashboard.DashboardViewModel$signIn$1
            // com.example.linku.data.remote.IFireOperationCallBack
            override fun <T> onSuccess(t: T) {
                Save.getInstance().saveUser(mapplication, acc, pwd)
                Save.getInstance().saveLoginStatus(mapplication, true)
                Log.i(TAG, acc + "")
                userAccount?.setValue(acc)
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {}
        }).signIn(acc, pwd)
    }

    fun logout() {
        FireBaseRepository(null).signOut()
        Save.getInstance().saveLoginStatus(mapplication, false)
        Save.getInstance().deleteUser(mapplication)
        userAccount?.value = null
    }

    fun showDialog(): MutableLiveData<Boolean> {
        Log.i(TAG, "loginstatus = " + Save.getInstance().getLoginStatus(mapplication))
        shouldshowLoginDialog!!.value = (Save.getInstance().getLoginStatus(mapplication))
        return shouldshowLoginDialog
    }


}