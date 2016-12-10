package com.mylist.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.model.EventDetail;
import com.app.model.MyUtill;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import appy.com.wazznowapp.EventChatFragment;
import appy.com.wazznowapp.MainActivity;
import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.R;

/**
 * Created by admin on 9/17/2016.
 */
public class EventModelAdapter extends BaseAdapter {

    MyUtill myUtill;
    Context con;
    ArrayList<EventDetail> alList;
    ViewHolder viewHolder;
    SharedPreferences preferences;
    String mycolor[];

    public EventModelAdapter(Context con, ArrayList<EventDetail> alList){
            this.con = con;
            this.alList = alList;
            preferences = MyApp.preferences;
            myUtill = new MyUtill();
            mycolor = con.getResources().getStringArray(R.array.mycolor);
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
        String strTime =String.valueOf(myUtill.getTimeDifference(detail.getEvent_date(), detail.getEvent_time())).trim();
        System.out.println("Line 79 event Time Difference :"+strTime+":");
        if(!TextUtils.isEmpty(strTime)){
            viewHolder.tvHour.setText(strTime);
        }
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
                        subscribed_user = new String("You +" + iSubscribedUser);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        subscribed_user = new String(" +" + subscribed_user);
                    }
                }else{
                    subscribed_user = new String(" +" + subscribed_user);
                }
            }else{
                subscribed_user = new String(" +" + subscribed_user);
            }
            viewHolder.tvNoOfTune.setVisibility(View.VISIBLE);
            viewHolder.tvNoOfTune.setText( subscribed_user + " Tuned In");
        }
        try {
            if (groupRec != null && detail.getCatergory_id() != null) {
                if (MyApp.preferences.getBoolean(detail.getEvent_id(), false)) {
                    viewHolder.imgChat.setImageResource(R.mipmap.chat_subscribe);
                } else {
                    viewHolder.imgChat.setImageResource(R.mipmap.chat_icon);
                }
            }
        }catch (Exception ex){
            viewHolder.imgChat.setImageResource(R.mipmap.chat_icon);
        }
        view.setBackgroundColor(Color.parseColor(mycolor[position]));
        return view;
    }

    class ViewHolder{
        ImageView img, imgChat;
        TextView tvCateName, tvHour, tvEventName, tvNoOfTune, tvEventPlace;
    }


}
