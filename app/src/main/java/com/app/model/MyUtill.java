package com.app.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.get.wazzon.EventChatActivity;
import com.get.wazzon.MyApp;
import com.get.wazzon.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.get.wazzon.EventChatActivity.CatID;
import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.HousePartyFragment.getCurrentTimeStamp;
import static com.get.wazzon.MyApp.FIREBASE_BASE_URL;


/**
 * Created by admin on 10/17/2016.
 */
public class MyUtill {
    HttpURLConnection urlConnection;
    JSONArray jsonObject = new JSONArray();
    URL url = null;
    public static String popupTitle = "";
    public static String popupMessage = "";



    public JSONArray getJSONFromServer(String urlStr){
        try {
            Log.e("MyUtill","getJSONFromServer URL : "+urlStr);
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            //System.out.println(sb.toString());
             jsonObject = new JSONArray(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();// for now eat exceptions
        } catch (Exception ex){
            Log.e("MyUtill", "Get Data From Server ERROR: "+ex.toString());
        }
        return jsonObject;
    }

    public static void addMsgtoFeatured(Activity act,String msg){
        Firebase myFirebaseRef = new Firebase(FIREBASE_BASE_URL);
        Firebase alanRef = myFirebaseRef.child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name()).child("FeatureChat");
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        alanRef.keepSynced(true);
        ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(act), getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),"normal");
        alanRef.push().setValue(alan);
        MyApp.CustomEventAnalytics("featured_sent",  eventDetail.getEvent_id()+"_featured");
    }


    public static void addMsgToCommentatorNotifier(Activity act,String msg){
        Firebase myFirebaseRef = new Firebase(FIREBASE_BASE_URL);
        Firebase alanRef = myFirebaseRef.child("/").child("commentator_notifier");
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        alanRef.keepSynced(true);
        ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(act), getCurrentTimeStamp(),"com","normal");
        NotifierChatData notaln = new NotifierChatData(alan,CatID,eventID);
        alanRef.push().setValue(notaln);
    }



    public static  void subscribeUserForEvents(String eventID){
        FirebaseMessaging.getInstance().subscribeToTopic(eventID);
    }



    public static String getDaysDifference(String startDate){
        String diff="";
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            Date Date2 = format.parse(startDate);
            long mills = Date2.getTime() - System.currentTimeMillis();
            long seconds = mills / 1000;
            long days = seconds / 86400;

            if(days>30){
             diff = "is coming soon";
            }else {
                diff = days + " Days";
            }
        }
        catch (Exception e){
        e.printStackTrace();
        }
        return diff;
    }


    public static String getTimeDifference(String startDate){
        String diff="";
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            Date Date2 = format.parse(startDate);
            long mills = Date2.getTime() - System.currentTimeMillis();
            long seconds = mills/1000;
            long days = seconds / 86400;
            String temp = ""+seconds;
            if (temp.contains("-")){
                    diff="Ago";
            }else {
                //days= Math.abs(days);
                if (days > 365) {
                    long years = days / 365;
                    diff = years + " Years to go";
                } else {
                    if (days > 30) {
                        long months = days / 30;
                        diff = "More than "+months + " Months to go";
                    } else {
                        if (days > 1 && days <= 30) {
                            if(days == 30){
                                long months = days / 30;
                                diff = months + " Month to go";
                            }else if(days<30){
                                diff = days + " Days to go";
                            }
                        } else {
                                if (days < 1) {
                                    long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
                                    diff = hours + " Hours to go";
                                }
                            }
                        }
                    }
             }
        }catch(Exception e){
            e.printStackTrace();
        }
        return diff;
    }


        public static boolean checkGooglePlaySevices(final Activity activity) {
            final int googlePlayServicesCheck = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
            switch (googlePlayServicesCheck) {
                case ConnectionResult.SUCCESS:
                    return true;
                case ConnectionResult.SERVICE_DISABLED:
                case ConnectionResult.SERVICE_INVALID:
                case ConnectionResult.SERVICE_MISSING:
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCheck, activity, 0);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            activity.finish();
                        }
                    });
                    dialog.show();
            }
            return false;
        }


    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime)  {
        long mills = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date resultdate = new Date(mills);
        String currentTime = sdf.format(resultdate);
        System.out.println(sdf.format(resultdate));
        try{
            java.util.Date inTime1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(initialTime);
            java.util.Date inTime2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(finalTime);
            java.util.Date inTime3 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(currentTime);

            if (inTime3.getTime() > inTime1.getTime() && inTime3.getTime() < inTime2.getTime()){
                Log.e("TimeDifference","inTime3 is between inTime1 and inTime2");
                return true;
            }else{
                Log.e("TimeDifference","in Else Condition");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static void hideKeyBoard(Activity act, View view, Boolean force) {
        //View view = act.getCurrentFocus();
        if (force) {
            /*************************MORE FORCEFUL********** MAYBE AGAINST ANDROID GUIDELINES ***********DID DUE TO REQUIREMENT*/
            try {
                act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else {
                Toast.makeText(act, "error", Toast.LENGTH_LONG).show();
            }
        }
    }


    public static void alertDialogShowUpdate(final Activity activity)
    {   final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        //alertDialog.setTitle(activity.getResources().getString(R.string.update_title));
        alertDialog.setTitle(popupTitle);
        //alertDialog.setMessage(activity.getResources().getString(R.string.update_msg));
        alertDialog.setMessage(popupMessage);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.positive_btn),
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // DO TASK
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
            }
            });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.negative_btn),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // DO TASK
                }
            });
        alertDialog.show();
    }
}
