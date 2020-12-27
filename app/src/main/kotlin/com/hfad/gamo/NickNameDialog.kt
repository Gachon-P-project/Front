package com.hfad.gamo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NickNameDialog(loginActivity: LoginActivity) {
    private val loginActivity = loginActivity;
    private val dlg = Dialog(loginActivity)
    private lateinit var listener : MyDialogOKClickedListener
    private lateinit var nicknameEditText: EditText
    private lateinit var nicknameExistentTextView: TextView
    private lateinit var nicknameRegisterButton: Button
    private var volley: VolleyForHttpMethod? = null
    private val nicknameExistent = 15
    private lateinit var nickname: String
    private lateinit var urlNickNameCheck: String
    private val urlRegisterUser = Component.default_url.plus(loginActivity.getString(R.string.registerUser))



    fun start() {
        volley = VolleyForHttpMethod(Volley.newRequestQueue(loginActivity))

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_nickname)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        nicknameEditText = dlg.findViewById(R.id.nickname_edit_text)
        nicknameExistentTextView = dlg.findViewById(R.id.nickname_existent)
        nicknameRegisterButton = dlg.findViewById(R.id.nickname_register)

        nicknameExistentTextView.visibility = View.INVISIBLE

        nicknameRegisterButton.setOnClickListener {
            nicknameRegisterButton.isClickable = false

            nickname = nicknameEditText.text.toString()
            urlNickNameCheck = Component.default_url.plus(loginActivity.getString(R.string.checkNickName, nickname))

            volley!!.getString(urlNickNameCheck) { response ->
                if(response.length == nicknameExistent) {
                    nicknameExistentTextView.visibility = View.VISIBLE
                    nicknameRegisterButton.isClickable = true
                } else {
                    setSharedItem("nickname", nickname)
                    val userJsonObject = JSONObject()
                    userJsonObject.put("user_no", getSharedItem<String>("number"))
                    userJsonObject.put("user_id", getSharedItem<String>("id"))
                    userJsonObject.put("user_name", getSharedItem<String>("name"))
                    userJsonObject.put("nickname", getSharedItem<String>("nickname"))
                    userJsonObject.put("user_major", getSharedItem<String>("department"))

                    volley!!.postJSONObjectString(userJsonObject,urlRegisterUser, { response ->
                        Log.i("response", response)
                        val nextIntent = Intent(loginActivity, MainActivity::class.java)
                        loginActivity.startActivity(nextIntent, null)
                        dlg.dismiss()
                        loginActivity.finish()
                    }, null)
//                    volley!!.postJSONObjectString(userJsonObject,urlRegisterUser, null)

                }
            }
        }

        dlg.show()
    }


    fun setOnOKClickedListener(listener: (String) -> Unit) {
        this.listener = object: MyDialogOKClickedListener {
            override fun onOKClicked(content: String) {
                listener(content)
            }
        }
    }


    interface MyDialogOKClickedListener {
        fun onOKClicked(content : String)
    }
}