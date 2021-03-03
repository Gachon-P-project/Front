package com.gachon.moga.board.ui.board

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gachon.moga.board.base.LiveCoroutinesViewModel
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.repository.BoardRepository
import com.skydoves.bindables.bindingProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : LiveCoroutinesViewModel() {

    private val postingListLiveDataPrivate = MutableLiveData<List<Posting>>()
    val postingListLiveData: LiveData<List<Posting>> get() = postingListLiveDataPrivate

    @get:Bindable
    var isLoading: Boolean by bindingProperty(false)
        private set

    fun fetchPostings(boardInfo: BoardInfo) {
        isLoading = true
        GlobalScope.launch {
            postingListLiveDataPrivate.postValue(boardRepository.fetchPostings(
                boardInfo = boardInfo,
                onSuccess = { isLoading = false },
                onError = { }
            ))
        }
    }
}