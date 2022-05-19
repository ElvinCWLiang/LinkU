package com.example.linku.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ArticleModel")
data class ArticleModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "publishTime")
    var publishTime: Long = 0,
    @ColumnInfo(name = "publishBoard")
    var publishBoard: String = "",
    @ColumnInfo(name = "publishAuthor")
    var publishAuthor: String? = "",
    @ColumnInfo(name = "publishTitle")
    var publishTitle: String? = "",
    @ColumnInfo(name = "publishContent")
    var publishContent: String? = "",
    @ColumnInfo(name = "reply")
    var reply: String? = ""
)

@Entity(tableName = "FriendModel")
data class FriendModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "email")
    var email: String = "",
    @ColumnInfo(name = "emailfrom")
    var emailfrom: String = "",
    @ColumnInfo(name = "content")
    var content: String? = "",
    @ColumnInfo(name = "time")
    var time: Long = 0,
    @ColumnInfo(name = "type")
    var type: Int = 0
)

@Entity(tableName = "UserModel")
data class UserModel(
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String = "",
    @ColumnInfo(name = "useruri")
    var useruri: String = "",
    @ColumnInfo(name = "userintroduction")
    var userintroduction: String = ""
)