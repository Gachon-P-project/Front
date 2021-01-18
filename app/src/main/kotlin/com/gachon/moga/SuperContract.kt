package com.gachon.moga

import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface SuperContract {

    interface WiffyObject {
        fun console(str: String) = Log.d("asdf", str)
        fun console(tag: String, str: String) = Log.d(tag, str)
        fun toast(context: Context, str: String) =
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()

        fun toast(context: Context, id: Int) =
            Toast.makeText(context, id, Toast.LENGTH_SHORT).show()

        fun toastLong(context: Context, str: String) =
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()

        fun toastLong(context: Context, id: Int) =
            Toast.makeText(context, id, Toast.LENGTH_LONG).show()
    }

    abstract class SuperActivity : AppCompatActivity(), WiffyObject {
        open fun toast(str: String) =
            Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()

        open fun toast(id: Int) = Toast.makeText(applicationContext, id, Toast.LENGTH_SHORT).show()
        open fun toastLong(str: String) =
            Toast.makeText(applicationContext, str, Toast.LENGTH_LONG).show()

        open fun toastLong(id: Int) =
            Toast.makeText(applicationContext, id, Toast.LENGTH_LONG).show()
    }

    abstract class SuperFragment : Fragment(), WiffyObject {
        open fun toast(str: String) = Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
        open fun toast(id: Int) = Toast.makeText(activity, id, Toast.LENGTH_SHORT).show()
        open fun toastLong(str: String) = Toast.makeText(activity, str, Toast.LENGTH_LONG).show()
        open fun toastLong(id: Int) = Toast.makeText(activity, id, Toast.LENGTH_LONG).show()
    }

    abstract class SuperDialog(context: Context, themeId: Int = 0) : Dialog(context, themeId),
        WiffyObject {
        open fun toast(str: String) = Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        open fun toast(id: Int) = Toast.makeText(context, id, Toast.LENGTH_SHORT).show()
        open fun toastLong(str: String) = Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        open fun toastLong(id: Int) = Toast.makeText(context, id, Toast.LENGTH_LONG).show()
    }

    abstract class SuperAsyncTask<A, B, C> : AsyncTask<A, B, C>(), WiffyObject

    interface SuperPresenter : WiffyObject {
        fun initPresent()
    }
}