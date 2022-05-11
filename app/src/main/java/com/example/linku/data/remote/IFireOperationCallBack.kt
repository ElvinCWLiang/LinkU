package com.example.linku.data.remote

interface IFireOperationCallBack{
    fun onFail()
    fun <T> onSuccess(t: T)
}