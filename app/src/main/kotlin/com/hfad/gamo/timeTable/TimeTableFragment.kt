package com.hfad.gamo.timeTable

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.toolbox.Volley
import com.github.eunsiljo.timetablelib.view.TimeTableView
import com.hfad.gamo.*
import com.hfad.gamo.ClickedBoard.ClickedBoardActivity
import io.wiffy.extension.isNetworkConnected
import kotlinx.android.synthetic.main.fragment_information_timetable.view.*
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.Exception
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class TimeTableFragment : TimeTableContract.View() {
    var myView: View? = null
    lateinit var mPresenter: TimeTablePresenter
    private var mInfo: String? = null
    private var jsonObjectForTitle: JSONObject? = null
    val set = HashSet<String>()
    private val subjectSet = mutableSetOf<String>()
    private val professorMap = mutableMapOf<String,String>()
    private val professorSet = mutableSetOf<String>()
    private val jsonObject = JSONObject();
    private val subjectHashSet = HashSet<String>()
    private lateinit var volley : VolleyForHttpMethod
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var urlInquireTimeTable : String
    private lateinit var responseJSONArray: JSONArray
    private lateinit var responseJSONObject: JSONObject

    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("HH:mm:ss")
    private val sem = when (2) {
        1 -> "10"
        2 -> "20"
        3 -> "11"
        else -> "21"
    }
    private val year = "2020";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volley = VolleyForHttpMethod(Volley.newRequestQueue(this.context))
        sharedPreferences = this.context?.getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_information_timetable, container, false)

        saveDataForTimeTable()

        return myView
    }

    override fun sendContext() = context
    override fun initView() {
        changeTheme()
        /* mInfo?.let {
            loginInformationSetting(it)
        } ?: doneLogin(requireActivity(), context!!)*/
        /*myView?.swipe?.setOnRefreshListener {
            mPresenter.resetTable()
            myView?.swipe?.isRefreshing = false
        }*/
        loginInformationSetting(getSharedItem("number", "null"))
    }

    fun changeTheme() {
        /*myView?.swipe?.setColorSchemeColors(
            if (Component.darkTheme) {
                resources.getColor(getDarkColor1())
            } else {
                resources.getColor(getThemeColor())
            }
        )*/
    }

    fun loginInformationSetting(info: String) {
        mInfo = info
        myView?.let {
            if (info.length > 6) {
                mPresenter.setLogin(info)
            }
        }
    }

    fun resetTable() = mPresenter.resetTable()


    
    // 시간표에 과목들 띄우기
    // 시간표 선택 리스너
    override fun initTable(set: HashSet<String>) {
        val pref = this.context!!.getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        val context = myView?.context

        jsonObjectForTitle = JSONObject(pref.getString("subject_professorJSONObject", null))
        Handler(Looper.getMainLooper()).post {
            val list = mPresenter.setTableList(set)
            myView?.mTable?.let {
                it.setOnTimeItemClickListener { _, _, data ->
                    val professor = jsonObjectForTitle!!.get(data.time.title.split("\n")[0])
                    val intent = Intent(context, ClickedBoardActivity::class.java)
                    intent.putExtra("title", data.time.title.split("\n")[0])
                    intent.putExtra("professor", professor.toString())
                    startActivity(intent)
                }
                it.setStartHour(9)
                it.setShowHeader(true)
                it.setTableMode(TimeTableView.TableMode.SHORT)
                it.setTimeTable(0, list)
//                it.set
            }
        }
    }

    private fun saveDataForTimeTable() {

        urlInquireTimeTable = Component.default_url.plus(this.context?.getString(R.string.inquireTimeTable, sharedPreferences.getString("number", null), year, sem))

        volley.getString(urlInquireTimeTable) { response ->
            responseJSONArray = JSONArray(response)

            for (i in 0 until responseJSONArray.length()) {
                responseJSONObject = responseJSONArray[i] as JSONObject

                val day = responseJSONObject.get("day").toString().split("요일")[0]
                val data = responseJSONObject.get("data") as JSONArray

                for (k in 0 until data.length()) {
                    val information = (data[k] as JSONObject).getString("subject").split("/")
                    val time = (data[k] as JSONObject).getString("time").split("~")
                    val subject = information[0].trim()
                    val table = TimeTableInformation(
                            day.trim(),
                            subject,
                            information[2].trim(),
                            information[1].trim(),
                            format.parse(time[0].let {
                                "${it.substring(0, 2)}:${it.substring(2, 4)}:00"
                            }.trim())?.time ?: 0,
                            format.parse(time[1].let {
                                "${it.substring(1, 3)}:${it.substring(3, 5)}:00"
                            }.trim())?.time ?: 0
                    )

                    set.add(table.information)
                    subjectSet.add(subject)
                    subjectHashSet.add(subject)
                    professorMap[subject] = information[2].trim()
                }
            }

            for (string in subjectSet) {
                professorMap[string]?.let { professorSet.add(it) }
            }


            val subjectSetIterator = subjectSet.iterator()
            val professorSetIterator = professorSet.iterator()

            for (i in 0 until subjectSet.count()) {
                jsonObject.put(subjectSetIterator.next(), professorSetIterator.next())
            }

            setSharedItem("tableSet", set)
            setSharedItem("subjectSet", subjectHashSet)
            setSharedItem("professorSet", professorSet)
            setSharedItem("subject_professorJSONObject", jsonObject.toString());
            initTable(set)
        }
    }
}