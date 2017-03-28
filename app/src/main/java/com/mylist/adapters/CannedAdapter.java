package com.mylist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.get.wazzon.R;

import java.util.ArrayList;

/**
 * Created by admin on 10/21/2016.
 */
public class CannedAdapter extends BaseAdapter {
    Context con;
    ArrayList<String> alList;
    TextView tvCanMsg;

    public CannedAdapter(Context context, ArrayList<String> alCan){
        con = context;
        alList = alCan;
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
        if(view == null){
            LayoutInflater inflate = (LayoutInflater) con.getSystemService(con.LAYOUT_INFLATER_SERVICE);
            view = inflate.inflate(R.layout.canned_row, null);
        }
        tvCanMsg = (TextView) view.findViewById(R.id.tvCanMsg);
        String msg = alList.get(position);
        try{
            tvCanMsg.setText(msg);
        }catch (Exception ex){
            Log.e("CannedAdapter", "Canned Message ERROR: "+ex.toString());
        }
        return view;
    }
}
