package com.gachon.moga.moga

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.toolbox.Volley
import com.github.eunsiljo.timetablelib.view.TimeTableView
import com.gachon.moga.*
import com.gachon.moga.board.BoardActivity
import com.gachon.moga.MainActivity
import com.gachon.moga.VolleyForHttpMethod
import com.gachon.moga.board.models.BoardInfo
import kotlinx.android.synthetic.main.fragment_timetable.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.HashSet

@Suppress("DEPRECATION")
class TimeTableFragment : TimeTableContract.View() {
    private val TAG = "TimeTableFragment"
    var myView: View? = null
    private var mPresenter: TimeTablePresenter = TimeTablePresenter(this)
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
    private lateinit var tvToolbarSemester: TextView
    private lateinit var tvToolbarYear: TextView
//    private lateinit var swipeLayout: SwipeRefreshLayout


    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("HH:mm:ss")

//    private val year = "2020"
//    private val semester = "20"
    private val nowDate: LocalDate = LocalDate.now()
    private var year = nowDate.format(DateTimeFormatter.ofPattern("yyyy"))
    private val month = nowDate.format(DateTimeFormatter.ofPattern("MM"))
    private val day = nowDate.format(DateTimeFormatter.ofPattern("dd"))
    private lateinit var lastDaySaved: String
    private lateinit var userNo: String
    private lateinit var yearOfAdmission: String
    private var semester = when (month.toInt()) {
        3, 4, 5, 6 -> "10"      // 1학기
        9, 10, 11, 12 -> "20"   // 2학기
        7, 8 -> "11"            // 여름학기
        1, 2 -> "21"            // 겨울학기
        else -> "00"            // 에러
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volley = VolleyForHttpMethod(Volley.newRequestQueue(this.context))
        sharedPreferences = requireContext().getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        lastDaySaved = getSharedItem("lastDaySavingTimeTable", "")
        userNo = getSharedItem<String>("number")
        yearOfAdmission= userNo.substring(0, 4);
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_timetable, container, false)
        tvToolbarSemester = myView?.findViewById(R.id.tv_timetable_fragment_toolbar_semester)!!
        tvToolbarYear = myView?.findViewById(R.id.tv_timetable_fragment_toolbar_year)!!
//        swipeLayout = myView?.findViewById(R.id.swipeLayout_timetable_fragment)!!

        tvToolbarYear.text = getSharedItem<String>("year")+"년"
        tvToolbarSemester.text = when(getSharedItem<String>("semester")) {
            "10" -> "1학기"
            "11" -> "여름학기"
            "20" -> "2학기"
            "21" -> "겨울학기"
            else -> "시간표"
        }

        if(lastDaySaved != day || lastDaySaved == "")
            saveDataForTimeTable()
        else
            initTable(getSharedItem("tableSet"))

//        val tableSet = getSharedItem<HashSet<String>>("tableSet")
//        if(tableSet.size != 0) {
//            initTable(tableSet)
//        } else {
//            saveDataForTimeTable()
//        }
        //saveDataForTimeTable()
//          스와이프시 리프레시
//        swipeLayout.setColorSchemeResources(R.color.indigo500)
//        swipeLayout.setOnRefreshListener {
//            saveDataForTimeTable()
//
//            swipeLayout.isRefreshing = false
//        }

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
//        val pref = this.context!!.getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        val context = myView?.context



//        jsonObjectForTitle = JSONObject(pref.getString("subject_professorJSONObject", null))
        jsonObjectForTitle = JSONObject(getSharedItem<String>("subject_professorJSONObject"))
        Handler(Looper.getMainLooper()).post {
            val list = mPresenter.setTableList(set)
            myView?.mTable?.let {
                it.setOnTimeItemClickListener { _, _, data ->
                    val professor = jsonObjectForTitle!!.get(data.time.title.split("\n")[0])
                    val intent = Intent(context, BoardActivity::class.java)
                   /* intent.putExtra("title", data.time.title.split("\n")[0])
                    intent.putExtra("professor", professor.toString())
                    intent.putExtra("boardType", 0)
                    */
                    val title = data.time.title.split("\n")[0]
                    val professorString = professor.toString()
                    val boardType = 0

                    intent.putExtra("BoardInfo", BoardInfo(title, professorString, boardType))

                    startActivity(intent)
                }
                it.setStartHour(9)
                it.setShowHeader(true)
                it.setTableMode(TimeTableView.TableMode.SHORT)
                it.setTimeTable(0, list)

                (activity as MainActivity).stopLoadingDialog()
            }
        }
    }

    private fun saveDataForTimeTable() {

        urlInquireTimeTable = Component.default_url.plus(this.context?.getString(R.string.inquireTimeTable, userNo, year, semester))


        volley.getString(urlInquireTimeTable) { response ->
            responseJSONArray = JSONArray(response)
            Log.d(TAG, "saveDataForTimeTable: response : $response")

            if(semester != "00" && (yearOfAdmission.toInt() - 1) <= year.toInt() && (response == "" || response == null || responseJSONArray.getJSONObject(0).getJSONArray("data") == null) || response == "[{\"day\":\"\",\"data\":[]}]"){
                semester = when (semester) {
                    "10" -> "21"
                    "11" -> "10"
                    "20" -> "21"
                    "21" -> "20"
                    else -> "00"            // 에러
                }
                if (semester == "21") {
                    year = (year.toInt() - 1).toString()
                }

                if((yearOfAdmission.toInt() - 1) <= year.toInt())
                    saveDataForTimeTable()
                else
                    initTable(set)


            } else {

//                sharedPreferences에 저장된 값과 비교해서 다르면 툴바 업데이트
                if(getSharedItem<String>("semester") != semester || getSharedItem<String>("year") != year) {
                    updateToolbarTitle(year, semester)
                    setSharedItem("semester", semester)
                    Log.i("semester", getSharedItem("semester"))
                    setSharedItem("year", year)
                    Log.i("year", getSharedItem("year"))
                }


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

                /*for (i in 0 until subjectSet.count()) {
                    jsonObject.put(subjectSetIterator.next(), professorSetIterator.next())
                }*/

                for (i in 0 until subjectSet.count()) {
                    val subject: String = subjectSetIterator.next()
                    jsonObject.put(subject, professorMap[subject])
                }

                setSharedItem("tableSet", set)
                setSharedItem("subjectSet", subjectHashSet)
                setSharedItem("professorSet", professorSet)
                setSharedItem("subject_professorJSONObject", jsonObject.toString());
                initTable(set)


            }

        }
    }

    private fun updateToolbarTitle(year: String, semester: String) {
        tvToolbarYear.text = year+"년"
        tvToolbarSemester.text = when(semester) {
            "10" -> "1학기"
            "11" -> "여름학기"
            "20" -> "2학기"
            "21" -> "겨울학기"
            else -> "시간표"
        }
    }
}

