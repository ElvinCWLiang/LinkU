package com.example.linku.data.local

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Delete
    fun deleteArticle(vararg articleModelArr: ArticleModel?)

    @Query("SELECT * FROM ARTICLEMODEL WHERE reply = :articleId")
    fun getArticleResponse(articleId: String): List<ArticleModel>

    @Query("SELECT * FROM ARTICLEMODEL WHERE publishTitle != ''")
    fun getallArticle(): List<ArticleModel>

    @Query("SELECT * FROM ARTICLEMODEL WHERE publishBoard LIKE :str AND publishTitle != ''")
    fun getBoardArticle(str: String?): List<ArticleModel>

//@Query("SELECT * FROM FRIENDMODEL WHERE EMAIL = :str order by TIME ASC")
    @Query("SELECT * FROM FRIENDMODEL order by TIME ASC")
    fun getConversation(): List<FriendModel>

    @Query("SELECT * FROM FRIENDMODEL WHERE ID IN (SELECT MIN(ID) AS ID FROM FRIENDMODEL GROUP BY EMAIL)")
    fun getFreindList(): List<FriendModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(vararg articleModel: ArticleModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriendList(vararg friendModel: FriendModel?)

    @Update
    fun updateArticle(vararg articleModel: ArticleModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserList(vararg userModel: UserModel?)

    @Query("SELECT * FROM UserModel where email = :acc LIMIT 1")
    fun getUser(acc: String) : UserModel

    @Query("SELECT * FROM UserModel")
    fun getAllUser() : List<UserModel>
}