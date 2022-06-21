package com.project.linku.ui.home

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.project.linku.ui.utils.Parsefun

class PublishViewModel(application: Application) : AndroidViewModel(application) {

    val mapplication = application
    val TAG = "ev_" + javaClass.simpleName
    val publishResponse = MutableLiveData<Boolean>()
    val updateResponse = MutableLiveData<Boolean>()
    var board_num = 0
    var board = ""
    var title = ""
    var content = ""
    var imagePath: Uri? = null

    fun publishArticle() {
        if(imagePath != null) {
            board = mapplication.resources.getStringArray(R.array.publish_array)[board_num]
            Log.i(TAG,"board = $board, title = $title, content = $content")
            val articleModel = ArticleModel("", 0, board, "", title, content, this@PublishViewModel.imagePath.toString(), "")
            FireBaseRepository(object : IFireOperationCallBack {
                override fun <T> onSuccess(t: T) {
                    publishResponse.value = true
                }
                override fun onFail() {
                    publishResponse.value = false
                }
            }).publishArticle(articleModel)
        } else {
            board = mapplication.resources.getStringArray(R.array.publish_array)[board_num]
            Log.i(TAG,"board = $board, title = $title, content = $content")
            val articleModel = ArticleModel("", 0, board, "", title, content, "", "")
            FireBaseRepository(object : IFireOperationCallBack {
                override fun <T> onSuccess(t: T) {
                    publishResponse.value = true
                }
                override fun onFail() {
                    publishResponse.value = false
                }
            }).publishArticle(articleModel)
        }
    }

    fun send(imagePath: Uri) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                this@PublishViewModel.imagePath = t as Uri
                updateResponse.value = true
            }
            override fun onFail() {}
        }).sendImageWithArticle(imagePath)
    }
}