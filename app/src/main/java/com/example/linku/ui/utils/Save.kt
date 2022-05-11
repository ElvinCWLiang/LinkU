package com.example.linku.ui.utils

import android.R
import android.content.Context
import kotlin.jvm.internal.Intrinsics


class Save {
    private val Account = "ACCOUNT"
    private val ConnectionStatus = "CONNECTIONSTATUS"
    val INSTANCE: Save = Save()
    private val LoginStatus = "LOGINSTATUS"
    private val Password = "PASSWORD"

    fun saveConnectionStatus(mContext: Context, isConnected: Boolean) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putBoolean(ConnectionStatus, isConnected).apply()
    }

    fun saveLoginStatus(mContext: Context, isLogin: Boolean) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putBoolean(LoginStatus, isLogin).apply()
    }

    fun saveUser(mContext: Context, acc: String?, pwd: String?) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putString(Account, acc).apply()
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .putString(Password, pwd).apply()
    }

    fun deleteUser(mContext: Context) {
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .remove(Account).apply()
        mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0).edit()
            .remove(Password).apply()
    }

    fun getUserAccount(mContext: Context): String {
        val string: String =
            mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0)
                .getString(Account, "zz")
        Intrinsics.checkNotNull(string)
        return string
    }

    fun getUserPassword(mContext: Context): String {
        val string: String =
            mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0)
                .getString(Password, "zz")
        Intrinsics.checkNotNull(string)
        return string
    }

    fun getConnectionStatus(mContext: Context): Boolean {
        return mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0)
            .getBoolean(ConnectionStatus, false)
    }

    fun getLoginStatus(mContext: Context): Boolean {
        return mContext.getSharedPreferences(mContext.getResources().getString(R.string.data), 0)
            .getBoolean(LoginStatus, false)
    }

    fun canBeUpload(mContext: Context): Boolean {
        return getLoginStatus(mContext) && getConnectionStatus(mContext)
    }

}