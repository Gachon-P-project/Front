package com.gachon.moga.board.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gachon.moga.R
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.databinding.ActivitySearchBinding
import com.skydoves.bindables.BindingActivity

class SearchActivity
    : BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    companion object {
        private const val boardInfoId = "BoardInfo"

        fun startActivity(context: Context, boardInfo: BoardInfo) {

            if (context is Activity) {
                val intent = Intent(context, SearchActivity::class.java)
                intent.putExtra(boardInfoId, boardInfo)
                context.startActivity(intent)
            }
        }
    }

}