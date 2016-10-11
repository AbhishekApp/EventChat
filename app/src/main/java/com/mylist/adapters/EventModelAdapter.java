package com.mylist.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.model.EventData;
import com.app.model.EventDetail;
import com.app.model.EventModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.R;
import appy.com.wazznowapp.SignUpActivity;

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
        viewHolder.tvHour.setText(String.valueOf(getTimeInterval(detail.getEvent_time())));
        String groupRec = preferences.getString(MyApp.USER_JOINED_GROUP, null);

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

    private int getTimeInterval(long eventTimeStamp){
        try {
            if (eventTimeStamp != 0) {
                Date purchasedDate = new Date();
//multiply the timestampt with 1000 as java expects the time in milliseconds
                purchasedDate.setTime((long) eventTimeStamp * 1000);

                Date currentDate = new Date();
                currentDate.setTime((long) System.currentTimeMillis() * 1000);

//To calculate the days difference between two dates
                int diffInDays = (int) ((currentDate.getTime() - purchasedDate.getTime())
                        / (1000 * 60 * 60 * 24));

                Date date = new Date(diffInDays);

                return diffInDays;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
        return 0;
    }
}
