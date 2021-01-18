package com.gachon.moga;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notice_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private JSONArray JSONArrayData = null;
    private String dept;
    private OnLoadMoreListener onLoadMoreListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 1;
    private Context context;

    private final String TAG = "Notice_Adapter";

    Notice_RecyclerAdapter(JSONArray list, String dept) {
        this.JSONArrayData = list;
        this.dept = dept;

    }

    @Override
    public int getItemViewType(int position) {
        try {
            int result = JSONArrayData.getJSONObject(position).getString("title").equals("loading..") ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setRecyclerView (RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                Log.d(TAG, "onScrolled: totalItemCount : " + totalItemCount + ", lastVisibleItem : " + lastVisibleItem);
                Log.d("ADAPTER ::", "isLoading : " + isLoading);
                if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    Log.d("ADAPTER ::", "SCROLLED");
                    if(onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                        Log.d("ADAPTER ::", "onLOADMORE has finished");
                    }
                    isLoading = true;
                }
            }
        });
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

//        뷰타입이 로딩이면 로딩 뷰 보여줌
        if(viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notice, parent, false);
            return new NoticeViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_board_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof NoticeViewHolder) {

            JSONObject data;
            String title = null;
            String date = null;
            String view = null;
            String board_no = null;
            int num = -1;
            int file = -1;
            boolean isNew = false;
            Date now = new Date();
            Date inputDate = now;
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                data = JSONArrayData.getJSONObject(position);
                title = data.getString("title");
                date = data.getString("date");
                view = data.getString("view");
                board_no = data.getString("board_no");
                num = data.getInt("num");
                file = data.getInt("file");

                final String board_noForIntent = board_no;

                inputDate = sf.parse(date);
                now = new Date();
//                글 작성일이 3일 이내면 new 표시
                if (now.getTime() - inputDate.getTime() < (1000 * 60 * 60 * 24 * 3))
                    isNew = true;
                ((NoticeViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), NoticeDetailActivity.class);
                        intent.putExtra("dept", dept);
                        intent.putExtra("board_no", board_noForIntent);
                        v.getContext().startActivity(intent);

                    }
                });

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

            ((NoticeViewHolder) holder).tvTitle.setText(title);
            ((NoticeViewHolder) holder).tvDate.setText(sf.format(inputDate));
            ((NoticeViewHolder) holder).tvCnt.setText(view);
            Log.d(TAG, "onBindViewHolder: title : " + title);


            if (isNew)
                ((NoticeViewHolder) holder).imgIsNew.setVisibility(View.VISIBLE);
            else
                ((NoticeViewHolder) holder).imgIsNew.setVisibility(View.INVISIBLE);


            if (file == 0)
                ((NoticeViewHolder) holder).imgIsFile.setVisibility(View.INVISIBLE);
            else
                ((NoticeViewHolder) holder).imgIsFile.setVisibility(View.VISIBLE);

            if (num == 0) {
                Log.d("ADAPTER" , "YELLOW BACKGROUND CARD :: " + title);
//                ((NotiViewHolder) holder).cardView.setCardBackgroundColor(((NotiViewHolder) holder).itemView.getContext().getResources().getColor(R.color.jinColor, null));
                ((NoticeViewHolder) holder).cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.jinColor));
            } else {
                ((NoticeViewHolder) holder).cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        } else {
            Log.d("INPUT DATA TO CARD ::", "LOADING");

        }
    }


    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }

    public void setIsLoading(boolean bool) {
        this.isLoading = bool;
    }
    public boolean getIsLoading() {
        return this.isLoading;
    }

    private class NoticeViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDate, tvCnt, tvBoardNo;
        public ImageView imgIsNew, imgIsFile;
        public androidx.cardview.widget.CardView cardView;

        public NoticeViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.notice_title);
            tvDate = view.findViewById(R.id.notice_date);
            tvCnt = view.findViewById(R.id.notice_view_cnt);
            imgIsNew = view.findViewById(R.id.notice_new);
            imgIsFile = view.findViewById(R.id.notice_file);
            cardView = view.findViewById(R.id.notice_card_view);


        }

    }


    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar)view.findViewById(R.id.progress_boardList);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void dataUpdate() {
        try {
            this.notifyDataSetChanged();
            Log.d(TAG, "dataUpdate: Success");
        } catch (Exception e) {
            Log.d(TAG, "dataUpdate: Exception occured!!");
            e.printStackTrace();
        }
    }


}
