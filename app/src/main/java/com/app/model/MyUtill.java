package com.app.model;

import android.util.Log;

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
            System.out.println(sb.toString());
             jsonObject = new JSONArray(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex){
            Log.e("MyUtill", "Get Data From Server ERROR: "+ex.toString());
        }

    //    System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());

        return jsonObject;
    }

    public String getTimeDifference(String startDate, String startTime){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String diff="";
        try {
            Date Date2 = format.parse(startDate+" "+startTime);
            long mills = Date2.getTime() - System.currentTimeMillis();
            mills = mills/1000;
            long seconds = mills % 60;
            mills/= 60;
            long minutes =mills % 60;
             mills /= 60;
            long hours = mills % 24;
            mills /= 24;
            long days = mills;
            diff= days+" days "+hours+" hrs "+minutes+" mins";
            if (diff.contains("-")){
                diff="";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return diff;
    }
}
