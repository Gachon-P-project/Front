package com.gachon.moga.board.ui.board

import android.os.Bundle
import com.skydoves.bindables.BindingActivity
import androidx.activity.viewModels
import com.gachon.moga.R
import com.gachon.moga.board.ui.board.adapter.PostingListAdapter
import com.gachon.moga.databinding.ActivityBoardBinding

class BoardActivity : BindingActivity<ActivityBoardBinding>(R.layout.activity_board) {

    private val viewModel: BoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding {
            lifecycleOwner = this@BoardActivity
            adapter = PostingListAdapter()
            vm = viewModel
        }
    }



}