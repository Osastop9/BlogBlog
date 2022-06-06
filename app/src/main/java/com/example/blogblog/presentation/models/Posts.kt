package com.example.blogblog.presentation.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "posts"
)
data class Posts(
    @PrimaryKey(autoGenerate = true)
    var tablepostID: Int? = null,
    val id: Int,
    val body: String,
    val title: String,
    val userId: Int
) : Serializable
