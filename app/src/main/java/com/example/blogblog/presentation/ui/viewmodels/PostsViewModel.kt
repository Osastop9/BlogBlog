package com.example.blogblog.presentation.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogblog.data.repository.Repository
import com.example.blogblog.presentation.models.Posts
import com.example.blogblog.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val posts: MutableLiveData<Resource<List<Posts>>> = MutableLiveData()

    init {
        loadPosts()
    }

    private fun loadPosts() = viewModelScope.launch {
        if (repository.loadPost().isEmpty()) {
            getPosts()
        } else {
            val load = repository.loadPost()
            posts.postValue(Resource.Success(load))
        }
    }

    private fun getPosts() = viewModelScope.launch {
        val response = repository.getPosts()
        viewModelScope.launch {
            posts.postValue(handlePostsResponse(response))
        }
    }

    fun addPost(posts: Posts) {
        viewModelScope.launch {
            val response = repository.addPost(posts)
            if (response.isSuccessful) {
                repository.addPostToDatabase(response.body()!!)
                loadPosts()
            }
        }
    }

    fun filterPosts(userId: Int) = viewModelScope.launch {
        posts.postValue(Resource.Loading())
        val response = repository.filterPosts(userId)
        posts.postValue(handleFilterPostsResponse(response))
    }

    private suspend fun handlePostsResponse(response: Response<List<Posts>>): Resource<List<Posts>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(
                    repository.savePostToDb(resultResponse)
                )
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleFilterPostsResponse(response: Response<List<Posts>>): Resource<List<Posts>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
