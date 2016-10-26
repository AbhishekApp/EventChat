package com.app.model;

import appy.com.wazznowapp.MyApp;

/**
 * Created by admin on 8/9/2016.
 */
public class ChatData {


    private String author;
    private String title;
    private String toUser;

    public ChatData() {}
    /*  public ChatData(String author, String title)
   {
       this(author, title, " ");
   }*/
    public ChatData(String author, String title, String toUser)
    {
        this.author = author;
        this.title = title;
        this.toUser = toUser;
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
}
