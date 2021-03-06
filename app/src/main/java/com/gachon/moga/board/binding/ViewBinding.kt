package com.gachon.moga.board.binding

import android.app.Activity
import android.content.ContextWrapper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gachon.moga.R
import com.gachon.moga.board.DateProcess
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.ui.board.BoardViewModel
import com.gachon.moga.board.ui.search.SearchActivity
import com.gachon.moga.board.ui.writing.posting.PostingWritingActivity

object ViewBinding {

    @JvmStatic
    @BindingAdapter("wrtDate")
    fun bindWrtDate(view: TextView, wrtDate: String?) {
        if(wrtDate == null) return

        if(wrtDate == "isDeleted")
            view.text = "방금"
        else {
            val androidDate = DateProcess.processServerDateToAndroidDate(wrtDate)
            view.text = androidDate
        }
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

    @JvmStatic
    @BindingAdapter("onClick", "intent")
    fun bindOnClick(view: View, onClick: Boolean, boardInfo: BoardInfo) {
        val context = view.context

        if (onClick && context is ContextWrapper) {
            val activity = context.baseContext as Activity
            view.setOnClickListener {
                when (view.id) {
                    R.id.activity_board_search
                    -> SearchActivity.startActivity(activity, boardInfo)
                    R.id.activity_board_writing
                    -> PostingWritingActivity.startActivity(activity, boardInfo)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("onRefresh")
    fun bindOnRefresh(view: SwipeRefreshLayout, viewModel: BoardViewModel) {
        view.setOnRefreshListener {
            viewModel.fetchPostings()
        }
    }

    @JvmStatic
    @BindingAdapter("isRefreshing")
    fun bindIsRefreshing(view: SwipeRefreshLayout, isLoading: Boolean) {
        if(!isLoading)
            view.isRefreshing = isLoading
    }

    @JvmStatic
    @BindingAdapter("whenDeleted")
    fun bindWhenDeleted(view: View, isDeleted: Boolean) {
        if(!isDeleted) return
        view.isEnabled = false

        if(view.id == R.id.img_btn_posting_toolbar_three_dots) {
            bindGone(view, true)
        }
    }

    /*@JvmStatic
    @BindingAdapter("setMenu")
    fun bindSetMenu(view: View, shouldBeSetMenu: Boolean) {
        val context = view.context
        if(shouldBeSetMenu && context is ContextWrapper) {
            val activity = context.baseContext as Activity
            activity.menuInflater.inflate()
        }

    }*/

}