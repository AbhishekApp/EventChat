package com.mylist.adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.model.CannedCricketMessage;

import java.util.ArrayList;

import appy.com.wazznowapp.R;
/**
 * Created by admin on 10/21/2016.
 */
public class CannedAdapter extends BaseAdapter {
    Context con;
    ArrayList<CannedCricketMessage> alList;
    TextView tvCanMsg;

    public CannedAdapter(Context context, ArrayList<CannedCricketMessage> alCan){
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
        CannedCricketMessage msg = alList.get(position);
        try{
            tvCanMsg.setText(msg.getCanned_message());
        }catch (Exception ex){
            Log.e("CannedAdapter", "Canned Message ERROR: "+ex.toString());
        }
        return view;
    }
}
