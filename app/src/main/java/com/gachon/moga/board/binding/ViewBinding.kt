package com.gachon.moga.board.binding

import android.app.Activity
import android.app.Application
import android.app.Service
import android.app.backup.BackupAgent
import android.content.Context
import android.content.ContextWrapper
import android.content.MutableContextWrapper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.databinding.BindingAdapter
import com.gachon.moga.board.DateProcess
import com.google.android.material.internal.ContextUtils.getActivity

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
    @BindingAdapter("onBackPressed")
    fun bindOnBackPressed(view: View, onBackPress: Boolean) {
        val context = view.context

        if (onBackPress && context is ContextWrapper) {
            view.setOnClickListener {
                (context.baseContext as Activity).onBackPressed()
            }
        }
    }
}