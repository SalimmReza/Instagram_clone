package com.example.instagram.Model;

public class N_Notification {

    private String user_id, text, postId;

    private boolean is_post;

    public N_Notification() {
    }

    public N_Notification(String user_id, String text, String postId, boolean is_post) {
        this.user_id = user_id;
        this.text = text;
        this.postId = postId;
        this.is_post = is_post;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isIs_post() {
        return is_post;
    }

    public void setIs_post(boolean is_post) {
        this.is_post = is_post;
    }
}
