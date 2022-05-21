package com.project.linku.data.remote

interface IFireOperationCallBack{
    fun onFail()
    fun <T> onSuccess(t: T)
}