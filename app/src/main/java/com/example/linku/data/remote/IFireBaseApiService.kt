package com.example.linku.data.remote

import android.net.Uri
import com.example.linku.data.local.ArticleModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot


interface IFireBaseApiService {
    fun addFriend(acc: String)

    fun getUserName(): String?

    fun publishArticle(articleModel: ArticleModel)

    fun searchAccount(str: String)

    fun send(userMessage: String, acc: String, type: Int)

    fun send(imagePath: Uri)

    fun sendReply(userReply: String, articleId: String, board: String)

    fun signIn(acc: String, pwd: String)

    fun signOut()

    fun signUp(acc: String, pwd: String)

    fun syncBoard(board: String)

    fun syncConversation(acc: String, childEventListener: ChildEventListener)

    fun syncFriendList()

    fun updateAvatar(uri: Uri)

    fun getFirebaseAuth() : FirebaseAuth?
}