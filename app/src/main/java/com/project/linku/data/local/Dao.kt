package com.project.linku.data.local

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Delete
    fun deleteArticle(vararg articleModelArr: ArticleModel?)

    @Query("SELECT * FROM ARTICLEMODEL WHERE reply = :articleId")
    fun getArticleResponse(articleId: String): List<ArticleModel>

    @Query("SELECT * FROM ARTICLEMODEL WHERE publishTitle != '' order by publishTime DESC")
    fun getallArticle(): List<ArticleModel>

    @Query("SELECT * FROM ARTICLEMODEL WHERE publishBoard LIKE :str AND publishTitle != '' order by publishTime DESC")
    fun getBoardArticle(str: String?): List<ArticleModel>

    @Query("SELECT * FROM FRIENDMODEL WHERE (EMAIL = :remoteAccount AND EMAILFROM = :localAccount) OR (EMAIL = :localAccount AND EMAILFROM = :remoteAccount) order by TIME ASC")
    fun getConversation(remoteAccount: String, localAccount: String): List<FriendModel>
/*
    @Query("SELECT * FROM FRIENDMODEL WHERE (EMAIL = :remoteAccount AND EMAILFROM = :localAccount) OR (EMAIL = :localAccount AND EMAILFROM = :remoteAccount) order by TIME ASC")
    fun getConversationRange(remoteAccount: String, localAccount: String, start: Int, end: Int): List<FriendModel>
*/
    @Query("SELECT * FROM FRIENDMODEL WHERE ID IN (SELECT MAX(ID) AS ID FROM FRIENDMODEL WHERE EMAIL != :localAccount GROUP BY EMAIL)")
    fun getFreindList(localAccount: String): List<FriendModel>

    @Query("DELETE FROM FRIENDMODEL")
    fun deleteFriendList()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(vararg articleModel: ArticleModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriendList(vararg friendModel: FriendModel?)

    @Update
    fun updateArticle(vararg articleModel: ArticleModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserList(vararg userModel: UserModel?)

    @Query("SELECT * FROM UserModel where email = :acc LIMIT 1")
    fun getUser(acc: String) : UserModel?

    @Query("SELECT * FROM UserModel")
    suspend fun getAllUser() : List<UserModel>
}