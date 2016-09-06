package com.app.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.FirebaseSimpleLoginError;
import com.firebase.simplelogin.FirebaseSimpleLoginUser;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.SimpleLoginCompletionHandler;

/**
 * Created by admin on 9/6/2016.
 */
public class UserLoginSignupAction {

    public void userSignup(Firebase myRef, final Context con, String email, String password){
        SimpleLogin authClient = new SimpleLogin(myRef, con);
        authClient.createUser(email, password, new SimpleLoginAuthenticatedHandler() {
            public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
                if(error != null) {
                    // There was an error creating this account
                    Toast.makeText(con, "Create user failed", Toast.LENGTH_SHORT).show();
                }
                else {
                    // We created a new user account
                    Toast.makeText(con, "Create user successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void userLogin(Firebase myRef, final Context con, String email, String password){
        SimpleLogin authClient = new SimpleLogin(myRef, con);
        authClient.loginWithEmail(email, password, new SimpleLoginAuthenticatedHandler() {
            public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
                if (error != null) {
                    // There was an error logging into this account
                    Toast.makeText(con, "Login failed", Toast.LENGTH_SHORT).show();
                    Log.e("Signup", "Login Failed ERROR: "+error.getMessage());
                } else {
                    // We are now logged in
                    Toast.makeText(con, "Login Successfull", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void userChangePassword(Firebase myRef, final Context con, String email, String password, String newPassword){
        SimpleLogin authClient = new SimpleLogin(myRef, con);
        authClient.changePassword(email, password, newPassword, new SimpleLoginCompletionHandler() {
            public void completed(FirebaseSimpleLoginError error, boolean success) {
                if(error != null) {
                    // There was an error processing this request
                    Toast.makeText(con, "Password not changed", Toast.LENGTH_SHORT).show();
                }
                else if(success) {
                    // Password changed successfully
                    Toast.makeText(con, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
