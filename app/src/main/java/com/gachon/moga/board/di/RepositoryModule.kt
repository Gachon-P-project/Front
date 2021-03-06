package com.gachon.moga.board.di

import com.gachon.moga.board.network.BoardClient
import com.gachon.moga.board.repository.BoardRepository
import com.gachon.moga.board.repository.PostingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideBoardRepository(
        boardClient: BoardClient
    ): BoardRepository {
        return BoardRepository(boardClient)
    }

    @Provides
    @ViewModelScoped
    fun providePostingRepository(
        boardRepository: BoardRepository
    ): PostingRepository {
        return PostingRepository(boardRepository)
    }


}