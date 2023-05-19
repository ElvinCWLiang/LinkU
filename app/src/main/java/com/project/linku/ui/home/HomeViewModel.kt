package com.project.linku.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.linku.MainActivity.Companion.userkeySet
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.local.UserModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _homeAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    val homeAdapterMaterial: LiveData<List<ArticleModel>> get() = _homeAdapterMaterial

    private val _isSwipeRefresh = MutableSharedFlow<Boolean>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val isRefresh = _isSwipeRefresh.asSharedFlow()

    private var spannableBoardPosition = 0

    val accept: (UiAction) -> Unit

    val state: StateFlow<List<ArticleModel>>

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>(
            replay = 1,
            extraBufferCapacity = 0,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        accept = { action ->
            viewModelScope.launch {
                actionStateFlow.emit(action)
            }
        }

        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .flatMapLatest {
                syncBoard(it.position)
            }

        state = searches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
    }

    private fun syncBoard(pos: Int): Flow<List<ArticleModel>> = callbackFlow {
        _isSwipeRefresh.tryEmit(true)
        viewModelScope.launch(Dispatchers.IO) {
            spannableBoardPosition = pos
            val boardArray = application.resources.getStringArray(R.array.board_array)
            val board = boardArray[pos]
            if (board == "All") {
                val articleList = mutableListOf<ArticleModel>()
                for (i in boardArray.indices) {
                    launch {
                        syncSpecificBoard(boardArray[i]).collect { list ->
                            articleList.addAll(list)
                            if (i == boardArray.size - 1) {
                                trySend(articleList)
                            }
                        }
                    }
                }
            } else {
                syncSpecificBoard(board).collectLatest {
                    trySend(it)
                }
            }
            syncLocalArticle(board)
        }

        awaitClose{}
    }

    private fun syncSpecificBoard(board: String): Flow<List<ArticleModel>> = callbackFlow {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                _isSwipeRefresh.tryEmit(false)
                if (t != null) {
                    val mDataSnapshot = t as DataSnapshot
                    val cacheUser = ArrayList<String>()
                    val articleModelList = mutableListOf<ArticleModel>()
                    for (next in mDataSnapshot.children) {
                        val articleModel = next.getValue(ArticleModel::class.java)
                        if (articleModel != null) {
                            articleModel.id = next.key.toString()
                        }
                        articleModel?.let {
                            it.publishAuthor?.let { author ->
                                if (!cacheUser.contains(author)) {
                                    syncUser(author)
                                    cacheUser.add(author)
                                }
                            }
                            articleModelList.add(it)
                        }

                        viewModelScope.launch {
                            LocalRepository(LocalDatabase.getInstance(application)).insertArticle(articleModel)
                        }
                    }
                    trySend(articleModelList)
                }
                syncLocalArticle(application.resources.getStringArray(R.array.board_array)[spannableBoardPosition])
            }
            override fun onFail() {
                _isSwipeRefresh.tryEmit(false)
                close()
            }
        }).syncBoard(board)

        awaitClose{}
    }

    fun syncUser(acc: String) {
        viewModelScope.launch {
            FireBaseRepository(object : IFireOperationCallBack {
                override fun <T> onSuccess(t: T) {
                    val userModel = (t as DataSnapshot).getValue(UserModel::class.java)
                    viewModelScope.launch {
                        LocalRepository(LocalDatabase.getInstance(application)).insertUserList(userModel)
                    }
                    userModel?.let {
                        userkeySet.put(userModel.email, userModel)
                    }
                }
                override fun onFail() { }
            }).syncUser(acc)
        }
    }

    private fun syncLocalArticle(board: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (board == "All") {
                _homeAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(application)).getallArticle())
            } else {
                _homeAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(application)).getboardArticle(board))
            }
        }
    }

    sealed class UiAction {
        data class Search(val position: Int) : UiAction()
    }

    data class UiState(
        val query: String = "",
        val lastQueryScrolled: String = "",
        val hasNotScrolledForCurrentSearch: Boolean = false
    )
}