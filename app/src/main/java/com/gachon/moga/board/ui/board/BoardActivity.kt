package com.gachon.moga.board.ui.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.skydoves.bindables.BindingActivity
import androidx.activity.viewModels
import com.gachon.moga.R
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.ui.board.adapter.PostingListAdapter
import com.gachon.moga.databinding.ActivityBoardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardActivity : BindingActivity<ActivityBoardBinding>(R.layout.activity_board) {

    private val viewModel: BoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding {
            lifecycleOwner = this@BoardActivity
            adapter = PostingListAdapter()
            vm = viewModel
        }
        val boardInfo = BoardInfo(null,null, 1)
        viewModel.fetchPostings(boardInfo)
    }

    fun initToolbar() {
        setSupportActionBar(binding.activityBoardToolbar)
        binding.activityBoardToolbarTitle.text = "board_title"
    }

    companion object {
        fun startActivity(context: Context, boardInfo: BoardInfo) {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardInfo", boardInfo)
            context.startActivity(intent)
        }
    }


}
