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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlin.jvm.internal.Intrinsics


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val mapplication: Application? = application
    val TAG = "ev_" + javaClass.simpleName
    val syncArticle = MutableLiveData<Boolean>()
    val homeAdapterMaterial = MutableLiveData<List<ArticleModel>>()

    fun syncBoard(pos: Int) {
        val board = mapplication!!.resources.getStringArray(R.array.board_array)[pos]
        Intrinsics.checkNotNullExpressionValue(
            board,
            "mapplication.resources.g…R.array.board_array)[pos]"
        )
        FireBaseRepository(object : IFireOperationCallBack {
            // from class: com.example.linku.ui.home.HomeViewModel$syncBoard$1
            // com.example.linku.data.remote.IFireOperationCallBack
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
                        var str: String? = null
                        val append = StringBuilder().append("onsuccess value Time: ").append(
                            if (m != null) java.lang.Long.valueOf(
                                m.publishTime!!
                            ) else null
                        ).append(" author: ").append(m?.publishAuthor).append("content: ").append(
                            m?.publishContent
                        ).append("title: ").append(m?.publishTitle).append("id: ").append(m?.id)
                            .append("reply: ")
                        if (m != null) {
                            str = m.reply
                        }
                        Log.i(tag, append.append(str).toString())
                    }
                    fetchlocalArticle(board)
                    Log.i(TAG, "onsuccess value = ${syncArticle.value.toString()}")

                }
            }

            // com.example.linku.data.remote.IFireOperationCallBack
            override fun onFail() {
                syncArticle.setValue(false)
                Log.i(TAG, "onfail value = ${syncArticle.value.toString()}")
            }
        }).syncBoard(board)
        fetchlocalArticle(board)
    }

    fun fetchlocalArticle(board: String?) {
        Intrinsics.checkNotNullParameter(board, "board")
        CoroutineScope()
        BuildersKt__Builders_commonKt.`launch$default`(
            GlobalScope,
            Main,
            null,
            `HomeViewModel$fetchlocalArticle$1`(board, this, null),
            2,
            null
        )
    }

}