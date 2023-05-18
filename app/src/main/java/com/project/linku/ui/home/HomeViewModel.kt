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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _isSwipeRefresh = MutableLiveData<Boolean>()
    val isSwipeRefresh: LiveData<Boolean> get() = _isSwipeRefresh

    private val _homeAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    val homeAdapterMaterial: LiveData<List<ArticleModel>> get() = _homeAdapterMaterial

//    private val _isSwipeRefresh = MutableLiveData<Boolean>()
//    val isSwipeRefresh: LiveData<Boolean> get() = _isSwipeRefresh

    private var spannableBoardPosition = 0

    fun syncBoard(pos: Int) {
        _isSwipeRefresh.value = true
        viewModelScope.launch(Dispatchers.IO) {
            spannableBoardPosition = pos
            val boardArray = application.resources.getStringArray(R.array.board_array)
            val board = boardArray[pos]
            if (board == "All") {
                for (sub_board in boardArray) {
                    syncSpecificBoard(sub_board)
                }
            } else {
                syncSpecificBoard(board)
            }
            syncLocalArticle(board)
        }
    }

    private fun syncSpecificBoard(board: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                _isSwipeRefresh.value = false
                if (t != null) {
                    val mDataSnapshot = t as DataSnapshot
                    val cacheUser = ArrayList<String>()
                    for (next in mDataSnapshot.children) {
                        val m = next.getValue(ArticleModel::class.java)
                        if (m != null) {
                            m.id = next.key.toString()
                        }
                        m?.publishAuthor?.let {
                            if (!cacheUser.contains(it)) {
                                syncUser(it)
                                cacheUser.add(it)
                            }
                        }
                        viewModelScope.launch {
                            LocalRepository(LocalDatabase.getInstance(application)).insertArticle(m)
                        }
                    }
                }
                syncLocalArticle(application.resources.getStringArray(R.array.board_array)[spannableBoardPosition])
            }
            override fun onFail() {
                _isSwipeRefresh.value = false
            }
        }).syncBoard(board)
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
}