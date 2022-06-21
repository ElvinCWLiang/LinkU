package com.project.linku.data.local

import kotlinx.coroutines.*

class LocalRepository(_db: LocalDatabase) {
    private val db = _db

    /* insert article to Room db */
    fun insertArticle(articleModel: ArticleModel?) {
        GlobalScope.launch(Dispatchers.IO) {
            db.dataDao().insertArticle(articleModel)
        }
    }

    /* return all article to HomeFragment(HomeAdpater) */
    suspend fun getallArticle(): List<ArticleModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getallArticle()
    }

    /* return specific article response to ArticleFragment */
    suspend fun getLocalArticleResponse(articleId: String): List<ArticleModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getArticleResponse(articleId)
    }

    /* return specific board article to HomeFragment(HomeAdpater) */
    suspend fun getboardArticle(board: String?): List<ArticleModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getBoardArticle(board)
    }

    /* insert friend list to ChatFragment(ChatAdapter) */
    fun insertFriendList(friendModel: FriendModel?){
        GlobalScope.launch(Dispatchers.IO) {
            db.dataDao().insertFriendList(friendModel)
        }
    }

    /* return friend list according from this account */
    suspend fun getFriendList(localAccount: String): List<FriendModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getFreindList(localAccount)
    }

    suspend fun deleteFriendList() = withContext(Dispatchers.IO){
        db.dataDao().deleteFriendList()
    }

    /* return the specific conversation from the acc*/
    suspend fun getConversaion(remoteAccount: String, localAccount: String): List<FriendModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getConversation(remoteAccount, localAccount)
    }
/*
    /* return the specific conversation range from the acc*/
    suspend fun getConversaionRange(remoteAccount: String, localAccount: String, start: Int, end: Int): List<FriendModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getConversationRange(remoteAccount, localAccount, start, end)
    }
*/
    /* insert UserModel as entiry into db */
    fun insertUserList(userModel: UserModel?){
        GlobalScope.launch(Dispatchers.IO) {
            db.dataDao().insertUserList(userModel)
        }
    }

    /* return the UserModel by account */
    suspend fun getUser(acc: String) : UserModel? = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getUser(acc)
    }

    /* return the UserModel by account */
    suspend fun getAllUser() : List<UserModel> = withContext(Dispatchers.IO){
        return@withContext db.dataDao().getAllUser()
    }
}