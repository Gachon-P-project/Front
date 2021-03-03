package com.gachon.moga.board.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.bindables.BindingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

abstract class LiveCoroutinesViewModel : BindingViewModel() {

    fun <T> Flow<T>.asLiveDataOnViewModelScope(): LiveData<T> {
        return asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
    }
}