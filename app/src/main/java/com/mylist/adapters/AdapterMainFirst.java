package com.mylist.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import appy.com.wazznowapp.R;

/**
 * Created by admin on 8/1/2016.
 */
public class AdapterMainFirst extends BaseAdapter {

    Context con;
    ArrayList<MainData> alList;
    ViewHolder viewHolder;

    public AdapterMainFirst(Context context, ArrayList<MainData> aList){
        con = context;
        alList = aList;
    }

    @Override
    public int getCount() {
        return 10;
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


        return view;
    }

    class ViewHolder{
        ImageView img, imgChat;
        TextView tvCateName, tvHour, tvEventName, tvNoOfTune, tvEventPlace;
    }
}
