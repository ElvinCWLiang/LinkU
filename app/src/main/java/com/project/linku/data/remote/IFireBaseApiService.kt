package com.project.linku.data.remote

import android.net.Uri
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import kotlinx.coroutines.flow.Flow

/* The operations which provide by Firebase */
interface IFireBaseApiService {
    fun addFriend(acc: String)
    fun getUserName(): String?
    fun publishArticle(articleModel: ArticleModel): Flow<Boolean>
    fun searchAccount(str: String)
    fun send(userMessage: String, account: String, type: Int)
    fun send(imagePath: Uri)
    fun sendImageWithArticle(imagePath: Uri)
    fun sendReply(userReply: String, articleId: String, board: String)
    fun signIn(acc: String, pwd: String): Flow<Boolean>
    fun signOut()
    fun signUp(acc: String, pwd: String)
    fun syncBoard(board: String)
    fun syncConversation(acc: String, childEventListener: ChildEventListener)
    fun syncFriendList()
    fun updateAvatar(userModel: UserModel, imagePath: Uri)
    fun getFirebaseAuth() : FirebaseAuth?
    fun updateUserIntroduction(userModel: UserModel)
    suspend fun syncUser(acc: String)
    fun syncArticleResponse(articleId: String, board: String, childEventListener: ChildEventListener)
    fun notifyMessage(param: ChildEventListener)
}