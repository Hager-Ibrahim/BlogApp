package com.example.blog.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {


    private String title ;
    private String description ;
    private String imageUri ;
    @ServerTimestamp
    private Date timestamp;
    private String id ;
    private String name ;
    private String userImg;

    public Post(){

    }

    public Post(String title, String description, String imageUri , Date timestamp , String id , String name , String userImg) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.timestamp = timestamp ;
        this.id = id ;
        this.name = name ;
        this.userImg = userImg ;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserImg() {
        return userImg;
    }
}
