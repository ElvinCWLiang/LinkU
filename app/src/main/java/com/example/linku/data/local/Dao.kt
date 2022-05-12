package com.example.linku.data.local

interface Dao {
    fun deleteArticle()

    fun deleteArticle(vararg articleModelArr: ArticleModel?)

    fun getArticleResponse(str: String?): List<ArticleModel?>

    fun getBoardArticle(str: String?): List<ArticleModel?>

    fun getConversation(str: String?): List<FriendModel?>

    fun getFreindList(): List<FriendModel?>

    fun getallArticle(): List<ArticleModel?>

    fun insertArticle(vararg articleModel: ArticleModel?)

    fun insertFriendList(vararg friendModel: FriendModel?)

    fun updateArticle(vararg articleModel: ArticleModel?)

}