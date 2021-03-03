package com.gachon.moga.board.network

import com.gachon.moga.board.models.network.board.Posting
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BoardService {

  @GET("boards/subject/{subject}/{professor}/{userNo}")
  suspend fun fetchSubjectPostings(
    @Path("subject") subject: String,
    @Path("professor") professor: String,
    @Path("userNo") userNo: Int
  ): ApiResponse<List<Posting>>

  @GET("boards/free/{boardType}/{userNo}")
  suspend fun fetchFreePostings(
    @Path("boardType") boardType: Int,
    @Path("userNo") userNo: Int
  ): ApiResponse<List<Posting>>

  @GET("boards/major/{boardType}/{userNo}/{dept}")
  suspend fun fetchMajorPostings(
    @Path("boardType") boardType: Int,
    @Path("userNo") userNo: Int,
    @Path("dept") dept: String
  ): ApiResponse<List<Posting>>

}