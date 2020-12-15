package com.hfad.gamo.ClickedBoard;

public class CommentItem {
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
}
