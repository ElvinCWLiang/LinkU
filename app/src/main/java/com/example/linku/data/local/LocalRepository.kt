package com.example.linku.data.local

import androidx.room.Database
import kotlinx.coroutines.*

import kotlin.jvm.internal.Intrinsics


class LocalRepository(_db: LocalDatabase) {
    private val db = _db

    fun insertArticle(articleModel: ArticleModel?) {
        GlobalScope.launch(Dispatchers.IO) {
            db.dataDao().insertArticle(articleModel)
        }
    }

    fun getallArticle(): List<ArticleModel?> {
        return db.dataDao().getallArticle()
    }

    fun getboardArticle(board: String?): List<ArticleModel?> {
        return db.dataDao().getBoardArticle(board)
    }

    fun insertFriendList(friendModel: FriendModel?) {
        GlobalScope.launch(Dispatchers.IO) {
            db.dataDao().insertFriendList(friendModel)
        }
    }

    fun getLocalArticleResponse(articleId: String?): List<ArticleModel?> {
        return db.dataDao().getArticleResponse(articleId)
    }

    fun getFreindList(): List<FriendModel?> {
        return db.dataDao().getFreindList()
    }

    fun getConversaion(acc: String?): List<FriendModel?> {
        return db.dataDao().getConversation(acc)
    }

}