package appy.com.wazznowapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.app.model.ConnectDetector;
import com.firebase.client.Firebase;

/**
 * Created by admin on 8/9/2016.
 */
public class MyApp extends Application {

    ConnectDetector connectDetector;
    public static boolean firebaseFlag = false;
  //  public static boolean USER_LOGIN = false;
    public static SharedPreferences preferences;
    public static String MyPREFERENCES = "UserName";
    public static String FIREBASE_BASE_URL =  "https://wazznow-cd155.firebaseio.com";
    public static String USER_JOINED_GROUP = "UserJoinedGroup";
    public static String USER_NAME = "UserName";
    public static String USER_LAST_NAME = "UserLastName";
    public static String USER_PHONE = "UserPhone";
    public static String USER_EMAIL = "UserEmail";
    public static String USER_PASSWORD = "UserPassword";

    @Override
    public void onCreate() {
        super.onCreate();
        connectDetector = new ConnectDetector(this);
        if(connectDetector.getConnection()) {
            Firebase.setAndroidContext(this);
            firebaseFlag = true;
        }
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        getDeviveID(getApplicationContext());
    }

    public static String getDeviveID(Context con){
        String android_id = Settings.Secure.getString(con.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("Android", "Android ID : " + android_id);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Android_ID", android_id);
        editor.commit();
        return android_id;
    }
}
