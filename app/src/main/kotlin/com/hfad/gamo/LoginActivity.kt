package com.hfad.gamo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hfad.gamo.Component.sharedPreferences
import io.wiffy.extension.encrypt
import io.wiffy.extension.getMACAddress
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)

        login_button.setOnClickListener() {
            executeLogin(id.text.toString(), pwd.text.toString())
        }



    }


    private fun executeLogin(id: String, password: String) {
        LoginAsyncTask(id, password, this).execute()
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
    }


    fun loginFailed(str: String) {
        /*if (BuildConfig.DEBUG) {
            console(str)
        }
        toast("로그인에 실패하였습니다.")*/
    }

    fun networkFailed() {
        //toast("네트워크에 연결할 수 없습니다.")
    }
}