package appy.com.wazznowapp;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by admin on 8/9/2016.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
