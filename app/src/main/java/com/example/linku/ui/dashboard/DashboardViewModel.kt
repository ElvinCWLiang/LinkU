package com.example.linku.ui.dashboard

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var mContext: Context? = application

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val TAG = "ev_" + javaClass.simpleName

    private val loginstatus: MutableLiveData<Boolean>? = null
    private val mApplication: Application? = null
    private val shouldshowLoginDialog: MutableLiveData<Boolean>? = null

    private val userAccount: MutableLiveData<String>? = null

    fun signUp(acc: String?, pwd: String?) {
        FireBaseRepository(null).signUp(acc, pwd)
    }


    fun signIn(acc: String?, pwd: String?) {
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.dashboard.DashboardViewModel$signIn$1
            // com.example.linku.data.remote.IFireOperationCallBack
            override fun <T> onSuccess(t: T) {
                save.saveUser(application, acc, pwd)
                save2.saveLoginStatus(application2, true)
                Log.i(TAG, acc)
                userAccount.setValue(acc)
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {}
        }).signIn(acc, pwd)
    }

    fun logout() {
        FireBaseRepository(null).signOut()
        Save.saveLoginStatus(this.mApplication, false)
        Save.deleteUser(this.mApplication)
        userAccount!!.value = null
    }

    fun showDialog(): MutableLiveData<Boolean>? {
        Log.i(TAG, "loginstatus = " + Save.getLoginStatus(this.mApplication))
        shouldshowLoginDialog!!.setValue(java.lang.Boolean.valueOf(Save.getLoginStatus(this.mApplication)))
        return shouldshowLoginDialog
    }


}