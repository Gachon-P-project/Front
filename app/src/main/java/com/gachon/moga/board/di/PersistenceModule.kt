package com.gachon.moga.board.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.gachon.moga.appConstantPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PersistenceModule {

  @Provides
  @Singleton
  fun provideMogaPrefs(
    application: Application
  ): SharedPreferences =
    application.getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)


}