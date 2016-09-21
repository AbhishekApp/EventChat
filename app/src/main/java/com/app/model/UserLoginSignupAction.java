package com.app.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.SignUpActivity;

/**
 * Created by admin on 9/6/2016.
 */
public class UserLoginSignupAction {

    SharedPreferences.Editor editor;
    String firebaseUserURL = MyApp.FIREBASE_BASE_URL;

    public void userSignup(final Activity con, final String uName, final String uLastName,final String uPhone, final String email, final String password){
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
                                editor = MyApp.preferences.edit();
                                editor.putString(SignUpActivity.USER_NAME, "");
                                editor.putString(SignUpActivity.USER_EMAIL, "");
                                editor.putString(SignUpActivity.USER_PASSWORD, "");
                                editor.commit();
                            }else if(task.isSuccessful()){
                                Toast.makeText(con, "User created successfully", Toast.LENGTH_SHORT).show();
                                task.getResult();
                                editor = MyApp.preferences.edit();
                                editor.putString(SignUpActivity.USER_NAME, uName);
                                editor.putString(SignUpActivity.USER_LAST_NAME, uLastName);
                                editor.putString(SignUpActivity.USER_PHONE, uPhone);
                                editor.putString(SignUpActivity.USER_EMAIL, email);
                                editor.putString(SignUpActivity.USER_PASSWORD, password);
                                editor.commit();

                                Firebase firebase = new Firebase(firebaseUserURL);
                                Map<String, String> alanisawesomeMap = new HashMap<String, String>();
                                alanisawesomeMap.put("name", uName);
                                alanisawesomeMap.put("lastName", uLastName);
                                alanisawesomeMap.put("passKey", password);
                                alanisawesomeMap.put("phone", uPhone);
                                alanisawesomeMap.put("email", email);

                                final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();

                                System.out.println("USER List new length : " );
                                System.out.println("USER List new deviceID : " );
                                users.put("0", alanisawesomeMap);
                                firebase.child("users/" + password).setValue(users);
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
                        } else {
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

   /* Map<String, String> alanisawesomeMap;
    private void addUserDetail(String name, String lastName, String passKey, String phone, String email){

        Map<String, String> alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("name", name);
        alanisawesomeMap.put("lastName", lastName);
        alanisawesomeMap.put("passKey", passKey);
        alanisawesomeMap.put("phone", phone);
        alanisawesomeMap.put("email", email);
        UserDetailTask task = new UserDetailTask();
        task.execute();
    }*/


    /*class UserDetailTask extends AsyncTask<Void, Void, Void> {

        HttpURLConnection urlConnection;
        JSONArray jsonArray;
        int length = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(firebaseUserURL+"/users.json");
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println("GetUSER jsonObject : " + sb.toString());
                JSONObject jsonObject = new JSONObject(sb.toString());
                System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
                length =  jsonObject.length();
                System.out.println("EVENT DATA length : " + length);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
            length++;
            System.out.println("USER List length : " + length);
            users.put(""+length, alanisawesomeMap);
            Firebase usersRef = new Firebase(firebaseUserURL).child("users");//.child(""+length);
            usersRef.setValue(users);
        }
    }*/
}
