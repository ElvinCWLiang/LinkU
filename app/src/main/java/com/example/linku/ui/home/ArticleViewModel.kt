package com.example.linku.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.linku.data.local.ArticleModel
import com.example.linku.data.remote.FireBaseRepository
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlin.jvm.internal.Intrinsics


class ArticleViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = "ev_" + javaClass.simpleName
    private val articleAdapterMaterial = MutableLiveData<List<ArticleModel>>()
    var articleId: String? = null
    var articleModel: ArticleModel? = null
    var board: String? = null
    private val mapplication: Application? = null
    private val userReply: MutableLiveData<String>? = null

    fun fetchlocalArticleResponse(articleId: String?, board: String?) {
        this.articleId = articleId
        this.board = board

        BuildersKt__Builders_commonKt.`launch$default`(
            GlobalScope,
            Main,
            null,
            `ArticleViewModel$fetchlocalArticleResponse$1`(this, articleId, null),
            2,
            null
        )
    }

    fun send() {
        val value = userReply!!.value
        Intrinsics.checkNotNull(value)
        FireBaseRepository(null).sendReply(value, getArticleId(), getBoard())
        Log.i(TAG,"userReply = " + userReply!!.value + "  articleId = " + getArticleId() + "  board = " + getBoard()
        )
        userReply!!.setValue("")
    }


}