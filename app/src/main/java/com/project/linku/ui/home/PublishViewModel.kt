package com.project.linku.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack

class PublishViewModel(application: Application) : AndroidViewModel(application) {

    val mapplication = application
    val TAG = "ev_" + javaClass.simpleName
    val syncArticle = MutableLiveData<Boolean>()
    val publishResponse = MutableLiveData<Boolean>()
    var board_num = 0
    var board = ""
    var title = ""
    var content = ""

    fun publishArticle() {
        board = mapplication.resources.getStringArray(R.array.publish_array)[board_num]
        Log.i(TAG,"board = $board, title = $title, content = $content")
        val articleModel = ArticleModel("", 0, board, "", title, content, "")
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                publishResponse.value = true
            }
            override fun onFail() {
                publishResponse.value = false
            }
        }).publishArticle(articleModel)
    }

/*
    fun syncBoard(pos: Int) {
        val board = mapplication!!.resources.getStringArray(R.array.board_array)[pos]

        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                syncArticle.setValue(true)
                if (t != null) {
                    val mDataSnapshot = t as DataSnapshot
                    for (next in mDataSnapshot.children) {
                        val m = next.getValue(ArticleModel::class.java)
                        LocalRepository(LocalDatabase.getInstance(mapplication)).insertArticle(m)
                        if (m != null) {
                            m.id = next.key.toString()
                        }
                    }
                    fetchlocalArticle(board)
                    Log.i(TAG, "onsuccess value = ${syncArticle.value.toString()}")

                }
            }

            // com.project.linku.data.remote.IFireOperationCallBack
            override fun onFail() {
                syncArticle.setValue(false)
                Log.i(TAG, "onfail value = ${syncArticle.value.toString()}")
            }
        }).syncBoard(board)
        fetchlocalArticle(board)
    }

    fun fetchlocalArticle(board: String?) {

        GlobalScope.launch {

        }
    }
*/
}