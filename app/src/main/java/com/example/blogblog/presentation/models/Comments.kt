package com.example.blogblog.presentation.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments"
)
data class Comments(
    @PrimaryKey(autoGenerate = true)
    var tablecommentID: Int? = null,
    val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)
