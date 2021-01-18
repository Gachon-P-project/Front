package com.gachon.moga

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.WindowManager

/*class LoginDialog
constructor(context: Context) : Dialog(context){

    init {
        setCanceledOnTouchOutside(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_login)
    }
}*/

class LoadingDialog {
    private var dlg : Dialog? = null

    fun start(context: Context) {
        dlg = Dialog(context)
        dlg?.setCanceledOnTouchOutside(false)
        dlg?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg?.setContentView(R.layout.dialog_loading)
        dlg?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        Log.i("dialog", "start")
        dlg?.show()
    }


    fun finish() {
        dlg?.dismiss()
    }
}




