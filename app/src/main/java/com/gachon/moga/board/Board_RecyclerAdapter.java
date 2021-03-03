package com.gachon.moga.board;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.moga.R;
import com.gachon.moga.board.models.ToPosting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Board_RecyclerAdapter extends RecyclerView.Adapter<Board_RecyclerAdapter.ViewHolder> {

    private JSONArray JSONArrayData = null;
    private final String board_title;
    private final int boardType;
    private Integer pageNum;


    Board_RecyclerAdapter(JSONArray list, String board_title, int boardType) {
        this.JSONArrayData = list;
        this.board_title = board_title;
        this.boardType = boardType;
    }

    Board_RecyclerAdapter(JSONArray list, String board_title, int boardType, Integer pageNum) {
        this.JSONArrayData = list;
        this.board_title = board_title;
        this.boardType = boardType;
        this.pageNum = pageNum;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView board_title;
        TextView board_contents;
        TextView board_date;
        TextView board_post_like;
        TextView board_reply_cnt;
        TextView nickname;
        View view;
        ToPosting toPosting;


        ViewHolder(View itemView) {
            super(itemView);

            board_title = itemView.findViewById(R.id.item_board_board_title);
            board_contents = itemView.findViewById(R.id.item_board_board_contents);
            board_date = itemView.findViewById(R.id.item_board_board_date);
            board_post_like = itemView.findViewById(R.id.item_board_like_cnt);
            board_reply_cnt = itemView.findViewById(R.id.item_board_reply_cnt);
            nickname = itemView.findViewById(R.id.item_board_nickname);
            view = itemView;
        }
    }


    @NonNull
    @Override
    public Board_RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_board, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Board_RecyclerAdapter.ViewHolder holder, int position) {

        JSONObject data = null;
        String postTitle = null;
        String postContents = null;
        String wrtDate = null;
        String replyCnt = null;
        String likeCnt = null;
        String nickname = null;
        int postNo = -1;
        String likeUser = null;
        int userNo = -1;
        String professorName = null;
        String subjectName = null;
        int boardType = -1;


        try {
            data = JSONArrayData.getJSONObject(position);
            postTitle = data.getString("post_title");
            postContents = data.getString("post_contents");
            wrtDate = data.getString("wrt_date");
            replyCnt = data.getString("reply_cnt");
            likeCnt = data.getString("like_cnt");
            nickname = data.getString("nickname");
            postNo = data.getInt("post_no");
            likeUser = data.getString("like_user");
            userNo = data.getInt("user_no");
            boardType = data.getInt("board_flag");
            professorName = data.getString("professor_name");
            subjectName = data.getString("subject_name");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        String androidDate = DateProcess.processServerDateToAndroidDate(wrtDate);

        holder.board_title.setText(postTitle);
        holder.board_contents.setText(postContents);
        holder.board_date.setText(androidDate);
        holder.board_reply_cnt.setText(replyCnt);
        holder.board_post_like.setText(likeCnt);
        holder.nickname.setText(nickname);

        holder.toPosting = new ToPosting(boardType, postNo, userNo, pageNum, subjectName, professorName);

        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), PostingActivity.class);
            intent.putExtra("toPosting", holder.toPosting);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return JSONArrayData.length();
    }


}
