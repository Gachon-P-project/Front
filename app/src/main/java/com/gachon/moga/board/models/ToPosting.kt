package com.gachon.moga.board.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ToPosting(
    val boardType: Int,
    val postNo: Int,
    val writerNo: Int,
    val pageNo: Int,
    val subjectName: String,
    val professorName: String
) : Parcelable
