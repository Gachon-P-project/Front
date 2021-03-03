package com.gachon.moga.board.ui.board

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gachon.moga.board.base.LiveCoroutinesViewModel
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.repository.BoardRepository
import com.skydoves.bindables.bindingProperty
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class BoardViewModel @AssistedInject constructor(
    private val boardRepository: BoardRepository,
    @Assisted private val boardInfo: BoardInfo
) : LiveCoroutinesViewModel() {

    private val postingListLiveDataPrivate = MutableLiveData<List<Posting>>()
    val postingListLiveData: LiveData<List<Posting>> get() = postingListLiveDataPrivate

    // val toolbarTitleLiveData: LiveData<String>

    @get:Bindable
    var isLoading: Boolean by bindingProperty(false)
        private set


    init {
        fetchPostings(boardInfo)
    }


    private fun fetchPostings(boardInfo: BoardInfo) {
        isLoading = true
        GlobalScope.launch {
            postingListLiveDataPrivate.postValue(boardRepository.fetchPostings(
                boardInfo = boardInfo,
                onSuccess = { isLoading = false },
                onError = { }
            ))
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(boardInfo: BoardInfo): BoardViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            boardInfo: BoardInfo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(boardInfo) as T
            }
        }
    }



}