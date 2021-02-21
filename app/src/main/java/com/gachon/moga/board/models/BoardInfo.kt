package com.gachon.moga.board.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardInfo(val title: String?, val professor: String?,
                     val boardType: Int) : Parcelable