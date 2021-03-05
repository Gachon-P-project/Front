package com.gachon.moga.board.ui.posting

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gachon.moga.BOARD_FREE
import com.gachon.moga.BOARD_MAJOR
import com.gachon.moga.BOARD_SUBJECT
import com.gachon.moga.board.base.LiveCoroutinesViewModel
import com.gachon.moga.board.extension.getDepartment
import com.gachon.moga.board.models.ToPosting
import com.gachon.moga.board.repository.PostingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class PostingViewModel @AssistedInject constructor(
    /*private val postingRepository: PostingRepository,*/
    private val prefs: SharedPreferences,
    @Assisted val toPosting: ToPosting
) : LiveCoroutinesViewModel() {

    private val toolbarTitleLiveDataPrivate: MutableLiveData<String> = MutableLiveData()
    val toolbarTitleLiveData: LiveData<String> get() = toolbarTitleLiveDataPrivate

    init {
        toolbarTitleLiveDataPrivate.value = getToolbarTitle(toPosting.boardType)
    }

    private fun getToolbarTitle(boardType: Int):String {
        return when(boardType) {
            BOARD_FREE -> defaultFreeBoard
            BOARD_MAJOR -> prefs.getDepartment() ?: defaultMajorBoard
            BOARD_SUBJECT -> toPosting.subjectName ?: defaultSubjectBoard
            else -> "게시판"
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(toPosting: ToPosting): PostingViewModel
    }

    companion object {
        private const val defaultFreeBoard = "자유 게시판"
        private const val defaultMajorBoard = "학과 게시판"
        private const val defaultSubjectBoard = "과목 게시판"

        fun provideFactory(
            assistedFactory: AssistedFactory,
            toPosting: ToPosting
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(toPosting) as T
            }
        }
    }
}