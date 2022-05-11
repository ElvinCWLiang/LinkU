package com.example.linku.data.local

interface DataDao {
    fun deleteArticle()

    fun deleteArticle(vararg articleModelArr: ArticleModel?)

    fun getArticleResponse(str: String?): List<ArticleModel?>?

    fun getBoardArticle(str: String?): List<ArticleModel?>?

    fun getConversation(str: String?): List<FriendModel?>?

    fun getFreindList(): List<FriendModel?>?

    fun getallArticle(): Any?

    fun insertArticle(vararg articleModel: ArticleModel?)

    fun insertFriendList(vararg friendModel: FriendModel?)

    fun updateArticle(vararg articleModel: ArticleModel?)

}