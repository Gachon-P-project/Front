package com.hfad.gamo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
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

    private val TAG: String? = "LoginActivity"

    private val loadingDialog: LoadingDialog? = LoadingDialog()

    private var volley: VolleyForHttpMethod? = null
    private var nickNameDialog: NickNameDialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        volley = VolleyForHttpMethod(Volley.newRequestQueue(this))
        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        var edtPassword = findViewById<EditText>(R.id.pwd);
//        Component.default_url = "http://172.30.1.2:17394"

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

        login_button.focusable = View.NOT_FOCUSABLE
        login_button.setOnClickListener() {
            loadingDialog?.start(this)
            newExecuteLogin(id.text.toString(), pwd.text.toString())
        }

        edtPassword.imeOptions = EditorInfo.IME_ACTION_DONE;
        edtPassword.setOnEditorActionListener{v, keyCode, event ->
            var handled = false
                if(keyCode == EditorInfo.IME_ACTION_DONE || keyCode == KEYCODE_ENTER) {
                    login_button.performClick()
                    handled = true
                }
            handled
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.finish()
        Log.i("Destroy","destroy");
    }



    private fun newExecuteLogin(id: String, pwd: String) {
        var jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("pwd", pwd)

        val registeredUser = 200
        val url = default_url.plus(getString(R.string.inquireUser))


        volley?.postJSONObjectString(jsonObject, url, { response: String ->
            val responseJSONObject = JSONObject(response)

            if (responseJSONObject.get("code") == registeredUser) {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent, null)
                this.finish()
            } else {
                nickNameDialog = NickNameDialog(this)
                nickNameDialog!!.start()
            }

//            sharedPreferences에 데이터 넣는 작업 필요

            loadingDialog?.finish()
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