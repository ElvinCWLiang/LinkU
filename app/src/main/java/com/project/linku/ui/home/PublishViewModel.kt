package com.project.linku.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.FirebaseResult
import com.project.linku.data.remote.IFireOperationCallBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor(
    private val repository : FireBaseRepository
) : ViewModel() {

    val TAG = "ev_" + javaClass.simpleName
    var imagePath: Uri? = null

    fun publishArticle(board: String, title: String, content: String) = callbackFlow {
        if(imagePath != null) {
            Log.i(TAG,"board = $board, title = $title, content = $content")
            val articleModel = ArticleModel("", 0, board, "", title, content, this@PublishViewModel.imagePath.toString(), "")
            repository.publishArticle(articleModel).collectLatest {
                trySend(it)
            }
        } else {
            Log.i(TAG,"board = $board, title = $title, content = $content")
            val articleModel = ArticleModel("", 0, board, "", title, content, "", "")
            repository.publishArticle(articleModel).collectLatest {
                trySend(it)
            }
        }
        awaitClose()
    }

    fun send(imagePath: Uri) = callbackFlow {
        repository.sendImageWithArticle(imagePath).collectLatest {
            when(it) {
                is FirebaseResult.Success -> {
                    trySend(true)
                }
                is FirebaseResult.Failure -> {
                    trySend(false)
                }
            }
        }
        awaitClose()
    }
}