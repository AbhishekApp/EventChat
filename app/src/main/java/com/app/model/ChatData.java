package com.app.model;

/**
 * Created by admin on 8/9/2016.
 */
public class ChatData {


    private String author;
    private String title;
    public ChatData() {}
    public ChatData(String author, String title)
    {
        this.author = author;
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
}
