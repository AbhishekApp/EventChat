package com.mylist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.get.wazzon.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by admin on 4/28/2017.
 */

public class WonAdapter extends BaseAdapter {

    Context con;
    ArrayList<String> alList;
    TextView tvWonRow;

    public WonAdapter(Context context, ArrayList<String> arrayList){
        con = context;
        alList = arrayList;
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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.won_row, null);
            tvWonRow = (TextView) view.findViewById(R.id.tvWonMsg);
        }else{
            tvWonRow = (TextView) view.findViewById(R.id.tvWonMsg);
        }

        tvWonRow.setText(alList.get(position));

        return view;
    }
}
