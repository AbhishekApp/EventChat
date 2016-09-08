package com.mylist.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.model.EventData;
import com.firebase.client.Query;

import appy.com.wazznowapp.R;

/**
 * Created by admin on 9/8/2016.
 */
public class EventAdapter extends FirebaseListAdapter<EventData> {

    Activity activity;
    ViewHolder viewHolder;

    public EventAdapter(Query ref, Activity activity, int layout, String mUsername){
        super(ref, EventData.class, layout, activity);
        this.activity = activity;
    }

    @Override
    protected void populateView(View view, EventData model) {
        viewHolder = new ViewHolder();
        viewHolder.img = (ImageView) view.findViewById(R.id.imgRowMain);
        viewHolder.tvCateName = (TextView) view.findViewById(R.id.tvCatRow);
        viewHolder.tvHour = (TextView) view.findViewById(R.id.tvTimeRow);
        viewHolder.tvEventName = (TextView) view.findViewById(R.id.tvEventNameRow);
        viewHolder.tvNoOfTune = (TextView) view.findViewById(R.id.tvEventTunedRow);
        viewHolder.tvEventPlace = (TextView) view.findViewById(R.id.tvEventLocRow);
        viewHolder.imgChat = (ImageView) view.findViewById(R.id.imgChatRow);
    }

    class ViewHolder{
        ImageView img, imgChat;
        TextView tvCateName, tvHour, tvEventName, tvNoOfTune, tvEventPlace;
    }

}
