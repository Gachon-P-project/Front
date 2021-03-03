package com.gachon.moga.board.binding

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gachon.moga.board.models.network.board.Posting
import com.gachon.moga.board.ui.board.adapter.PostingListAdapter
import com.skydoves.whatif.whatIfNotNullAs
import com.skydoves.whatif.whatIfNotNullOrEmpty

object RecyclerViewBinding {

  @JvmStatic
  @BindingAdapter("adapter")
  fun bindAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter
  }

  @JvmStatic
  @BindingAdapter("adapterPostingList")
  fun bindAdapterPostingList(view: RecyclerView, PostingList: List<Posting>?) {
    Log.d("adapter", PostingList?.size.toString())
    PostingList.whatIfNotNullOrEmpty { itemList ->
      view.adapter.whatIfNotNullAs<PostingListAdapter> { adapter ->
        adapter.setPostingList(itemList)
      }
    }
  }

}