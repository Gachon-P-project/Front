package com.hfad.gamo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_sign_in.*
import io.wiffy.extension.encrypt
import io.wiffy.extension.getMACAddress
import io.wiffy.extension.isNetworkConnected


@Suppress("DEPRECATION")
class LoginDialog(context: Context) : SuperContract.SuperDialog(context) {
    private var tempLogin = false

    @SuppressLint("ApplySharedPref", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sign_in)

        settingColor()

        logout.setOnClickListener {
            if (!isNetworkConnected(context)) {
                toast("로그아웃에 문제가 발생했습니다.")
            } else {
                logout()
            }
        }

        //진짜 로그인
        isLogin(Component.isLogin)


        gachon.text = Html.fromHtml(
                "<u>가천대학교 홈페이지</u>"
        )
        gachonname.text = "${getSharedItem("name", "")}님 안녕하세요!"
        gachon.setOnClickListener {
            Component.noneVisible = true
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://gachon.ac.kr/")))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun isLogin(bool: Boolean) {

        //진짜 로그인
        login.setOnClickListener {
            login()
        }


        tempLogin = bool
        if (bool) {
            frame_logined.visibility = View.VISIBLE
            frame_none_logined.visibility = View.GONE
        } else {
            frame_logined.visibility = View.GONE
            frame_none_logined.visibility = View.VISIBLE
        }
    }


    private fun settingColor() {
        val color = getThemeColor()
        val style = getThemeButtonResource()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            login.setBackgroundResource(style)
            logout.setBackgroundResource(style)
        } else {
            login.setBackgroundColor(ContextCompat.getColor(context, color))
            logout.setBackgroundColor(ContextCompat.getColor(context, color))
        }
    }

    // 진짜 로그인 과정.
    @SuppressLint("InflateParams")
    private fun login() {
        /*val prompt = LayoutInflater.from(context).inflate(R.layout.dialog_login, null)

        AlertDialog.Builder(context).apply {
            setView(prompt)
            setTitle("계정 정보")
            setCancelable(false)
            setPositiveButton("로그인") { dialog, _ ->
                if (prompt.login_name.text.toString().isBlank() || prompt.login_password.text.toString().isBlank()) {
                    toast("계정을 확인해주세요.")
                } else if (!isNetworkConnected(context)) {
                    toast("인터넷 연결을 확인해주세요.")
                    dialog.cancel()
                } else {
                    executeLogin(
                        prompt.login_name.text.toString(),
                        prompt.login_password.text.toString()
                    )
                }
            }
            setNegativeButton("취소") { dialog, _ -> dialog.cancel() }

        }.show()*/

    }

    private fun executeLogin(id: String, password: String) {
        //LoginAsyncTask(id, password, this).execute()
    }

    fun sendContext(): Context = context

    @SuppressLint("ApplySharedPref")
    private fun logout() {

        /*val number = getSharedItem<String>("number")
        removeSharedItems(
            "id",
            "password",
            "name",
            "number",
            "pattern",
            "department",
            "image",
            "birthday",
            "gender",
            "tableSet",
            "clubCD"
        )
        setSharedItem("login", false)
        Component.isLogin = false
        isLogin(false)
        (MainActivity.mView).mainLogout()

        if (Component.adminMode) {
            (MainActivity.mView).logout()
        } else {
            toast("로그아웃 되었습니다.")
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(number)

        Component.isBirthday = false
        MainActivity.mView.patternVisibility()
        dismiss()*/
    }

    @SuppressLint("ApplySharedPref", "SetTextI18n")
    fun saveInformation(information: StudentInformation) {
        with(information) {
            setSharedItems(
                Pair("id", id),
                Pair("password", encrypt(password!!, getMACAddress())),
                Pair("name", name),
                Pair("number", number),
                Pair("department", department),
                Pair("image", imageURL),
                Pair("clubCD", clubCD)
            )
            setSharedItem("login", true)
        }



        /*gachonname.text = "${getSharedItem("name", "")}님 안녕하세요!"
        Component.isLogin = true
        isLogin(true)
        (MainActivity.mView).mainLogin()
        if (information.number == "201735829" || information.number == "201635812") {
            (MainActivity.mView).login()
        } else if (information.department == "소프트웨어학과") {
            toast("우리과 학생이시군요?")
        } else {
            toast("로그인에 성공하였습니다.")
        }
        FirebaseMessaging.getInstance().subscribeToTopic(information.number!!)
            .addOnCompleteListener {
                setSharedItem("mynum", true)
            }
        (MainActivity.mView).askSetPattern()*/


        dismiss()
    }

    fun loginFailed(str: String) {
        if (BuildConfig.DEBUG) {
            console(str)
        }
        toast("로그인에 실패하였습니다.")
    }

    fun networkFailed() = toast("네트워크에 연결할 수 없습니다.")
}