package com.app.model;

/**
 * Created by admin on 8/12/2016.
 */
public class UserProfile {

    String userName;
    String userLastName;
    String userEmail;
    String userPassword;
    String userPhone;
    String userID;

    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserLastName(){
        return this.userLastName;
    }
    public void setUserLastName(String userLastName){
        this.userLastName = userLastName;
    }

    public String getUserEmail(){
        return this.userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public String getUserPassword(){
        return this.userPassword;
    }
    public void setUserPassword(String userPassword){
        this.userPassword = userPassword;
    }

    public String getUserPhone(){
        return this.userPhone;
    }
    public void setUserPhone(String userPhone){
        this.userPhone = userPhone;
    }

    public String getUserID(){
        return this.userID;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }

}
