package com.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import appy.com.wazznowapp.MyApp;

/**
 * Created by admin on 8/12/2016.
 */
public class UserProfile {

    private String userName;
    private String userLastName;
    private String userEmail;
    private String userPassword;
    private String userPhone;
    private String userID;

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

    public void updateUserGroup(Context con, String newGroup) {
        String userGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, null);
        if(userGroup != null && !TextUtils.isEmpty(userGroup)){
            if(!userGroup.contains(newGroup)){
                userGroup = userGroup +","+ newGroup;
            }else
            {
                return;
            }
        }else
        {
            userGroup = newGroup;
            Toast.makeText(con, "Tuned in successfully", Toast.LENGTH_SHORT).show();
        }
        Firebase usersRef = new Firebase(MyApp.FIREBASE_BASE_URL);
        String deviceID = MyApp.getDeviveID(con);
        Firebase alanRef = usersRef.child("users/"+deviceID+"/0");
        Map<String, Object> nickname = new HashMap<String, Object>();
        nickname.put("joined_group", userGroup);
        alanRef.updateChildren(nickname);
     // Toast.makeText(con, "User group update successfully "+newGroup, Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = MyApp.preferences.edit();
        editor.putString(MyApp.USER_JOINED_GROUP, userGroup);
        editor.commit();
    }




    public void update_house_party_invitations(Context con, String newGroup) {
        String userGroup = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, null);
        if(userGroup != null && !TextUtils.isEmpty(userGroup)){
            if(!userGroup.contains(newGroup)){
                userGroup = userGroup +","+ newGroup;
            }else
            {
                return;
            }
        }else
        {
            userGroup = newGroup;
            //Toast.makeText(con, "Invited successfully", Toast.LENGTH_SHORT).show();
        }
        Firebase usersRef = new Firebase(MyApp.FIREBASE_BASE_URL);
        String deviceID = MyApp.getDeviveID(con);
        Firebase alanRef = usersRef.child("users/"+deviceID+"/0");
        Map<String, Object> nickname = new HashMap<String, Object>();
        nickname.put("house_party_invitations", userGroup);
        alanRef.updateChildren(nickname);
        // Toast.makeText(con, "User group update successfully "+newGroup, Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = MyApp.preferences.edit();
        editor.putString(MyApp.HOUSE_PARTY_INVITATIONS, userGroup);
        editor.commit();
    }


}
