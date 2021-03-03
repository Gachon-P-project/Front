package com.gachon.moga.board.network

import android.content.SharedPreferences
import com.gachon.moga.BOARD_FREE
import com.gachon.moga.BOARD_MAJOR
import com.gachon.moga.BOARD_SUBJECT
import com.gachon.moga.board.extension.getDepartment
import com.gachon.moga.board.extension.getUserNo
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.models.network.board.free.FreeAllPostingResponse
import com.gachon.moga.board.models.network.board.major.MajorAllPostingResponse
import com.gachon.moga.board.models.network.board.subject.SubjectAllPostingResponse
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class BoardClient @Inject constructor(
  private val boardService: BoardService,
  private val prefs: SharedPreferences
) {

  suspend fun fetchSubjectPostings(
    subject: String,
    professor: String
  ): ApiResponse<List<Posting>> =
    boardService.fetchSubjectPostings(
      subject = subject,
      professor = professor,
      userNo =  prefs.getUserNo()
    )

  suspend fun fetchFreePostings(): ApiResponse<List<Posting>> =
    boardService.fetchFreePostings(
      boardType = BOARD_FREE,
      userNo = prefs.getUserNo()
    )

  suspend fun fetchMajorPostings(): ApiResponse<List<Posting>> =
    boardService.fetchMajorPostings(
      boardType = BOARD_MAJOR,
      userNo = prefs.getUserNo(),
      dept = prefs.getDepartment() ?: "ERROR"
    )

}