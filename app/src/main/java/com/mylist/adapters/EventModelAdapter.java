package com.mylist.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.model.EventDetail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.R;

/**
 * Created by admin on 9/17/2016.
 */
public class EventModelAdapter extends BaseAdapter {

    Context con;
    ArrayList<EventDetail> alList;
    ViewHolder viewHolder;
    SharedPreferences preferences;

    public EventModelAdapter(Context con, ArrayList<EventDetail> alList){
            this.con = con;
            this.alList = alList;
            preferences = MyApp.preferences;
    }

    @Override
    public int getCount() {
        return alList.size();
    }

    @Override
    public Object getItem(int position) {
        return alList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main_row, null);
            viewHolder.img = (ImageView) view.findViewById(R.id.imgRowMain);
            viewHolder.tvCateName = (TextView) view.findViewById(R.id.tvCatRow);
            viewHolder.tvHour = (TextView) view.findViewById(R.id.tvTimeRow);
            viewHolder.tvEventName = (TextView) view.findViewById(R.id.tvEventNameRow);
            viewHolder.tvNoOfTune = (TextView) view.findViewById(R.id.tvEventTunedRow);
            viewHolder.tvEventPlace = (TextView) view.findViewById(R.id.tvEventLocRow);
            viewHolder.imgChat = (ImageView) view.findViewById(R.id.imgChatRow);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        EventDetail detail = alList.get(position);
        viewHolder.tvCateName.setText(detail.getCategory_name());
        viewHolder.tvEventName.setText(detail.getEvent_title());
        viewHolder.tvEventPlace.setText(detail.getEvent_meta());
        viewHolder.tvHour.setText(String.valueOf(getTimeDifference(detail.getEvent_date(), detail.getEvent_time())));
        String groupRec = preferences.getString(MyApp.USER_JOINED_GROUP, null);
        if(detail.getSubscribed_user().equalsIgnoreCase("0")) {
                viewHolder.tvNoOfTune.setVisibility(View.GONE);
        }
        else {

            String subscribed_user = detail.getSubscribed_user();
            if (groupRec != null && detail.getCatergory_id() != null) {
                if (groupRec.contains(detail.getCatergory_id())) {
                    try {
                        int iSubscribedUser = Integer.parseInt(subscribed_user);
                        iSubscribedUser--;
                        subscribed_user = "You +" + iSubscribedUser;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        subscribed_user = "+" + subscribed_user;
                    }
                }
            }else{
                subscribed_user = "+" + subscribed_user;
            }
            viewHolder.tvNoOfTune.setVisibility(View.VISIBLE);
            viewHolder.tvNoOfTune.setText( subscribed_user + " Tuned In");
        }
        try {
            if (groupRec != null && detail.getCatergory_id() != null) {
                if (groupRec.contains(detail.getCatergory_id())) {
                    viewHolder.imgChat.setImageResource(R.mipmap.chat_subscribe);
                } else {
                    viewHolder.imgChat.setImageResource(R.mipmap.chat_icon);
                }
            }
        }catch (Exception ex){
            viewHolder.imgChat.setImageResource(R.mipmap.chat_icon);
        }
        return view;
    }

    class ViewHolder{
        ImageView img, imgChat;
        TextView tvCateName, tvHour, tvEventName, tvNoOfTune, tvEventPlace;
    }

    private String getTimeDifference(String startDate, String startTime){
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
//            fromDate = date1+time1;
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
        System.out.println("Time Difference : "+dateFormat);
        return dateFormat;
    }
}
