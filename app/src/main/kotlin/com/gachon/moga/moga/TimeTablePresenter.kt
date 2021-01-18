package com.gachon.moga.moga

import com.github.eunsiljo.timetablelib.data.TimeData
import com.github.eunsiljo.timetablelib.data.TimeTableData
import com.gachon.moga.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class TimeTablePresenter(val mView: TimeTableContract.View) : TimeTableContract.Presenter {
    override fun initPresent() = mView.initView()


    override fun setTableList(set: HashSet<String>): ArrayList<TimeTableData> {

        val superList = ArrayList<ArrayList<TimeData<Any?>?>>(6).apply {
            for (n in 0 until 6) add(ArrayList())
        }

        val realList = ArrayList<ArrayList<TimeData<Any?>>>(6).apply {
            for (n in 0 until 6) add(ArrayList())
        }

        val colorStateList = HashMap<String, Int>()


        for (value in set.iterator()) {
            value.split("%^").let {
                superList[dayToInt(it[0])].add(
                    TimeData(
                        0,
                        if (it[3].isBlank()) {
                            it[1]
                        } else {
                            "${it[1]}\n${it[3]}"
                        }
                        ,
                        getRandomColorId(),
                            R.color.white,
                        it[4].toLong(),
                        it[5].toLong()
                    )
                )
            }
        }

        for (v in 0..4) {
            Collections.sort(superList[v], TimeCompare)
        }

        val list = ArrayList<TimeTableData>().apply {
            for (n in 0 until 5) {
                for (x in 0 until superList[n].size) {
                    if (superList[n][x] == null) {
                        continue
                    }
                    if (x == superList[n].size - 1) {
                        realList[n].add(
                            TimeData(
                                1,
                                superList[n][x]?.title,
                                if (colorStateList.containsKey(superList[n][x]?.title)) {
                                    colorStateList[superList[n][x]?.title] ?: getRandomColorId()
                                } else {
                                    getRandomColorId().apply {
                                        colorStateList[superList[n][x]?.title ?: ""] = this
                                    }
                                },
                                R.color.white,
                                superList[n][x]?.startMills ?: 0,
                                superList[n][x]?.stopMills ?: 0
                            )
                        )
                        break
                    }
                    if (superList[n][x]?.title == superList[n][x + 1]?.title) {
                        var k = x + 1
                        var temp: Long? = 0
                        while (superList[n][x]?.title == superList[n][k]?.title) {
                            temp = superList[n][k]?.stopMills
                            superList[n][k] = null
                            if (k < superList[n].size - 1) {
                                k += 1
                            } else {
                                break
                            }
                        }
                        realList[n].add(
                            TimeData(
                                0,
                                superList[n][x]?.title,
                                if (colorStateList.containsKey(superList[n][x]?.title)) {
                                    colorStateList[superList[n][x]?.title] ?: getRandomColorId()
                                } else {
                                    getRandomColorId().apply {
                                        colorStateList[superList[n][x]?.title ?: ""] = this
                                    }
                                },
                                R.color.white,
                                superList[n][x]?.startMills ?: 0,
                                temp ?: 0
                            ))
                    } else {
                        realList[n].add(
                            TimeData(
                                0,
                                superList[n][x]?.title,
                                if (colorStateList.containsKey(superList[n][x]?.title)) {
                                    colorStateList[superList[n][x]?.title] ?: getRandomColorId()
                                } else {
                                    getRandomColorId().apply {
                                        colorStateList[superList[n][x]?.title ?: ""] = this
                                    }
                                },
                                R.color.white,
                                superList[n][x]?.startMills ?: 0,
                                superList[n][x]?.stopMills ?: 0
                            )
                        )
                    }
                }
                Collections.sort(realList[n], TimeCompare)
                add(TimeTableData(intToDay(n), realList[n]))
            }
        }
        superList.clear()
        colorStateList.clear()

        return list
    }

    override fun resetTable() {
        val info = getSharedItem<String>("number")
        if (info.length > 6) {
            TimeTableAsyncTask(mView, info).execute()
        }
    }

    override fun setLogin(info: String) {

        TimeTableAsyncTask(mView, info).execute()
        /*val mTable = getSharedItem<HashSet<String>>("tableSet")
        if (mTable.size <= 0) {
            TimeTableAsyncTask(mView, info).execute()
        } else {
            mView.initTable(mTable)
        }*/

        // TimeTableAsyncTask(mView, info).execute()


    }
}