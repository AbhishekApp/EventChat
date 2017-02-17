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


    public String getTimeDifference(String startDate){
        String diff="";
        //if(startDate.equals("03/20/2017  03:40:01")){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        try {
            Date Date2 = format.parse(startDate);
            long mills = Date2.getTime() - System.currentTimeMillis();
            long seconds = mills/1000;
            long days = seconds / 86400;

            /*mills/= 60;
            int  minutes = (int) mills % 60;
            mills /= 60;
            int hours =  (int) mills % 24;
            mills /= 24;
            int  days = (int) mills % 30;
            mills /= 30;
            int months = (int) mills % 12;
            mills /= 12;
            int years = (int) mills ;*/

            //diff= days+" days "+hours+" hrs "+minutes+" mins";

            /*if (years >=1 && months<=12 && days<=30) {
                diff = years + " Years to go" ;
            }
            if (years<=0 && months<12 &&  days>=30){
                diff= months+" Months to go";
            }
            if (years<=0 && months < 12 && days<30 && days>0){
                diff= days+" Days to go";
            }
            else if(days<=0 && hours > 0){
                diff= hours+" Hours to go";
            }
            else if(hours <=0 && minutes >=0){
                diff= minutes+" mins "+seconds+" sec";
            }
            else if(hours <=0 && minutes <0){
                diff= minutes+" Minutes to go";
            }
            else{
                System.out.println("LAST ELSE::::::::::  "+years + " years " + months + " months " + days + " days "+hours+" hrs "+minutes+" mins "+seconds+" sec");
            }*/
            //System.out.println("START::::::::::  " +  days + " days" );
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
                        /*int hours = days

                        if (days < 1 && hours > 1) {
                            diff = hours + " Hours to go";
                        } else {
                            if (minutes > 1) {
                                diff = minutes + " Minutes to go";
                            } else {
                                if (seconds > 1 && seconds < 60) {
                                    diff = minutes + " Seconds to go";*/
                        }
                    }
             }
        }catch(Exception e){
            e.printStackTrace();
        }//}
        return diff;
    }
}
