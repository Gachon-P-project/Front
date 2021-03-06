package com.gachon.moga.board.repository

import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.network.BoardClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostingRepository @Inject constructor(
    private val boardRepository: BoardRepository
) : Repository {

    suspend fun fetchPosting(boardInfo: BoardInfo, postNo: Int): Posting? {

        val postings: List<Posting> = boardRepository.fetchPostings(boardInfo, {}, {})

        return findCurrentPosting(postings, postNo)
    }

    private fun findCurrentPosting(postings: List<Posting>, postNo: Int): Posting? {
        for(posting in postings) {
            if (posting.postNo == postNo) return posting
        }
        return null
    }
}