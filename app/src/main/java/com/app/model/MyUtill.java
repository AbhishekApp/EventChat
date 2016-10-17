package com.app.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 10/17/2016.
 */
public class MyUtill {


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
        System.out.println("Line 184 event Time Difference : "+dateFormat);
        return dateFormat.trim();
    }
}
