package com.gachon.moga.moga

import android.content.Context
import com.github.eunsiljo.timetablelib.data.TimeTableData
import com.gachon.moga.SuperContract

interface TimeTableContract {
    abstract class View : SuperContract.SuperFragment() {
        abstract fun initView()
        abstract fun initTable(set: HashSet<String>)
        abstract fun sendContext(): Context?
        /*override fun toast(str: String) {
            Toast.makeText(activity, str, Toast.LENGTH_SHORT).apply {
                view.findViewById<TextView>(android.R.id.message)
                    ?.let { it.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER }
            }
                .show()
        }*/
    }

    interface Presenter : SuperContract.SuperPresenter {
        fun setTableList(set: HashSet<String>): ArrayList<TimeTableData>
        fun resetTable()
        fun setLogin(info: String)
    }
}