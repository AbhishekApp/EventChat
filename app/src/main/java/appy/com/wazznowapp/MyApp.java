package appy.com.wazznowapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.app.model.AdminMessage;
import com.app.model.AnalyticsSingleton;
import com.app.model.CannedMessage;
import com.app.model.ConnectDetector;
import com.firebase.client.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

/**
 * Created by admin on 8/9/2016.
 */
public class MyApp extends Application {
    ConnectDetector connectDetector;
    public static boolean firebaseFlag = false;
    //public static boolean USER_LOGIN = false;
    public static SharedPreferences preferences;
    public static String MyPREFERENCES = "UserName";
    public static String FIREBASE_BASE_URL =  "https://wazznow-cd155.firebaseio.com";
    public static String FIREBASE_IMAGE_URL = "gs://wazznow-cd155.appspot.com/EventImage/";
    public static String USER_JOINED_GROUP = "UserJoinedGroup";
    public static String USER_NAME = "UserName";
    public static String USER_LAST_NAME = "UserLastName";
    public static String USER_PHONE = "UserPhone";
    public static String USER_EMAIL = "UserEmail";
    public static String USER_TYPE = "UserType";
    public static String USER_PASSWORD = "UserPassword";
    public static ArrayList<CannedMessage> alCanMsg;
    public static Typeface authorFont,authorMsg;
    public static int FeaturedMsgLimit = 3;
    public static int StadiumMsgLimit = 5;
    public static FirebaseAnalytics firebaseAnalytics;
    public static final String CHAT_SENT="chat_sent";
    public static final String CANNED_SENT = "canned_sent";
    public static final String FEATURED_SENT = "featured_sent";
    public static final String FRAGMENT_SELECTED = "fragment_selected";
    public static final String SIGNUP_ACTIVITY_LOADED = "signup_activity_loaded";
    public static ArrayList<AdminMessage> alAdmMsg;
    public static boolean isSignupSuccessful= false;

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



        /*************************************Firebase Offline Capabilities****************************************/

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true); //Globally set Persistence to all instance of firebase at initialisation level of app

        /*************************************Enable Image Caching ************************************************/

        //Picasso.Builder builder = new Picasso.Builder(this);
        //builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        //Picasso built = builder.build();
        //built.setIndicatorsEnabled(true); //just for testing caching feature of Picasso
        //built.setloggingEnabled(true); //this feature is depreciated from  2.1.0 & now obsoleted
        //Picasso.setSingletonInstance(built);

        /**************************************************************************************************************/
        authorFont = Typeface.createFromAsset(getAssets(),"Roboto-Medium.ttf");
        authorMsg = Typeface.createFromAsset(getAssets(),"Roboto-Light.ttf");
        // Obtain the Firebase Analytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static String getDeviveID(Context con){
        String android_id = Settings.Secure.getString(con.getContentResolver(),Settings.Secure.ANDROID_ID);
        Log.d("Android", "Android ID : " + android_id);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Android_ID", android_id);
        editor.commit();
        return android_id;
    }


    public static void PreDefinedEventAnalytics(String AnalyticsName,String AnalyticsValue, String Item_ID){
        /********************************FIREBASE_ANALYTICS_CODE*****************************************/
        Bundle bundle = new Bundle();
        AnalyticsSingleton as = new AnalyticsSingleton();
        if (AnalyticsName!=null) {
            //as.setId();
            // choose random food name from the list
            as.setName(AnalyticsValue);
        }

        String eventName;
        switch (AnalyticsName) {
            case FirebaseAnalytics.Event.SELECT_CONTENT:
                eventName = "select_content";
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, as.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Item_ID);
                break;

            case FirebaseAnalytics.Event.JOIN_GROUP:
                eventName = "join_group";
                bundle.putString(FirebaseAnalytics.Param.GROUP_ID, Item_ID);
                break;

            case FirebaseAnalytics.Event.SHARE:
                eventName = "share";
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, as.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Item_ID);
                break;

            case FirebaseAnalytics.Event.SIGN_UP:
                eventName = "sign_up";
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, as.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,Item_ID );
                break;

            case FirebaseAnalytics.Event.VIEW_ITEM_LIST:
                eventName = "view_item_list";
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, as.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Item_ID);
                break;

            default:
                throw new IllegalArgumentException("Invalid data: " + AnalyticsName);
        }

        //Logs an app event.
        firebaseAnalytics.logEvent(eventName, bundle);
        //Sets whether analytics collection is enabled for this app on this device.
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        firebaseAnalytics.setMinimumSessionDuration(20000);
        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(500); // in milli-seconds
        //Sets the user ID property.
        firebaseAnalytics.setUserId(String.valueOf(/*as.getId()*/Item_ID));
        //Sets a user property to a given value.
        firebaseAnalytics.setUserProperty("eventList", as.getName());
    }


    public static void CustomEventAnalytics(String EventLogName, String customEventName, String customEventMessage){
        /*************************************Firebase Custom Events Analytics*********************************/
        String eventName = "A";
        switch (EventLogName) {
            case CHAT_SENT:
                eventName = "chat_sent";
                break;
            case CANNED_SENT:
                eventName = "canned_sent";
                break;
            case FEATURED_SENT:
                eventName = "featured_sent";
                break;
            case FRAGMENT_SELECTED:
                eventName = "fragment_selected";
                break;
            case SIGNUP_ACTIVITY_LOADED:
                eventName = "signup_activity_loaded";
                break;
            default:
                throw new IllegalArgumentException("Invalid data: " +EventLogName );
        }
        Bundle params = new Bundle();
        params.putString("custom_event", customEventName);
        params.putString("custom_message", customEventMessage);
        firebaseAnalytics.logEvent(eventName, params);
    }
}
