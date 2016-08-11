package com.app.model;

/**
 * Created by admin on 8/9/2016.
 */
public class MyUser {

   // private int birthYear;
    private String author;
    private String title;
    public MyUser() {}
    public MyUser(String author, String title)
    {
        this.author = author;
        this.title = title;
    }
    /*
    public MyUser(String fullName, int birthYear) {
        this.fullName = fullName;
        this.birthYear = birthYear;
    }
    public long getBirthYear() {
        return birthYear;
    }*/
    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
}
