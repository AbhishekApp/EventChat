package com.app.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.FirebaseSimpleLoginError;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginCompletionHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.SignUpActivity;

/**
 * Created by admin on 9/6/2016.
 */
public class UserLoginSignupAction {

    SharedPreferences.Editor editor;

    public void userSignup(final Activity con, final String uName, final String email, final String password){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(mAuthListener);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(con, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Signup", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Toast.makeText(con, "User creation failed", Toast.LENGTH_SHORT).show();
                                MyApp.USER_LOGIN = false;
                            }else if(task.isSuccessful()){
                                Toast.makeText(con, "User created successfully", Toast.LENGTH_SHORT).show();
                                task.getResult();
                                editor = MyApp.preferences.edit();
                                editor.putString(SignUpActivity.USER_NAME, uName);
                                editor.putString(SignUpActivity.USER_EMAIL, email);
                                editor.putString(SignUpActivity.USER_PASSWORD, email);
                                editor.commit();
                                MyApp.USER_LOGIN = true;
                            }


                        }
                    });


        }else{
            Toast.makeText(con, "Please fill all the required field", Toast.LENGTH_SHORT).show();
        }
    }
    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d("Signup", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d("Signup", "onAuthStateChanged:signed_out");
            }
            // ...
        }
    };

    public void userLogin(Firebase myRef, final Activity con, String email, String password){
      /*  SimpleLogin authClient = new SimpleLogin(myRef, con);
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
        });*/

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(con, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Login", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Login", "signInWithEmail:failed", task.getException());
                            Toast.makeText(con, "Login Fail", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(con, "User logged in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    public void userChangePassword(Firebase myRef, final Context con, String email, String password, String newPassword){
        SimpleLogin authClient = new SimpleLogin(myRef, con);
        authClient.changePassword(email, password, newPassword, new SimpleLoginCompletionHandler() {
            public void completed(FirebaseSimpleLoginError error, boolean success) {
                if (error != null) {
                    // There was an error processing this request
                    Toast.makeText(con, "Password not changed", Toast.LENGTH_SHORT).show();
                } else if (success) {
                    // Password changed successfully
                    Toast.makeText(con, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void userLogout(){
        FirebaseAuth.getInstance().signOut();
    }


}
