package com.gachon.moga.board.di

import android.content.SharedPreferences
import com.gachon.moga.board.network.HttpRequestInterceptor
import com.gachon.moga.board.network.BoardClient
import com.gachon.moga.board.network.BoardService
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(HttpRequestInterceptor())
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://moga.club/")
      .addConverterFactory(MoshiConverterFactory.create())
      .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
      .build()
  }

  @Provides
  @Singleton
  fun provideMogaService(retrofit: Retrofit): BoardService {
    return retrofit.create(BoardService::class.java)
  }

  @Provides
  @Singleton
  fun provideMogaClient(BoardService: BoardService, prefs: SharedPreferences): BoardClient {
    return BoardClient(BoardService, prefs)
  }

}