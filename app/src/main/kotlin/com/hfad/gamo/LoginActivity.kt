package com.hfad.gamo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hfad.gamo.Component.sharedPreferences
import io.wiffy.extension.BuildConfig
import io.wiffy.extension.encrypt
import io.wiffy.extension.getMACAddress
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        Component.default_url = "http://172.30.1.2:17394"

        /*FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                    }
                });*/

        login_button.setOnClickListener() {
            //Log.i("id,pwd", id.text.toString() + pwd.text.toString())
            LoginDialog(this).show()
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
        if (BuildConfig.DEBUG) {
//            console(str)
        }
//        toast("로그인에 실패하였습니다.")
        Toast.makeText(applicationContext, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()

        this.recreate()
    }

    fun networkFailed() {
        //toast("네트워크에 연결할 수 없습니다.")
        Toast.makeText(applicationContext, "네트워크를 확인해 주세요.", Toast.LENGTH_SHORT).show()

        this.recreate()
    }
}