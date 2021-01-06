package com.hfad.gamo.timeTable

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.volley.toolbox.Volley
import com.hfad.gamo.*
import io.wiffy.extension.isNetworkConnected
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate

class TimeTableAsyncTask(private val mView: TimeTableContract.View, private val number: String) :
    SuperContract.SuperAsyncTask<Void, Void, Int>() {

    private val TAG = "TimeTableAsyncTask"
    val set = HashSet<String>()
    private val subjectSet = mutableSetOf<String>()
    private val professorMap = mutableMapOf<String,String>()
    private val professorSet = mutableSetOf<String>()
    private val jsonObject = JSONObject();
    private val subjectHashSet = HashSet<String>()
    private val volley = VolleyForHttpMethod(Volley.newRequestQueue(mView.context))
    private val sharedPreferences = mView.context?.getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
    private lateinit var urlInquireTimeTable : String
    private lateinit var responseJSONArray: JSONArray

    @SuppressLint("SimpleDateFormat")
    private val format_time = SimpleDateFormat("HH:mm:ss")
    @SuppressLint("SimpleDateFormat")
    private val format_data = SimpleDateFormat("yyyy-MM")

    private val nowDate: LocalDate = LocalDate.now()


    private val semester = when (2) {
        1 -> "10"
        2 -> "20"
        3 -> "11"
        else -> "21"
    }
    private val year = "2020";

    override fun onPreExecute() {
        //Component.getBuilder()?.show()
        Log.d(TAG, "doInBackground: now : $nowDate")
    }

    override fun doInBackground(vararg params: Void?): Int {
        if (!isNetworkConnected(mView.sendContext()!!)) {
            return ACTION_FAILURE
        }
        return try {
            val page = Jsoup.parseBodyFragment(
                EntityUtils.toString(
                    DefaultHttpClient().execute(
                        HttpPost("http://smart.gachon.ac.kr:8080/WebMain?YEAR=$year&TERM_CD=$semester&STUDENT_NO=$number&GROUP_CD=CS&SQL_ID=mobile%2Faffairs%3ACLASS_TIME_TABLE_STUDENT_SQL_S01&fsp_action=AffairsAction&fsp_cmd=executeMapList&callback_page=%2Fmobile%2Fgachon%2Faffairs%2FAffClassTimeTableList.jsp")
                    ).entity
                )
            ).select("li")

            if (sharedPreferences != null) {
                urlInquireTimeTable = Component.default_url.plus(mView.context?.getString(R.string.inquireTimeTable,sharedPreferences.getString("number ", null), year, semester ))
            }

            volley.getString(urlInquireTimeTable) { response ->
                responseJSONArray = JSONArray(response)
            }

            for (element in page) {
                if (element.text().contains("요일")) {
                    val day = element.select("a").text().split("요일")[0]
                    for (data in element.select("ul.schedule_gray li")) {
                        val information = data.text().split("/")
                        val preInformation = information[0].split(" ")
                        val subject = data.html().split("<p>")[1].split("/")[0].trim()
                        val professor = data.html().split("<p>")[1].split("/")[2].trim().dropLast(1)
                        val table = TimeTableInformation(
                            day.trim(),
                            subject,
                            information[2].trim(),
                            information[1].trim(),
                            format_time.parse(preInformation[1].let {
                                "${it.substring(0, 2)}:${it.substring(2, 4)}:00"
                            }.trim())?.time ?: 0,
                            format_time.parse(preInformation[3].let {
                                "${it.substring(0, 2)}:${it.substring(2, 4)}:00"
                            }.trim())?.time ?: 0
                        )
                        set.add(table.information)
                        subjectSet.add(subject)
                        subjectHashSet.add(subject)
                        professorMap[subject] = professor
                    }
                }
            }

            for(string in subjectSet) {
                professorMap[string]?.let { professorSet.add(it) }
            }


            val subjectSetIterator = subjectSet.iterator()
            val professorSetIterator = professorSet.iterator()

            for(i in 0 until subjectSet.count()) {
             jsonObject.put(subjectSetIterator.next(), professorSetIterator.next())
            }

            setSharedItem("tableSet", set)
            setSharedItem("subjectSet", subjectHashSet)
            setSharedItem("professorSet",professorSet)
            setSharedItem("subject_professorJSONObject", jsonObject.toString());
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