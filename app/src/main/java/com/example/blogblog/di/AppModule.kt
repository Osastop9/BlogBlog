package com.example.blogblog.di

import android.content.Context
import androidx.room.Room
import com.example.blogblog.data.api.PostsAPI
import com.example.blogblog.data.database.PostsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val BASE_URL = "https://jsonplaceholder.typicode.com"

    @Singleton
    @Provides
    fun getAPI(retrofit: Retrofit): PostsAPI {
        return retrofit.create(PostsAPI::class.java)
    }

    @Singleton
    @Provides
    fun getRetroInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun providePostDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        PostsDatabase::class.java,
        "posts.db"
    ).build()

    @Singleton
    @Provides
    fun providePostDao(db: PostsDatabase) = db.getPostDao()
}
