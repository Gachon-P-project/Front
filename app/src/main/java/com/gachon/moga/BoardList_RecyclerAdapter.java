package com.gachon.moga;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.moga.board.models.BoardInfo;
import com.gachon.moga.board.ui.board.BoardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BoardList_RecyclerAdapter extends RecyclerView.Adapter<BoardList_RecyclerAdapter.ViewHolder> {

    private ArrayList<String> data;
    private JSONObject subject_professorJSONObject;
    private int boardType;

    BoardList_RecyclerAdapter(ArrayList<String> data, JSONObject jsonObject) {
        this.data = data;
        this.subject_professorJSONObject = jsonObject;
    }

    BoardList_RecyclerAdapter(ArrayList<String> data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        View view;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.board_list_title);
            view = itemView;
        }
    }

    @NonNull
    @Override
    public BoardList_RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_board_list, parent, false);
        BoardList_RecyclerAdapter.ViewHolder vh = new BoardList_RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final BoardList_RecyclerAdapter.ViewHolder holder, int position) {

        holder.title.setText(data.get(position));
        holder.view.setOnClickListener(v ->
                postIntent(v.getContext(), holder.title.getText().toString())
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void postIntent(Context context, String title) {

        String professor = null;

        if(subject_professorJSONObject != null) {
            try {
                professor = subject_professorJSONObject.get(title).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        BoardInfo boardInfo = new BoardInfo(title, professor, boardType);

        /*final Intent intent = new Intent(context, BoardActivity.class);
        intent.putExtra("BoardInfo", boardInfo);
        context.startActivity(intent);*/
        BoardActivity.Companion.startActivity(context,boardInfo);
    }

    public void setBoardType(int boardType) {
        this.boardType = boardType;
    }
}
