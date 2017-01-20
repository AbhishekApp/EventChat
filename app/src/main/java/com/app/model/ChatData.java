package com.app.model;

import appy.com.wazznowapp.MyApp;

/**
 * Created by admin on 8/9/2016.
 */
public class ChatData {


    private String author;
    private String title;
    private String toUser;
    private String timestamp;


    public ChatData() {}
    /*  public ChatData(String author, String title)
   {
       this(author, title, " ");
   }*/
    public ChatData(String author, String title, String toUser, String timestamp)
    {
        this.author = author;
        this.title = title;
        this.toUser = toUser;
        this.timestamp = timestamp;
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

}
