package com.app.model;

import android.app.Activity;
import android.util.Log;
import com.firebase.client.Firebase;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
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
        //System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
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




    public String getTimeDifference(String startDate){
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





    public void JODATimeDiff(String today, String StartTime1){
        Period p = new Period(new DateTime(today), new DateTime(StartTime1), PeriodType.dayTime());
        int month = p.getMonths();
        int days = p.getDays();
        int hours = p.getHours();
        int mints = p.getMinutes();
        int seconds = p.getSeconds();

    }



}
