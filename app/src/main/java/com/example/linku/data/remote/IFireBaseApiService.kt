package com.example.linku.data.remote

import com.example.linku.data.local.ArticleModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot


interface IFireBaseApiService {
    fun addFriend(str: String?)

    fun getUserName(): String?

    fun publishArticle(articleModel: ArticleModel?): ArticleModel?

    fun searchAccount(str: String?)

    fun send(str: String?, str2: String?)

    fun sendReply(str: String?, str2: String?, str3: String?)

    fun signIn(str: String?, str2: String?)

    fun signOut()

    fun signUp(str: String?, str2: String?)

    fun syncBoard(str: String?): DataSnapshot?

    fun syncConversation(str: String?, childEventListener: ChildEventListener?)

    fun syncFriendList(): DataSnapshot?
}