package com.app.model;

/**
 * Created by admin on 8/9/2016.
 */
public class ChatData {

    private String author="";
    private String title="";
    private String toUser="";
    private String timestamp="";
    private String authorType="";
    private String messageType="";

    public ChatData() {}

    public ChatData(String author, String title, String toUser, String timestamp, String authorType, String messageType)
    {
        this.author = author;
        this.title = title;
        this.toUser = toUser;
        this.timestamp = timestamp;
        this.authorType = authorType;
        this.messageType = messageType;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
    public String getToUser(){
        return toUser;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getAuthorType() {
        if (authorType!=null){
            return authorType;
        }else{
            return "";
        }

    }
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

}
