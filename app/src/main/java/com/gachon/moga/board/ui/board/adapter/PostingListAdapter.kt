package com.gachon.moga.board.ui.board.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gachon.moga.R
import com.gachon.moga.board.models.ToPosting
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.ui.posting.PostingActivity
import com.gachon.moga.databinding.ItemBoardBinding
import com.skydoves.bindables.binding
import kotlinx.android.synthetic.main.item_board_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class PostingListAdapter : RecyclerView.Adapter<PostingListAdapter.PostingViewHolder>() {

    private val items: MutableList<Posting> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostingViewHolder {
        val binding = parent.binding<ItemBoardBinding>(R.layout.item_board)
        return PostingViewHolder(binding, ToPosting()).apply {
            binding.root.setOnClickListener {
                PostingActivity.startActivity(parent.context, toPosting)
            }
        }
    }

    override fun onBindViewHolder(holder: PostingViewHolder, position: Int) {
        holder.binding.apply {
            posting = items[position]
            executePendingBindings()
        }
        holder.toPosting.boardType = items[position].boardFlag
        holder.toPosting.postNo = items[position].postNo
        holder.toPosting.writerNo = items[position].userNo
        holder.toPosting.professorName = items[position].professorName
        holder.toPosting.subjectName = items[position].subjectName
    }

    override fun getItemCount() = items.size

    fun setPostingList(postingList: List<Posting>) {
        items.clear()
        items.addAll(postingList)
        notifyDataSetChanged()
    }

    class PostingViewHolder(val binding: ItemBoardBinding, val toPosting: ToPosting) :
            RecyclerView.ViewHolder(binding.root)
}
