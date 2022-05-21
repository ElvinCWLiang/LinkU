package com.project.linku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArticleModel::class, FriendModel::class, UserModel::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {

    companion object {
        private var instance: LocalDatabase? = null
        private var db_name = "data"

        fun getInstance(context: Context): LocalDatabase {
            return instance ?: Room.databaseBuilder(context, LocalDatabase::class.java, db_name)
                .fallbackToDestructiveMigration()
                .build().also {
                    instance = it
                }
        }
    }

    abstract fun dataDao(): Dao
}