package com.hfad.gamo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NickNameDialog(baseLoginActivity: Activity) {
    private val TAG = "NickNameDialog"
    private val baseActivity = baseLoginActivity;
    private val dlg = Dialog(baseLoginActivity)
    private lateinit var listener : MyDialogOKClickedListener
    private lateinit var nicknameEditText: EditText
    private lateinit var nicknameExistentTextView: TextView
    private lateinit var nicknameRegisterButton: Button
    private var volley: VolleyForHttpMethod? = null
    private val nicknameExistent = 15
    private lateinit var nickname: String
    private lateinit var urlNickNameCheck: String
    private val urlRegisterUser = Component.default_url.plus(baseLoginActivity.getString(R.string.registerUser))
    private val urlNicknameUpdate = Component.default_url.plus(baseActivity.getString(R.string.nicknameUpdate))


    fun start() {
//        volley = VolleyForHttpMethod(Volley.newRequestQueue(baseActivity))
//
//        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
//        dlg.setContentView(R.layout.dialog_nickname2)     //다이얼로그에 사용할 xml 파일을 불러옴
//        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
//
//        nicknameEditText = dlg.findViewById(R.id.nickname_edit_text)
//        nicknameExistentTextView = dlg.findViewById(R.id.nickname_existent)
//        nicknameRegisterButton = dlg.findViewById(R.id.nickname_register)
//
//        nicknameExistentTextView.visibility = View.INVISIBLE

        nicknameRegisterButton.setOnClickListener {
            nicknameRegisterButton.isClickable = false

            nickname = nicknameEditText.text.toString()
            nickname = nickname.replace(" ", "")
            urlNickNameCheck = Component.default_url.plus(baseActivity.getString(R.string.checkNickName, nickname))

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
                        val nextIntent = Intent(baseActivity, MainActivity::class.java)
                        baseActivity.startActivity(nextIntent, null)
                        dlg.dismiss()
                        baseActivity.finish()
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
    
    fun initDialog(deviceWidth: Int) {
        volley = VolleyForHttpMethod(Volley.newRequestQueue(baseActivity))

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_nickname)        //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)
        dlg.setCanceledOnTouchOutside(false)                //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        nicknameEditText = dlg.findViewById(R.id.nickname_edit_text)
        nicknameExistentTextView = dlg.findViewById(R.id.nickname_existent)
        nicknameRegisterButton = dlg.findViewById(R.id.nickname_register)
        nicknameExistentTextView.visibility = View.INVISIBLE

        val params: ViewGroup.LayoutParams? = dlg?.window?.attributes
//        params?.width = (deviceWidth * 0.9).toInt()
        params?.width = (deviceWidth * 0.95).toInt()
        dlg?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun updateNickname() {
        var previousNickname = getSharedItem<String>("nickname")
        nicknameEditText.setText(previousNickname)
        nicknameEditText.hint = previousNickname
        nicknameRegisterButton.text = "닉네임 변경"
        dlg.setCancelable(true)

        nicknameRegisterButton.setOnClickListener {
            nicknameRegisterButton.isClickable = false
            nickname = nicknameEditText.text.toString()
            nickname = nickname.replace(" ", "")
            if(nickname == "" || nickname == previousNickname)
                dlg.dismiss()
            else {
                urlNickNameCheck = Component.default_url.plus(baseActivity.getString(R.string.checkNickName, nickname))

                volley!!.getString(urlNickNameCheck) { response ->
                    if(response.length == nicknameExistent) {
                        nicknameExistentTextView.visibility = View.VISIBLE
                        nicknameRegisterButton.isClickable = true
                    } else {
                        val userJsonObject = JSONObject()
                        userJsonObject.put("nickname", nickname)
                        userJsonObject.put("user_no", getSharedItem<String>("number"))

                        volley!!.putJSONObject(userJsonObject, urlNicknameUpdate, { response: String ->
                            Log.i(TAG, "updateNickname: response : $response")
                            setSharedItem("nickname", nickname)
                            (baseActivity as MainActivity)?.refreshFragment();
                            dlg.dismiss()
                        }, { error: VolleyError? ->
                            if (error != null) {
                                nicknameRegisterButton.isClickable = true
                                Log.i(TAG, "updateNickname: $error")
                                Toast.makeText(baseActivity, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }
        dlg.show()
    }
}