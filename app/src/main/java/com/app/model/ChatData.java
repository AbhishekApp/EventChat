package com.app.model;

/**
 * Created by admin on 8/9/2016.
 */
public class ChatData {


    private String author;
    private String title;
    private String toUser;
    private String timestamp;
    private String authorType;


    public ChatData() {}
    /*  public ChatData(String author, String title)
   {
       this(author, title, " ");
   }*/
    public ChatData(String author, String title, String toUser, String timestamp, String authorType)
    {
        this.author = author;
        this.title = title;
        this.toUser = toUser;
        this.timestamp = timestamp;
        this.authorType = authorType;
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
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

}
