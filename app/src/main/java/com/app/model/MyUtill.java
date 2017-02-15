package com.app.model;

import android.app.Activity;
import android.util.Log;

import com.firebase.client.Firebase;

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

import appy.com.wazznowapp.EventChatActivity;
import appy.com.wazznowapp.MyApp;

import static appy.com.wazznowapp.HousePartyFragment.getCurrentTimeStamp;
import static appy.com.wazznowapp.MyApp.FIREBASE_BASE_URL;

/**
 * Created by admin on 10/17/2016.
 */
public class MyUtill {
    HttpURLConnection urlConnection;
    JSONArray jsonObject;
    URL url = null;

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
            //e.printStackTrace();// for now eat exceptions
        } catch (Exception ex){
            Log.e("MyUtill", "Get Data From Server ERROR: "+ex.toString());
        }
    //    System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
        return jsonObject;
    }

    public static void addMsgtoFeatured(Activity act,String msg){
        Firebase myFirebaseRef = new Firebase(FIREBASE_BASE_URL);
        Firebase alanRef = myFirebaseRef.child(EventChatActivity.SuperCateName + "/ " + EventChatActivity.eventDetail.getCategory_name()).child("FeatureChat");
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
        alanRef.keepSynced(true);
        ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(act), getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),"normal");
        alanRef.push().setValue(alan);
        MyApp.CustomEventAnalytics("featured_sent", EventChatActivity.SuperCateName , EventChatActivity.eventDetail.getCategory_name());
    }


    public String getTimeDifference(String startDate, String startTime){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String diff="";
        try {
            Date Date2 = format.parse(startDate+" "+startTime);
            long mills = Date2.getTime() - System.currentTimeMillis();
            mills = mills/1000;
            long seconds = (int) mills % 60;
            mills/= 60;
            int  minutes = (int) mills % 60;
            mills /= 60;
            int hours =  (int) mills % 24;
            mills /= 24;
            int  days = (int) mills;

            //diff= days+" days "+hours+" hrs "+minutes+" mins";
            if (days>0){
                diff= days+" days "+hours+" hrs "+minutes+" mins";
            }
            else if(days<=0 && hours > 0){
                diff= hours+" hrs "+minutes+" mins "+seconds+" sec";
            }
            else if(days<=0 && hours <=0){
                diff= minutes+" mins "+seconds+" sec";
            }
            else if(days<=0 && hours <=0 && minutes <=0){
                diff= seconds+" seconds";
            }
            if (diff.contains("-")){
                diff=diff.replace("-","")+" Ago";

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return diff;
    }
}
