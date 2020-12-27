package com.hfad.gamo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.MainThread

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
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private val TAG: String? = "LoginActivity"

    private val loadingDialog: LoadingDialog? = LoadingDialog()

    private var volley: VolleyForHttpMethod? = null
    private var nickNameDialog: NickNameDialog?= null
    private lateinit var studentInformation: StudentInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        default_url = "http://172.30.1.2:17394"

        volley = VolleyForHttpMethod(Volley.newRequestQueue(this))
        sharedPreferences = getSharedPreferences(appConstantPreferences, Context.MODE_PRIVATE)
        var edtPassword = findViewById<EditText>(R.id.pwd);
        
//        val displayRectangle = Rect()
//        val window: Window = getWindow()
//        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        



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
            try {
                val responseJSONObject = JSONObject(response)

                var data: JSONObject = responseJSONObject.get("data") as JSONObject

                if (responseJSONObject.get("code") == registeredUser) {
                    setSharedItem("nickname", data.get("nickname"))
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent, null)
                    this.finish()
                } else {

                    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val display = windowManager.defaultDisplay
                    val size = Point()
                    display.getSize(size)

                    nickNameDialog = NickNameDialog(this)
                    nickNameDialog?.initDialog(size.x)
                    nickNameDialog!!.start()
                }

                studentInformation = StudentInformation(
                        data.get("user_name").toString(),
                        data.get("user_no").toString(),
                        data.get("user_id").toString(),
                        "pwd",
                        data.get("user_major").toString(),
                        "http://gcis.gachon.ac.kr/common/picture/haksa/shj/" + data.get("user_no") + ".jpg",
                        ""
                )
                saveInformation(studentInformation)

            } catch (e: Exception) {
                e?.stackTrace

            }

            loadingDialog?.finish()
        }, { error: VolleyError? ->
            if (error != null) {
//                Log.i("LoginVolley", "fail")
                Log.i(TAG, "newExecuteLogin: " + error)
                loadingDialog?.finish()
                Toast.makeText(this, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
        })
    }



//    private fun executeLogin(id: String, password: String) {
//        LoginAsyncTask(id, password, this).execute()
//    }


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