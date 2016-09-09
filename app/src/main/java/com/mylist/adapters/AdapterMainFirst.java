package com.mylist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.model.EventData;

import java.util.ArrayList;

import appy.com.wazznowapp.R;

/**
 * Created by admin on 8/1/2016.
 */
public class AdapterMainFirst extends BaseAdapter {

    Context con;
    ArrayList<EventData> alList;
    ViewHolder viewHolder;

    public AdapterMainFirst(Context context, ArrayList<EventData> aList){
        con = context;
        this.alList = aList;
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
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }

        EventData data = alList.get(position);
        System.out.println("data.getevent_super_cate_name() "+data.getevent_super_cate_name());
        System.out.println("data.getevent_cate_name() "+data.getevent_cate_name());
        viewHolder.tvCateName.setText(data.getevent_super_cate_name());
        viewHolder.tvEventName.setText(data.getevent_cate_name());

        return view;
    }

    class ViewHolder{
        ImageView img, imgChat;
        TextView tvCateName, tvHour, tvEventName, tvNoOfTune, tvEventPlace;
    }
}
