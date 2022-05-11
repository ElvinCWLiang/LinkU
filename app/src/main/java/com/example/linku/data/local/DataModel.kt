package com.example.linku.data.local

data class ArticleModel(
    var id: String? = "",
    var publishAuthor: String? = "",
    var publishBoard: String? = "",
    var publishContent: String? = "",
    var publishTime: Long? = 0,
    var publishTitle: String? = "",
    var reply: String? = ""
)

data class FriendModel(
    var content: String? = "",
    var email: String? = "",
    var id: String? = "",
    var time: Long? = 0,
    var type: Int? = 0
)

data class User(
    var email: String? = ""
)