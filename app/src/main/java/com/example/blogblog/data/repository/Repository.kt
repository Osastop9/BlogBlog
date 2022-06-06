package com.example.blogblog.data.repository

import com.example.blogblog.data.api.PostsAPI
import com.example.blogblog.data.database.PostsDAO
import com.example.blogblog.presentation.models.Comments
import com.example.blogblog.presentation.models.Posts

import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val postsDAO: PostsDAO,
    private val postsAPI: PostsAPI
) {
    suspend fun getPosts(): Response<List<Posts>> {
        return postsAPI.getPosts()
    }

    suspend fun addPost(posts: Posts): Response<Posts> {
        return postsAPI.addPosts(posts)
    }

    suspend fun addComment(postId: Int, comments: Comments): Response<Comments> {
        return postsAPI.addComments(postId, comments)
    }

    suspend fun filterPosts(userId: Int) =
        postsAPI.getPosts(userId)

    suspend fun getComments(postId: Int): Response<List<Comments>> {
        return postsAPI.getComments(postId)
    }

    suspend fun savePostToDb(posts: List<Posts>): List<Posts> {
        postsDAO.addPosts(posts)
        return postsDAO.getAllPosts()
    }

    suspend fun saveCommentsToDb(comments: List<Comments>): List<Comments> {
        postsDAO.addComments(comments)
        return comments
    }

    suspend fun loadPost(): List<Posts> {
        return postsDAO.getAllPosts()
    }

    suspend fun loadComments(postId: Int): List<Comments> {
        return postsDAO.getPostComments(postId)
    }

    suspend fun addPostToDatabase(posts: Posts) {
        postsDAO.addPosts(listOf(posts))
    }

    suspend fun addCommentToDatabase(comments: Comments) {
        postsDAO.addComments(listOf(comments))
    }
}
