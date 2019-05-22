package com.example.blog.Models;

import java.util.Date;

public class Comment {

    private String comment ;
    private Date timestamp ;
    private String name ;
    private String userImg ;


    public Comment(){}

    public Comment(String comment, Date timestamp, String name, String userImg) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.name = name;
        this.userImg = userImg;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getComment() {
        return comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getUserImg() {
        return userImg;
    }
}
