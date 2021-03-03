package com.gachon.moga.board.binding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gachon.moga.board.DateProcess

object ViewBinding {

    @JvmStatic
    @BindingAdapter("wrtDate")
    fun bindWrtDate(view: TextView, wrtDate: String?) {
        val androidDate = DateProcess.processServerDateToAndroidDate(wrtDate)
        view.text = androidDate
    }

    @JvmStatic
    @BindingAdapter("gone")
    fun bindGone(view: View, shouldBeGone: Boolean) {
        view.visibility = if (shouldBeGone) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("text")
    fun bindGone(view: TextView, text: String?) {
        view.text = text ?: ""
    }
}