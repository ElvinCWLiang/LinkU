package com.example.linku.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.linku.R
import com.example.linku.data.local.ArticleModel
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
import com.example.linku.data.local.UserModel
import com.example.linku.data.remote.FireBaseRepository
import com.example.linku.data.remote.IFireOperationCallBack
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val mapplication: Application = application
    val TAG = "ev_" + javaClass.simpleName
    val syncArticle = MutableLiveData<Boolean>()
    val homeAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    var spnboardpos = 0

    fun syncBoard(pos: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            spnboardpos = pos
            val board_array = mapplication.resources.getStringArray(R.array.board_array)
            val board = board_array[pos]
            if (board == "All") {
                for (sub_board in board_array) {
                    syncremote(sub_board)
                }
            } else if (board != "All") {
                syncremote(board)
            }
            Log.i(TAG,"syncBoard")
            synclocalArticle(board)
        }
    }

    private fun syncremote(board: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                syncArticle.value = true
                if (t != null) {
                    val mDataSnapshot = t as DataSnapshot
                    for (next in mDataSnapshot.children) {
                        val m = next.getValue(ArticleModel::class.java)
                        if (m != null) {
                            m.id = next.key.toString()
                        }
                        m?.publishAuthor?.let { syncUser(it) }
                        LocalRepository(LocalDatabase.getInstance(mapplication)).insertArticle(m)
                        Log.i(TAG, "id = ${m?.id} " +
                                "title = ${m?.publishTitle} " +
                                "board = ${m?.publishBoard} " +
                                "author = ${m?.publishAuthor} " +
                                "time = ${m?.publishTime} " +
                                "content = ${m?.publishContent} " +
                                "reply = ${m?.reply}")
                    }
                }
                synclocalArticle(mapplication.resources.getStringArray(R.array.board_array)[spnboardpos])
                Log.i(TAG,"onSuccess $board")
            }
            override fun onFail() {
                syncArticle.setValue(false)
                Log.i(TAG, "onfail value = ${syncArticle.value.toString()}")
            }
        }).syncBoard(board)
    }

    fun syncUser(acc: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                //save current user
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList((t as DataSnapshot).getValue(
                    UserModel::class.java))
            }
            override fun onFail() { }
        }).syncUser(acc)
    }

    private fun synclocalArticle(board: String?) {
        Log.i(TAG,"synclocalArticle")
        GlobalScope.launch(Dispatchers.IO) {
            if (board == "All") {
                homeAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getallArticle())
            } else if (board != "All")
                homeAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getboardArticle(board))
        }
    }
}