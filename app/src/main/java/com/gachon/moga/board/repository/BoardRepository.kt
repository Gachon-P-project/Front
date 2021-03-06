package com.gachon.moga.board.repository

import android.util.Log
import com.gachon.moga.BOARD_SUBJECT
import com.gachon.moga.BOARD_FREE
import com.gachon.moga.BOARD_MAJOR

import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.network.BoardClient
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import javax.inject.Inject

class BoardRepository @Inject constructor(
    private val boardClient: BoardClient
) : Repository {

    suspend fun fetchPostings(
        boardInfo: BoardInfo,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ): List<Posting> {
        return when (boardInfo.boardType) {
            BOARD_SUBJECT -> fetchSubjectPostings(boardInfo, onSuccess, onError)
            BOARD_MAJOR -> fetchMajorPostings(onSuccess, onError)
            BOARD_FREE -> fetchFreePostings(onSuccess, onError)
            else -> listOf()
        }
    }

    private suspend fun fetchSubjectPostings(
        boardInfo: BoardInfo,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ): List<Posting> {

        lateinit var listPosting: List<Posting>

        val response = boardClient.fetchSubjectPostings(
            subject = boardInfo.title ?: "",
            professor = boardInfo.professor ?: ""
        )

        response.suspendOnSuccess {
            data.whatIfNotNull {
                listPosting = this.data!!
                onSuccess()
            }
        }

        return listPosting
    }

    private suspend fun fetchMajorPostings(
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ): List<Posting> {

        lateinit var listPosting: List<Posting>

        val response = boardClient.fetchMajorPostings()

        response.suspendOnSuccess {
            data.whatIfNotNull {
                listPosting = this.data!!
                onSuccess()
            }
        }

        return listPosting
    }

    private suspend fun fetchFreePostings(
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ): List<Posting> {

        lateinit var listPosting: List<Posting>

        val response = boardClient.fetchFreePostings()

        response.suspendOnSuccess {
            data.whatIfNotNull {
                listPosting = this.data!!
                onSuccess()
            }
        }
            .onError { Log.d("retrofit", "error") }
            .onException { Log.d("retrofit", "exception") }

        return listPosting
    }

}

