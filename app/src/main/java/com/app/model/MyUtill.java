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
import java.text.DateFormat;
import java.text.ParseException;
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

             jsonObject = new JSONArray(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex){
            Log.e("MyUtill", "Get Data From Server ERROR: "+ex.toString());
        }

        System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());

        return jsonObject;
    }

    public String getTimeDifference(String startDate, String startTime){
        String format = "MM/dd/yyyy HH:mm:ss";
        System.out.println("event Time Difference : "+startDate+" "+startTime);
        String date1 = startDate;
        String time1 = startTime;
        DateFormat dtFormat = new SimpleDateFormat(format);
        Date date = new Date();
        System.out.println(dtFormat.format(date));
        String eDate[] = dtFormat.format(date).split(" ");
        String date2 = eDate[0];
        String time2 = eDate[1];

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = sdf.parse(date1 + " " + time1);
            toDate = sdf.parse(date2 + " " + time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println("Time Difference toDate : "+toDate);
//        System.out.println("Time Difference fromDate : "+fromDate);

        long diff =  fromDate.getTime() - toDate.getTime();
        String dateFormat="";
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        if(diffDays>0){
            dateFormat+=diffDays+" day ";
        }
        diff -= diffDays * (24 * 60 * 60 * 1000);

//        System.out.println("Time Difference diff : "+diff);

        int diffhours = (int) (diff / (60 * 60 * 1000));
        if(diffhours>0){
            dateFormat+=diffhours+" hour ";
        }
        diff -= diffhours * (60 * 60 * 1000);

        int diffmin = (int) (diff / (60 * 1000));
        if(diffmin>0){
            dateFormat+=diffmin+" min ";
        }
        diff -= diffmin * (60 * 1000);

        int diffsec = (int) (diff / (1000));
        if(diffsec>0){
            // dateFormat+=diffsec+" sec";
        }
        System.out.println("Line 112 event Time Difference : "+dateFormat);
        return dateFormat.trim();
    }
}
