package com.hfad.gamo.timeTable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.eunsiljo.timetablelib.view.TimeTableView
import com.hfad.gamo.ClickedBoard.ClickedBoardActivity
import com.hfad.gamo.R
import com.hfad.gamo.appConstantPreferences
import com.hfad.gamo.getSharedItem
import kotlinx.android.synthetic.main.fragment_information_timetable.view.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class TimeTableFragment : TimeTableContract.View() {
    var myView: View? = null
    lateinit var mPresenter: TimeTablePresenter
    private var mInfo: String? = null
    private var jsonObject: JSONObject? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_information_timetable, container, false)
        mPresenter = TimeTablePresenter(this)
        mPresenter.initPresent()

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

        jsonObject = JSONObject(pref.getString("subject_professorJSONObject", null))
        Handler(Looper.getMainLooper()).post {
            val list = mPresenter.setTableList(set)
            myView?.mTable?.let {
                it.setOnTimeItemClickListener { _, _, data ->
                    val professor = jsonObject!!.get(data.time.title.split("\n")[0])
                    val intent = Intent(context, ClickedBoardActivity::class.java)
                    intent.putExtra("title", data.time.title.split("\n")[0])
                    intent.putExtra("professor", professor.toString())
                    startActivity(intent)
                }
                it.setStartHour(9)
                it.setShowHeader(true)
                it.setTableMode(TimeTableView.TableMode.SHORT)
                it.setTimeTable(0, list)
            }
        }
    }
}