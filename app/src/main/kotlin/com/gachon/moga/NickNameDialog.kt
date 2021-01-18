package com.gachon.moga

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
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
    private lateinit var nicknameActionButton: Button
    private lateinit var btnNicknameUpdate: Button
    private lateinit var btnCancel: Button
    private var volley: VolleyForHttpMethod? = null
    private val nicknameExistent = 15
    private lateinit var nickname: String
    private lateinit var urlNickNameCheck: String
    private val urlRegisterUser = Component.default_url.plus(baseLoginActivity.getString(R.string.registerUser))
    private val urlNicknameUpdate = Component.default_url.plus(baseActivity.getString(R.string.updateNickname))


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

        btnNicknameUpdate.visibility = View.GONE
        btnCancel.visibility = View.GONE
        nicknameActionButton.visibility = View.VISIBLE

        nicknameActionButton.setOnClickListener {
            nicknameActionButton.isClickable = false

            nickname = nicknameEditText.text.toString()
            nickname = nickname.replace(" ", "")
            urlNickNameCheck = Component.default_url.plus(baseActivity.getString(R.string.checkNickName, nickname))

            volley!!.getString(urlNickNameCheck) { response ->
                if(response.length == nicknameExistent) {
                    nicknameExistentTextView.visibility = View.VISIBLE
                    nicknameActionButton.isClickable = true
                } else {
                    setSharedItem("nickname", nickname)
                    val userJsonObject = JSONObject()
                    userJsonObject.put("user_no", getSharedItem<String>("number"))
                    userJsonObject.put("user_id", getSharedItem<String>("id"))
                    userJsonObject.put("user_name", getSharedItem<String>("name"))
                    userJsonObject.put("nickname", getSharedItem<String>("nickname"))
                    userJsonObject.put("user_major", getSharedItem<String>("department"))

                    volley!!.postJSONObjectString(userJsonObject, urlRegisterUser, { response ->
                        Log.i("response", response)
                        val nextIntent = Intent(baseActivity, StartGuideActivity::class.java)
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
        fun onOKClicked(content: String)
    }
    
    fun initDialog(deviceWidth: Int) {
        volley = VolleyForHttpMethod(Volley.newRequestQueue(baseActivity))

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_nickname)        //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)
        dlg.setCanceledOnTouchOutside(false)                //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        nicknameEditText = dlg.findViewById(R.id.nickname_edit_text)
        nicknameExistentTextView = dlg.findViewById(R.id.nickname_existent)
        nicknameActionButton = dlg.findViewById(R.id.nickname_register)
        btnNicknameUpdate = dlg.findViewById(R.id.button_dialog_nickname_update)
        btnCancel = dlg.findViewById(R.id.button_dialog_nickname_cancel)
        nicknameExistentTextView.visibility = View.INVISIBLE


//        다이얼로그 창 크기 조절
        val params: ViewGroup.LayoutParams? = dlg?.window?.attributes
        params?.width = (deviceWidth * 0.80).toInt()
        dlg?.window?.attributes = params as WindowManager.LayoutParams
        dlg?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        nicknameEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            var handled = false
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == EditorInfo.IME_ACTION_DONE) {
                nicknameActionButton.performClick()
                handled = true
            }
            handled
        })


    }

    fun updateNickname() {
        var previousNickname = getSharedItem<String>("nickname")
        nicknameEditText.setText(previousNickname)
        nicknameEditText.hint = previousNickname
//        nicknameActionButton.text = "닉네임 변경"
        btnNicknameUpdate.visibility = View.VISIBLE
        btnCancel.visibility = View.VISIBLE
        nicknameActionButton.visibility = View.GONE
        dlg.setCancelable(true)

        btnNicknameUpdate.setOnClickListener {
            nicknameActionButton.isClickable = false
            nickname = nicknameEditText.text.toString()
            nickname = nickname.replace(" ", "")
            if(nickname == "" || nickname == previousNickname)
                dlg.dismiss()
            else {
                urlNickNameCheck = Component.default_url.plus(baseActivity.getString(R.string.checkNickName, nickname))

                volley!!.getString(urlNickNameCheck) { response ->
                    if(response.length == nicknameExistent) {
                        nicknameExistentTextView.visibility = View.VISIBLE
                        nicknameActionButton.isClickable = true
                    } else {
                        val userJsonObject = JSONObject()
                        userJsonObject.put("nickname", nickname)
                        userJsonObject.put("user_no", Integer.valueOf(getSharedItem<String>("number")))

                        volley!!.putJSONObjectString(userJsonObject, urlNicknameUpdate, { response: String ->
                            Log.i(TAG, "updateNickname: response : $response")
                            setSharedItem("nickname", nickname)
                            (baseActivity as MainActivity)?.refreshFragment();
                            dlg.dismiss()
                        }, { error: VolleyError? ->
                            if (error != null) {
                                nicknameActionButton.isClickable = true
                                Log.i(TAG, "updateNickname: $error")
                                Toast.makeText(baseActivity, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}