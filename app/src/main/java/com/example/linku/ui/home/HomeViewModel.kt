package com.example.linku.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.linku.R
import com.example.linku.data.local.ArticleModel
import com.example.linku.data.local.LocalDatabase
import com.example.linku.data.local.LocalRepository
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

    fun syncBoard(pos: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val board_array = mapplication.resources.getStringArray(R.array.board_array)
            val board = board_array[pos]
            Log.i("evvv","pos = ${board}")
            if (board == "All") {
                for (sub_board in board_array) {
                    syncremote(sub_board)
                }
            } else if (board != "All") {
                syncremote(board)
            }
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
            }
            override fun onFail() {
                syncArticle.setValue(false)
                Log.i(TAG, "onfail value = ${syncArticle.value.toString()}")
            }
        }).syncBoard(board)
    }

    private fun synclocalArticle(board: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            if (board == "All") {
                homeAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getallArticle())
            } else if (board != "All")
                homeAdapterMaterial.postValue(LocalRepository(LocalDatabase.getInstance(mapplication)).getboardArticle(board))
        }
    }
}