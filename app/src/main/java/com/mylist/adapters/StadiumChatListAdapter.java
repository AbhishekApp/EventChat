package com.mylist.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.app.model.ChatData;
import com.firebase.client.Query;

import appy.com.wazznowapp.R;

/**
 * Created by admin on 8/11/2016.
 */
public class StadiumChatListAdapter extends FirebaseListAdapter<ChatData> {
 //   (Query mRef, Class<T> mModelClass, int mLayout, Activity activity)

    public StadiumChatListAdapter(Query ref, Activity activity, int layout, String mUsername){
        super(ref, ChatData.class, layout, activity);
    }

    @Override
    protected void populateView(View v, ChatData model) {
        TextView tv = (TextView) v.findViewById(R.id.tvChat);
        tv.setText(model.getTitle());
    }
}
