package com.gachon.moga

import java.io.Serializable
import java.util.*

data class Parse(
    val value: String, val text: String, val data: String,
    val isNotification: Boolean, val isNew: Boolean, val isSave: Boolean, val link: String
) : Serializable

class ParseList : ArrayList<Parse>(), Serializable


data class ContactInformation(val dept: String, val name: String, val tel: String)

data class TimeTableInformation(
    val day: String,
    val subject: String,
    val professor: String,
    val place: String,
    val start: Long,
    val end: Long
) {
    val information = "$day%^$subject%^$professor%^$place%^$start%^$end"

    override fun toString() =
        "\nday : $day \n" +
                "subject : $subject \n" +
                "professor : $professor \n" +
                "place : $place \n" +
                "time : $start - $end"
}

data class ClassDataInformation(val name: String, val time: String, val room: String)

data class StudentInformation(
    val name: String?,
    val number: String?,
    val id: String?,
    val password: String?,
    val department: String?,
    val imageURL: String?,
    val clubCD: String?
) {
    override fun toString() = "name:$name\nnumber:$number\ndepartment:$department"
}


data class CreditInformation(
    val name: String,
    val isu: String,
    val chu: String
)

data class CreditFormal(
    val year: String,
    val term: String,
    val name: String,
    val grade: Int,
    val credit: Int,
    val mark: String
) {
    override fun toString() = "${year}년 $term $name ${grade}학년 ${credit}학점 ($mark)"
}

data class CreditAverage(val credit: Double, val score: Double, val mark: Double) {
    override fun toString() =
        "이수학점:<font color=\"#EC6746\">${credit.toInt()}</font>/평점:<font color=\"#EC6746\">$mark</font>/백분률 점수:<font color=\"#EC6746\">$score</font>"
}