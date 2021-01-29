package com.gachon.moga

import android.annotation.SuppressLint
import java.lang.Exception
import java.text.SimpleDateFormat
import com.gachon.moga.Component.colorCount

@SuppressLint("SimpleDateFormat")
fun classToTime(time: String): LongArray {
    val format = SimpleDateFormat("HH:mm:ss")
    var start: String
    var end: String
    try {
        when (time) {
            "A" -> {
                start = "09:30:00"
                end = "10:45:00"
            }
            "B" -> {
                start = "11:00:00"
                end = "12:15:00"
            }
            "C" -> {
                start = "13:00:00"
                end = "14:15:00"
            }
            "D" -> {
                start = "14:30:00"
                end = "15:45:00"
            }
            "E" -> {
                start = "16:00:00"
                end = "17:15:00"
            }
            else -> {
                when (time.toInt()) {
                    in 1..8 -> {
                        start = "${(time.toInt() + 8)}:00:00"
                        end = "${(time.toInt() + 8)}:50:00"
                    }
                    9 -> {
                        start = "17:30:00"
                        end = "18:20:00"
                    }
                    10 -> {
                        start = "18:25:00"
                        end = "19:15:00"
                    }
                    11 -> {
                        start = "19:20:00"
                        end = "20:10:00"
                    }
                    12 -> {
                        start = "20:15:00"
                        end = "21:05:00"
                    }
                    13 -> {
                        start = "21:10:00"
                        end = "22:00:00"
                    }
                    14 -> {
                        start = "22:05:00"
                        end = "22:55:00"
                    }
                    else -> {
                        start = "00:00:00"
                        end = "00:00:00"
                    }
                }
            }
        }
    } catch (e: Exception) {
        start = "00:00:00"
        end = "00:00:00"
    }

    return longArrayOf(format.parse(start)?.time ?: 0L, format.parse(end)?.time ?: 0L)
}

fun getRandomColorId(): Int = Component.timeTableColor[colorCount % 8].apply {
    colorCount += 1
}

fun dayToInt(day: String): Int = when (day) {
    "월" -> 0
    "화" -> 1
    "수" -> 2
    "목" -> 3
    "금" -> 4
    else -> 5
}

fun intToDay(int: Int): String = when (int) {
    0 -> "월"
    1 -> "화"
    2 -> "수"
    3 -> "목"
    4 -> "금"
    else -> "토"
}
