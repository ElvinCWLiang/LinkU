package com.project.linku.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor() : ViewModel() {

    val TAG = "ev_" + javaClass.simpleName
    var imagePath: Uri? = null

    fun publishArticle(board: String, title: String, content: String) = callbackFlow {
        if(imagePath != null) {
            Log.i(TAG,"board = $board, title = $title, content = $content")
            val articleModel = ArticleModel("", 0, board, "", title, content, this@PublishViewModel.imagePath.toString(), "")
            FireBaseRepository(object : IFireOperationCallBack {
                override fun <T> onSuccess(t: T) {
                    trySend(true)
                }
                override fun onFail() {
                    trySend(false)
                }
            }).publishArticle(articleModel)
        } else {
            Log.i(TAG,"board = $board, title = $title, content = $content")
            val articleModel = ArticleModel("", 0, board, "", title, content, "", "")
            FireBaseRepository(object : IFireOperationCallBack {
                override fun <T> onSuccess(t: T) {
                    trySend(true)
                }
                override fun onFail() {
                    trySend(false)
                }
            }).publishArticle(articleModel)
        }

        awaitClose()
    }

    fun send(imagePath: Uri) = callbackFlow {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                this@PublishViewModel.imagePath = t as Uri
                trySend(true)
            }
            override fun onFail() {
                trySend(false)
            }
        }).sendImageWithArticle(imagePath)

        awaitClose()
    }
}