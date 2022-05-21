package com.project.linku.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.project.linku.MainActivity.Companion.userkeySet
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.local.UserModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.data.remote.IFireOperationCallBack
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
                    syncspecificboard(sub_board)
                }
            } else if (board != "All") {
                syncspecificboard(board)
            }
            //Log.i(TAG,"syncBoard")
            synclocalArticle(board)
        }
    }

    private fun syncspecificboard(board: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                syncArticle.value = true
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
                        LocalRepository(LocalDatabase.getInstance(mapplication)).insertArticle(m)
                        /*Log.i(TAG, "id = ${m?.id} " +
                                "title = ${m?.publishTitle} " +
                                "board = ${m?.publishBoard} " +
                                "author = ${m?.publishAuthor} " +
                                "time = ${m?.publishTime} " +
                                "content = ${m?.publishContent} " +
                                "reply = ${m?.reply}")*/
                    }
                }
                synclocalArticle(mapplication.resources.getStringArray(R.array.board_array)[spnboardpos])
            }
            override fun onFail() {
                syncArticle.setValue(false)
            }
        }).syncBoard(board)
    }

    fun syncUser(acc: String) {
        FireBaseRepository(object : IFireOperationCallBack {
            override fun <T> onSuccess(t: T) {
                val userModel = (t as DataSnapshot).getValue(UserModel::class.java)
                LocalRepository(LocalDatabase.getInstance(mapplication)).insertUserList(userModel)
                userModel?.let {
                    userkeySet.put(userModel.email, userModel)
                }
            }
            override fun onFail() { }
        }).syncUser(acc)
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