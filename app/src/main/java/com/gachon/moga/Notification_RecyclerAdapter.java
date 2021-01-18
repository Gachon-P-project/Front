package com.gachon.moga;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.moga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Notification_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private final String TAG = "Notice_Adapter";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private JSONArray dataArray;
    private NotificationFragment fragment;
    private String dept = DataIOKt.getDepartment();
    private int unread = DataIOKt.getUnread();


    public Notification_RecyclerAdapter(JSONArray dataArray, NotificationFragment fragment) {
        super();
        this.dataArray = dataArray;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        JSONObject data;
        final String title;
        final String content;
        String type = null;
//        final String board_no = "1961";             // 임시
        String board_no = null;
        Date date = null;
        final boolean isRead;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timeZone);

        Log.d(TAG, "onBindViewHolder: position : " + position);

        try {
            Log.d(TAG, "onBindViewHolder: position: " + position + ", data: " + dataArray.toString());
            data = dataArray.getJSONObject(dataArray.length() - position - 1) ;
            Log.d(TAG, "onBindViewHolder: data : " + data.toString());
            title = data.getString("title");
            content = data.getString("content");
            if(title.equals("["+dept+"]"))
                type="notice_new";
            board_no = data.getString("board_no");
            date = dateFormat.parse(data.getString("time"));
            String sTime = new SimpleDateFormat("MM-dd HH:mm").format(date);
            isRead = data.getBoolean("isRead");
            ((viewHolder)holder).tvTitle.setText(title);
            ((viewHolder)holder).tvContent.setText(content);
            ((viewHolder)holder).tvTime.setText(sTime);
            androidx.cardview.widget.CardView cardView;
            switch (type) {
                case "notice_new" :
                    ((viewHolder)holder).imgIcon.setImageResource(R.drawable.bottom_notice);
                    break;
                case "board_imply" :
                    break;
                case "board_reply" :
                    break;
                case "board_like" :
                    break;
                case "board_newPost_bookmark" :
                    break;
                default:
                    break;
            }
            if(!isRead) {
                ((viewHolder)holder).cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.jinColor));
            }

            final String finalBoard_no = board_no;
            final String finalType = type;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((viewHolder)holder).cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    if(!isRead)
                        setRead(position, true);
                    Intent intent = null;
                    switch (finalType) {
                        case "notice_new" :
                            intent = new Intent(context, NoticeDetailActivity.class);
                            intent.putExtra("board_no", finalBoard_no);
                            intent.putExtra("dept", dept);
//                            ((NotificationFragment) fragment).switchFragment("notice");
                            ((MainActivity)((NotificationFragment) fragment).getActivity()).replaceFragment("notice");
                            context.startActivity(intent);
                            break;
                        case "board_imply" :
                            break;
                        case "board_reply" :
                            break;
                        case "board_like" :
                            break;
                        case "board_newPost_bookmark" :
                            break;
                    };
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

//        }


    }

    @Override
    public int getItemCount() {
        if(dataArray == null)
            return 0;
        else
            return dataArray.length();
    }


    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();

    }


    private class viewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvContent, tvTime;
        public ImageView imgIcon;
        public androidx.cardview.widget.CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvContent = itemView.findViewById(R.id.tvNotificationContent);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            imgIcon = itemView.findViewById(R.id.imgNotificationIcon);
            cardView = itemView.findViewById(R.id.cardviewNotification);
        }
    }

    public boolean setRead(int position, boolean isRead ) {
        try {
            Log.d(TAG, String.format("setRead: position : %d, isRead : " + isRead, position));
            JSONObject obj = dataArray.getJSONObject((dataArray.length() - position - 1));
            Log.d(TAG, "setRead: before : " + dataArray.toString());
            obj.put("isRead", isRead);
            dataArray.put((dataArray.length() - position - 1), obj);
            this.notifyDataSetChanged();
            Log.d(TAG, "setRead: after : " + dataArray.toString());
            unread = isRead ? (unread-1) : (unread+1);
            fragment.setBadge(unread);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }





}
