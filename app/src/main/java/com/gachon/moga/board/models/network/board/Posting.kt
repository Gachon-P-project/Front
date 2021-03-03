package com.gachon.moga.board.models.network.board

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Posting(
        @field:Json(name = "post_no") val postNo: Int = 0,
        @field:Json(name = "post_title") val postTitle: String = "null",
        @field:Json(name = "post_contents") val postContents: String = "null",
        @field:Json(name = "wrt_date") val wrtDate: String = "null",
        @field:Json(name = "reply_yn") val replyYN: String = "null",
        @field:Json(name = "major_name") val majorName: String = "null",
        @field:Json(name = "subject_name") val subjectName: String = "null",
        @field:Json(name = "professor_name") val professorName: String = "null",
        @field:Json(name = "user_no") val userNo: Int = 0,
        @field:Json(name = "board_flag") val boardFlag: Int = 0,
        @field:Json(name = "nickname") val nickName: String = "null",
        @field:Json(name = "reply_cnt") val replyCnt: Int = 0,
        @field:Json(name = "like_cnt") val likeCnt: Int = 0,
        @field:Json(name = "like_user") val likeUser: Int = 0
) : Parcelable