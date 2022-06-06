package com.example.blogblog.data.database


import androidx.room.*
import com.example.blogblog.presentation.models.Comments
import com.example.blogblog.presentation.models.Posts


@Dao
interface PostsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPosts(posts: List<Posts>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addComments(comment: List<Comments>)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<Posts>

    @Query("SELECT * FROM comments")
    suspend fun getComments(): List<Comments>

    @Query("SELECT * FROM comments WHERE postId LIKE :postId")
    suspend fun getPostComments(postId: Int? = null): List<Comments>

    @Delete
    suspend fun deletePost(posts: Posts)
}
