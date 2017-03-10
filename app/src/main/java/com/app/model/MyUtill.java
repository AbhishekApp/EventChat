package com.app.model;

import android.app.Activity;
import android.app.AlertDialog;
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





    public void JODATimeDiff(String today, String StartTime1){
        Period p = new Period(new DateTime(today), new DateTime(StartTime1), PeriodType.dayTime());
        int month = p.getMonths();
        int days = p.getDays();
        int hours = p.getHours();
        int mints = p.getMinutes();
        int seconds = p.getSeconds();

    }






    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime)  {
        /*String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)&& actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting MM/dd/yyyy HH:mm:ss format");
        }*/

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


        /*


        try {
            String string1 = "20:11:13";
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = "14:49:00";
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            String someRandomTime = "01:00:00";
            Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.
                System.out.println(true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

      */





        /*

          public static boolean isTimeBetweenTwoTime(String argStartTime,
            String argEndTime, String argCurrentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        //
        if (argStartTime.matches(reg) && argEndTime.matches(reg)
                && argCurrentTime.matches(reg)) {
            boolean valid = false;
            // Start Time
            java.util.Date startTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(argStartTime);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Current Time
            java.util.Date currentTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(argCurrentTime);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentTime);

            // End Time
            java.util.Date endTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(argEndTime);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            //
            if (currentTime.compareTo(endTime) < 0) {

                currentCalendar.add(Calendar.DATE, 1);
                currentTime = currentCalendar.getTime();

            }

            if (startTime.compareTo(endTime) < 0) {

                startCalendar.add(Calendar.DATE, 1);
                startTime = startCalendar.getTime();

            }
            //
            if (currentTime.before(startTime)) {

                System.out.println(" Time is Lesser ");

                valid = false;
            } else {

                if (currentTime.after(endTime)) {
                    endCalendar.add(Calendar.DATE, 1);
                    endTime = endCalendar.getTime();

                }

                System.out.println("Comparing , Start Time /n " + startTime);
                System.out.println("Comparing , End Time /n " + endTime);
                System.out
                        .println("Comparing , Current Time /n " + currentTime);

                if (currentTime.before(endTime)) {
                    System.out.println("RESULT, Time lies b/w");
                    valid = true;
                } else {
                    valid = false;
                    System.out.println("RESULT, Time does not lies b/w");
                }

            }
            return valid;

        } else {
            throw new IllegalArgumentException(
                    "Not a valid time, expecting HH:MM:SS format");
        }

    }
        */

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
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Time to level up!");
        alertDialog.setMessage("It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum.");
        alertDialog.setPositiveButton("Update",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // DO TASK
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                }
            });
        alertDialog.setNegativeButton("Exit",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // DO TASK
                }
            });
        alertDialog.show();
    }
}
