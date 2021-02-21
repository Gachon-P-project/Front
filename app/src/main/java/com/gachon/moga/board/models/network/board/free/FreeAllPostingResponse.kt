package com.gachon.moga.board.models.network.board.free

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class FreeAllPostingResponse(
    @field:Json(name = "post_no") val postNo: Int,
    @field:Json(name = "post_title") val postTitle: String,
    @field:Json(name = "post_contents") val postContents: String,
    @field:Json(name = "wrt_date") val wrtDate: String,
    @field:Json(name = "reply_yn") val replyYN: String,
    @field:Json(name = "major_name") val majorName: String,
    @field:Json(name = "user_no") val userNo: Int,
    @field:Json(name = "board_flag") val boardFlag: Int,
    @field:Json(name = "nickname") val nickName: String,
    @field:Json(name = "reply_cnt") val replyCnt: Int,
    @field:Json(name = "like_cnt") val likeCnt: Int,
    @field:Json(name = "like_user") val likeUser: Int,
) : Parcelable