package com.gachon.moga.board.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ToPosting(
    var boardType: Int = -1,
    var postNo: Int = -1,
    var writerNo: Int = -1,
    var pageNo: Int = -1,
    var subjectName: String? = null,
    var professorName: String? = null
) : Parcelable
