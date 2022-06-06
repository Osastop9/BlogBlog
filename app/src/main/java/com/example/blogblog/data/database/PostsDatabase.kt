package com.example.blogblog.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.blogblog.presentation.models.Comments
import com.example.blogblog.presentation.models.Posts

@Database(entities = [Posts::class, Comments::class], version = 1)
abstract class PostsDatabase : RoomDatabase() {

    abstract fun getPostDao(): PostsDAO
}
