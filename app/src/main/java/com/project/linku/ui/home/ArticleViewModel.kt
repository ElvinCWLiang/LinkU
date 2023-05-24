package com.project.linku.ui.home

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.remote.FireBaseRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository : FireBaseRepository,
    private val application: Application
): ViewModel() {
    private val tag = "ev_" + javaClass.simpleName
    private lateinit var articleId: String
    private lateinit var board: String
    var userReply = MutableLiveData<String>().apply { value = "" }
    private val _articleAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    val articleAdapterMaterial: LiveData<List<ArticleModel>> = _articleAdapterMaterial.distinctUntilChanged()

    fun syncArticleResponse(articleId: String, board: String) {
        repository.syncArticleResponse(articleId, board, object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(ArticleModel::class.java)
                m?.let {
                    m.id = snapshot.key.toString()
                }
                viewModelScope.launch {
                    LocalRepository(LocalDatabase.getInstance(application)).insertArticle(m)
                }
                fetchLocalArticleResponse(articleId, board)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        fetchLocalArticleResponse(articleId, board)
    }

    fun fetchLocalArticleResponse(articleId: String, board: String) {
        this.articleId = articleId
        this.board = board
        viewModelScope.launch(IO) {
            Log.i(tag, "board = $board, articleId = $articleId")
            _articleAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(application)).getLocalArticleResponse(articleId))
        }
    }

    fun onTextChange(editable: Editable?) {
        userReply.value = editable.toString()
    }

    fun send() {
        userReply.value?.let {
            repository.sendReply(it, articleId, board)
            Log.i(tag,"userReply = $it + articleId = $articleId board = $board")
            userReply.value = ""
        }
    }
}