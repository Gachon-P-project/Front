package com.hfad.gamo.timeTable

import android.annotation.SuppressLint
import com.hfad.gamo.*
import io.wiffy.extension.isNetworkConnected
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup
import java.lang.Exception
import java.text.SimpleDateFormat

class TimeTableAsyncTask(val mView: TimeTableContract.View, val number: String) :
    SuperContract.SuperAsyncTask<Void, Void, Int>() {

    val set = HashSet<String>()
    private val subjectSet = HashSet<String>()

    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("HH:mm:ss")
    private val sem = when (2) {
        1 -> "10"
        2 -> "20"
        3 -> "11"
        else -> "21"
    }
    private val year = 2020;

    override fun onPreExecute() {
        //Component.getBuilder()?.show()
    }

    override fun doInBackground(vararg params: Void?): Int {
        if (!isNetworkConnected(mView.sendContext()!!)) {
            return ACTION_FAILURE
        }
        return try {
            val page = Jsoup.parseBodyFragment(
                EntityUtils.toString(
                    DefaultHttpClient().execute(
                        HttpPost("http://smart.gachon.ac.kr:8080/WebMain?YEAR=$year&TERM_CD=$sem&STUDENT_NO=$number&GROUP_CD=CS&SQL_ID=mobile%2Faffairs%3ACLASS_TIME_TABLE_STUDENT_SQL_S01&fsp_action=AffairsAction&fsp_cmd=executeMapList&callback_page=%2Fmobile%2Fgachon%2Faffairs%2FAffClassTimeTableList.jsp")
                    ).entity
                )
            ).select("li")

            for (element in page) {
                if (element.text().contains("요일")) {
                    val day = element.select("a").text().split("요일")[0]
                    for (data in element.select("ul.schedule_gray li")) {
                        val information = data.text().split("/")
                        val preInformation = information[0].split(" ")
                        val subject = data.html().split("<p>")[1].split("/")[0].trim()
                        val table = TimeTableInformation(
                            day.trim(),
                            subject,
                            information[2].trim(),
                            information[1].trim(),
                            format.parse(preInformation[1].let {
                                "${it.substring(0, 2)}:${it.substring(2, 4)}:00"
                            }.trim())?.time ?: 0,
                            format.parse(preInformation[3].let {
                                "${it.substring(0, 2)}:${it.substring(2, 4)}:00"
                            }.trim())?.time ?: 0
                        )
                        set.add(table.information)
                        subjectSet.add(subject)
                    }
                }
            }
            setSharedItem("tableSet", set)
            setSharedItem("subjectSet", subjectSet)
            mView.initTable(set)
            ACTION_SUCCESS
        } catch (e: Exception) {
            ACTION_FAILURE
        }
    }

    override fun onPostExecute(result: Int?) {
        /*Component.getBuilder()?.dismiss()
        if (result == ACTION_FAILURE) {
            mView.toast("인터넷 연결을 확인해주세요.")
        }*/
    }
}