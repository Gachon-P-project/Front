package com.gachon.moga.board;

import android.os.Parcel;
import android.os.Parcelable;

/*public class ReplyItem {
    private int is_mine;

    private int post_no;
    private String user_id;
    private String wrt_data;
    private String contents;

    public int getIs_mine() {
        return is_mine;
    }

    public void setIs_mine(int is_mine) {
        this.is_mine = is_mine;
    }

    public int getPost_no() {
        return post_no;
    }

    public void setPost_no(int post_no) {
        this.post_no = post_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWrt_data() {
        return wrt_data;
    }

    public void setWrt_data(String wrt_data) {
        this.wrt_data = wrt_data;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}*/

public class ReplyItem implements Parcelable {

    private String content;
    private String wrt_date;

    public ReplyItem(String content, String wrt_date) {
        this.content = content;
        this.wrt_date = wrt_date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWrt_date(String wrt_date) {
        this.wrt_date = wrt_date;
    }

    public String getContent() {
        return content;
    }

    public String getWrt_date() {
        return wrt_date;
    }


    protected ReplyItem(Parcel in) {
        this.content = in.readString();
        this.wrt_date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(wrt_date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReplyItem> CREATOR = new Creator<ReplyItem>() {
        @Override
        public ReplyItem createFromParcel(Parcel in) {
            return new ReplyItem(in);
        }

        @Override
        public ReplyItem[] newArray(int size) {
            return null;
        }
    };
}
