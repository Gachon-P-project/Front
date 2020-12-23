package com.hfad.gamo

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import io.wiffy.extension.appendEnter
import io.wiffy.extension.isNetworkConnected
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class LoginAsyncTask(
    private val ids: String,
    private val password: String,
    private val mView: LoginActivity
) : SuperContract.SuperAsyncTask<Void, Void, Int>() {

    private lateinit var studentInformation: StudentInformation
    private val strBuilder = StringBuilder()

    override fun onPreExecute() {
        // Component.getBuilder()?.show()
    }

    override fun doInBackground(vararg params: Void?): Int {

        /*if (!isNetworkConnected(mView.baseContext)) {
            return ACTION_NETWORK_FAILURE
        }*/
        val number: String
        try {
            val sendObject = JSONObject().apply {
                put("fsp_cmd", "login")
                put("DVIC_ID", "dwFraM1pVhl6mMn4npgL2dtZw7pZxw2lo2uqpm1yuMs=")
                put("fsp_action", "UserAction")
                put("LOGIN_ID", ids)
                put("USER_ID", ids)
                put("PWD", password)
                put("APPS_ID", "com.sz.Atwee.gachon")
            }

            val checkObject =
                "{\"USER_ID\":\"${ids}\",\"fsp_ds_cmd\":[{\"TYPE\":\"N\",\"SQL_ID\":\"mobile/common:USER_INFO_SQL_S01\",\"INSERT_SQL_ID\":\"\",\"UPDATE_SQL_ID\":\"\",\"DELETE_SQL_ID\":\"\",\"SAVE_FLAG_COLUMN\":\"\",\"KEY_ZERO_LEN\":\"\",\"USE_INPUT\":\"N\",\"USE_ORDER\":\"Y\",\"EXEC\":\"\",\"FAIL\":\"\",\"FAIL_MSG\":\"\",\"EXEC_CNT\":0,\"MSG\":\"\"}],\"fsp_action\":\"xDefaultAction\",\"fsp_cmd\":\"execute\"}";

            var mObject: JSONObject? = null

            try {
                mObject =
                    JSONObject(EntityUtils.toString(DefaultHttpClient().execute(HttpPost("http://smart.gachon.ac.kr:8080//WebJSON").apply {
                        entity = StringEntity(sendObject.toString())
                    }).entity))

                number = mObject.getJSONObject("ds_output").getString("userUniqNo")

            } catch (e: Exception) {
                strBuilder.appendEnter(
                    try {
                        "Login Error : ${
                        mObject?.getString("ErrorMsg")
                        }"
                    } catch (e: Exception) {
                        ""
                    }
                )
                return ACTION_FAILURE
            }

            try {
                JSONObject(EntityUtils.toString(DefaultHttpClient().execute(HttpPost("http://smart.gachon.ac.kr:8080//WebJSON").apply {
                    entity = StringEntity(checkObject)
                }).entity)).getJSONArray("ds_user_info").getJSONObject(0).apply {
                    studentInformation = StudentInformation(
                        getString("KNAME"),
                        number,
                        ids,
                        password,
                        getString("DEPT_NAME"),
                        "http://gcis.gachon.ac.kr/common/picture/haksa/shj/$number.jpg",
                        getString("DEPT_CD")
                    )
                }

            } catch (e: Exception) {

                // strBuilder.appendEnter("Login Error : second error")
                return ACTION_FAILURE
            }



        } catch (e: Exception) {
            strBuilder.appendEnter("functionError : ${e.message}\n")
            return ACTION_FAILURE
        }
        return ACTION_SUCCESS
    }

    override fun onPostExecute(result: Int?) {
        Handler(Looper.getMainLooper()).post {
            Component.getBuilder()?.dismiss()
        }
        when (result ?: ACTION_FAILURE) {
            ACTION_SUCCESS -> {
                mView.saveInformation(studentInformation)
                val nextIntent = Intent(mView, MainActivity::class.java)
                startActivity(mView,nextIntent,null)
                mView.finish();
            }
            ACTION_NETWORK_FAILURE -> {
                mView.networkFailed()
            }
            else -> {
                mView.loginFailed(strBuilder.toString())
            }
        }
    }
}