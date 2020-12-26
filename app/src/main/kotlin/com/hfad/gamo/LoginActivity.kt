package com.hfad.gamo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.hfad.gamo.Component.default_url
import com.hfad.gamo.Component.sharedPreferences
import io.wiffy.extension.BuildConfig
import io.wiffy.extension.encrypt
import io.wiffy.extension.getMACAddress
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val loginDialog: LoginDialog? = LoginDialog()
    private var volley: VolleyForHttpMethod? = null
    private var nickNameDialog: NickNameDialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        Component.default_url = "http://172.30.1.2:17394"

        volley = VolleyForHttpMethod(Volley.newRequestQueue(this))
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
            loginDialog?.start(this)
            newExecuteLogin(id.text.toString(), pwd.text.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Destroy", "destroy");
    }


    private fun newExecuteLogin(id: String, pwd: String) {
        var jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("pwd", pwd)

        val registeredUser = 200
        val url = default_url.plus(getString(R.string.inquireUser))

        volley?.postJSONObjectString(jsonObject, url, { response: String ->
            val responseJSONObject = JSONObject(response)

            if(responseJSONObject.get("code") == registeredUser) {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent, null)
                this.finish()
            } else {
                nickNameDialog = NickNameDialog(this)
                nickNameDialog!!.start()
            }

            loginDialog?.finish()
        }, { error: VolleyError? ->
            if (error != null) {
                Log.i("LoginVolley", "fail")
            }
        })
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