package com.hfad.gamo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.gamo.ClickedBoard.ClickedBoardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Board_RecyclerAdapter extends RecyclerView.Adapter<Board_RecyclerAdapter.ViewHolder> {

    private ArrayList<String> data;
    private JSONObject subject_professorJSONObject;

    Board_RecyclerAdapter(ArrayList<String> data, JSONObject jsonObject) {
        this.data = data;
        this.subject_professorJSONObject = jsonObject;
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
    public Board_RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_board_list, parent, false);
        Board_RecyclerAdapter.ViewHolder vh = new Board_RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final Board_RecyclerAdapter.ViewHolder holder, int position) {

        holder.title.setText(data.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        postIntent(v.getContext(), holder.title.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void postIntent(Context context, String title) {

        final Intent intent = new Intent(context, ClickedBoardActivity.class);
        intent.putExtra("title", title);
        try {
            intent.putExtra("professor", subject_professorJSONObject.get(title).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context.startActivity(intent);
    }
}
