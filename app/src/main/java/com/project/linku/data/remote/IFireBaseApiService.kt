package com.project.linku.data.remote

import android.net.Uri
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow

/* The operations which provide by Firebase */
interface IFireBaseApiService {
    fun addFriend(account: String): Flow<FirebaseResult<Boolean>>
    fun getUserName(): String?
    fun publishArticle(articleModel: ArticleModel): Flow<Boolean>
    fun searchAccount(str: String): Flow<FirebaseResult<DataSnapshot>>
    fun send(userMessage: String, account: String, type: Int): Flow<FirebaseResult<Boolean>>
    fun send(imagePath: Uri): Flow<FirebaseResult<Uri>>
    fun sendImageWithArticle(imagePath: Uri): Flow<FirebaseResult<Uri>>
    fun sendReply(userReply: String, articleId: String, board: String): Flow<FirebaseResult<Boolean>>
    fun signIn(acc: String, pwd: String): Flow<FirebaseResult<Boolean>>
    fun signOut()
    fun signUp(acc: String, pwd: String): Flow<FirebaseResult<Boolean>>
    fun syncBoard(board: String): Flow<FirebaseResult<DataSnapshot>>
    fun syncConversation(acc: String, childEventListener: ChildEventListener)
    fun syncFriendList(): Flow<FirebaseResult<DataSnapshot>>
    fun updateAvatar(userModel: UserModel, imagePath: Uri): Flow<FirebaseResult<UserModel>>
    fun getFirebaseAuth() : FirebaseAuth?
    fun updateUserIntroduction(userModel: UserModel): Flow<FirebaseResult<UserModel>>
    fun syncUser(acc: String): Flow<FirebaseResult<DataSnapshot>>
    fun syncArticleResponse(articleId: String, board: String, childEventListener: ChildEventListener)
    fun notifyMessage(param: ChildEventListener)
}