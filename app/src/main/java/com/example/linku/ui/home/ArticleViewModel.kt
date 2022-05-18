package com.example.linku.ui.home

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.example.linku.data.local.ArticleModel
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
import com.example.linku.data.remote.FireBaseRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = "ev_" + javaClass.simpleName

    lateinit var articleId: String
    var articleModel: ArticleModel? = null
    lateinit var board: String
    private var mapplication: Application = application
    var userReply = MutableLiveData<String>().apply { value = "" }
    private val articleAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    val _articleAdapterMaterial: LiveData<List<ArticleModel>> = articleAdapterMaterial.distinctUntilChanged()

    fun synclocalArticleResponse(articleId: String, board: String) {
        this.articleId = articleId
        this.board = board
        GlobalScope.launch(IO) {
            Log.i(TAG, "board = $board, articleId = $articleId")
            articleAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getLocalArticleResponse(articleId))
            Log.i(TAG, "fetchlocalArticle_end")
        }
    }

    fun onTextChange(editable: Editable?) {
        userReply.value = editable.toString()
    }

    fun send() {
        FireBaseRepository(null).sendReply(userReply.value!!, articleId, board)
        Log.i(TAG,"userReply = ${userReply!!.value} + articleId = ${articleId} board = $board")
        userReply.value = ""
    }


}