package com.project.linku.ui.home

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.remote.FireBaseRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = "ev_" + javaClass.simpleName
    private lateinit var articleId: String
    private var articleModel: ArticleModel? = null
    private lateinit var board: String
    private var mapplication: Application = application
    var userReply = MutableLiveData<String>().apply { value = "" }
    private val _articleAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    val articleAdapterMaterial: LiveData<List<ArticleModel>> = _articleAdapterMaterial.distinctUntilChanged()

    fun syncArticleResponse(articleId: String, board: String) {
        FireBaseRepository(null).syncArticleResponse(articleId, board, object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(ArticleModel::class.java)
                m?.let {
                    m.id = snapshot.key.toString()
                }
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertArticle(m)
                fetchlocalArticleResponse(articleId, board)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        fetchlocalArticleResponse(articleId, board)
    }

    fun fetchlocalArticleResponse(articleId: String, board: String) {
        this.articleId = articleId
        this.board = board
        GlobalScope.launch(IO) {
            Log.i(TAG, "board = $board, articleId = $articleId")
            _articleAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getLocalArticleResponse(articleId))
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