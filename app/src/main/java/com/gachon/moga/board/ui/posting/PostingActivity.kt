package com.gachon.moga.board.ui.posting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.gachon.moga.R
import com.gachon.moga.board.models.BoardInfo
import com.gachon.moga.board.models.ToPosting
import com.gachon.moga.board.ui.board.BoardViewModel
import com.gachon.moga.board.ui.posting.adapter.ReplyListAdapter
import com.gachon.moga.databinding.ActivityPostingBinding
import com.skydoves.bindables.BindingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KProperty

@AndroidEntryPoint
class PostingActivity : BindingActivity<ActivityPostingBinding>(R.layout.activity_posting) {

    @Inject
    lateinit var postingViewModelFactory: PostingViewModel.AssistedFactory

    private val viewModel: PostingViewModel by viewModels {
        PostingViewModel.provideFactory(postingViewModelFactory, toPosting)
    }

    private val toPosting: ToPosting by lazy {
        intent.getParcelableExtra(toPostingId)!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding {
            lifecycleOwner = this@PostingActivity
            adapter = ReplyListAdapter()
            vm = viewModel
        }
    }


    companion object {
        private const val toPostingId = "ToPosting"

        fun startActivity(context: Context, toPosting: ToPosting) {
            val intent = Intent(context, PostingActivity::class.java)
            intent.putExtra(toPostingId, toPosting)
            context.startActivity(intent)
        }
    }
}

