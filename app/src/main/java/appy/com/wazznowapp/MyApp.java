package appy.com.wazznowapp;

import android.app.Application;

import com.app.model.ConnectDetector;
import com.firebase.client.Firebase;

/**
 * Created by admin on 8/9/2016.
 */
public class MyApp extends Application {

    ConnectDetector connectDetector;
    public static boolean firebaseFlag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        connectDetector = new ConnectDetector(this);
        if(connectDetector.getConnection()) {
            Firebase.setAndroidContext(this);
            firebaseFlag = true;
        }
    }
}
