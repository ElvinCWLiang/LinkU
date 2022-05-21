package com.project.linku.ui.utils

import android.content.Context
import android.net.Uri
import com.project.linku.R

class Save {
    private val Account = "ACCOUNT"
    private val ConnectionStatus = "CONNECTIONSTATUS"
    private val Password = "PASSWORD"
    private val UsersURI = "USERSURI"

    companion object {
        private var instance: Save? = null

        fun getInstance(): Save {
            return instance ?: Save()
        }
    }

    fun saveConnectionStatus(mContext: Context, isConnected: Boolean) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putBoolean(ConnectionStatus, isConnected).apply()
    }

    fun saveUser(mContext: Context, acc: String?, pwd: String?) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putString(Account, acc).apply()
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putString(Password, pwd).apply()
    }

    /* when user click signout in dashboardFragment */
    fun deleteUser(mContext: Context) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .remove(Account).apply()
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .remove(Password).apply()
    }

    /* return userAccount in MainActivityViewModel when the application is initiated */
    fun getUserAccount(mContext: Context): String {
        return mContext.getSharedPreferences(mContext.resources.getString(R.string.data), 0)
            .getString(Account, "zz")!!
    }

    /* return UserPassword in MainActivityViewModel when the application is initiated */
    fun getUserPassword(mContext: Context): String {
        return mContext.getSharedPreferences(mContext.resources.getString(R.string.data), 0)
            .getString(Password, "zz")!!
    }

    fun getConnectionStatus(mContext: Context): Boolean {
        return mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0)
            .getBoolean(ConnectionStatus, false)
    }

    fun saveUserAvatarUri(mContext: Context, acc: String, imagePath: Uri) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putString(acc, imagePath.toString()).apply()
    }

    fun getUserAvatarUri(mContext: Context, acc: String): String? {
        return mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0)
            .getString(acc, "")
    }

}