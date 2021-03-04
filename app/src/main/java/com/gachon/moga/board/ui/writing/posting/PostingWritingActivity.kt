package com.gachon.moga.board.ui.writing.posting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gachon.moga.R
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.ui.search.SearchActivity
import com.gachon.moga.databinding.ActivityWritingBinding
import com.skydoves.bindables.BindingActivity

class PostingWritingActivity
    : BindingActivity<ActivityWritingBinding>(R.layout.activity_writing) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    companion object {
        private const val boardInfoId = "BoardInfo"

        fun startActivity(context: Context, boardInfo: BoardInfo) {

            if (context is Activity) {
                val intent = Intent(context, PostingWritingActivity::class.java)
                intent.putExtra(boardInfoId, boardInfo)
                context.startActivity(intent)
            }
        }
    }
}