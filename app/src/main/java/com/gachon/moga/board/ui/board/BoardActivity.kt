package com.gachon.moga.board.ui.board

import android.app.Activity
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
import javax.inject.Inject

@AndroidEntryPoint
class BoardActivity : BindingActivity<ActivityBoardBinding>(R.layout.activity_board) {

    @Inject
    lateinit var boardViewModelFactory: BoardViewModel.AssistedFactory

    private val viewModel: BoardViewModel by viewModels {
        BoardViewModel.provideFactory(boardViewModelFactory, boardInfo)
    }

    private val boardInfo = BoardInfo("자유게시판", null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding {
            lifecycleOwner = this@BoardActivity
            adapter = PostingListAdapter()
            vm = viewModel
        }
    }

    companion object {
        private const val boardInfoId = "BoardInfo"

        fun startActivity(context: Context, boardInfo: BoardInfo) {
            if(context is Activity) {
                val intent = Intent(context, BoardActivity::class.java)
                intent.putExtra(boardInfoId, boardInfo)
                context.startActivity(intent)
            }
        }
    }


}
