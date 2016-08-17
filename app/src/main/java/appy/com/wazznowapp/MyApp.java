package appy.com.wazznowapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.model.ConnectDetector;
import com.firebase.client.Firebase;

/**
 * Created by admin on 8/9/2016.
 */
public class MyApp extends Application {

    ConnectDetector connectDetector;
    public static boolean firebaseFlag = false;
    public static boolean USER_LOGIN = false;
    public static SharedPreferences preferences;
    public static String MyPREFERENCES = "UserName";

    @Override
    public void onCreate() {
        super.onCreate();
        connectDetector = new ConnectDetector(this);
        if(connectDetector.getConnection()) {
            Firebase.setAndroidContext(this);
            firebaseFlag = true;
        }
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }
}
