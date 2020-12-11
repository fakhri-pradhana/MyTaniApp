package com.android.mytani.models;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public class Post implements Serializable {

    private String postKey;
    private String title;
    private String description;
    private String category;
    private String picture;
    private String userId;
    private String userPhoto;
    private Object timeStamp;

    public Post() {
        // EMPTY CONST
    }


    public Post(String postKey, String title, String description, String category, String picture, String userId, String userPhoto) {
        this.postKey = postKey;
        this.title = title;
        this.description = description;
        this.category = category;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post(String title, String description, String category, String picture, String userId, String userPhoto) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }
}
