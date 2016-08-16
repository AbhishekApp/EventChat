package com.mylist.adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.ChatData;
import com.firebase.client.Query;

import appy.com.wazznowapp.R;

/**
 * Created by admin on 8/11/2016.
 */
public class StadiumChatListAdapter extends FirebaseListAdapter<ChatData> {
 //   (Query mRef, Class<T> mModelClass, int mLayout, Activity activity)

    TextView tvUser, tvMsg;
    LinearLayout linear;
    RelativeLayout.LayoutParams relativeParam;

    public StadiumChatListAdapter(Query ref, Activity activity, int layout, String mUsername){
        super(ref, ChatData.class, layout, activity);
    }

    @Override
    protected void populateView(View v, ChatData model) {
        tvUser = (TextView) v.findViewById(R.id.tvChatUser);
        tvMsg = (TextView) v.findViewById(R.id.tvChat);
        linear = (LinearLayout) v.findViewById(R.id.linearMsgChat);

        tvMsg.setText(model.getTitle());
        tvUser.setText(model.getAuthor());


        tvMsg.setPadding(2, 2, 2, 2);
        tvUser.setPadding(2, 2, 2, 2);
        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if(!model.getAuthor().contains("Abhi")) {
             tvMsg.setGravity(Gravity.LEFT);
            tvUser.setGravity(Gravity.LEFT);
            tvUser.setVisibility(View.VISIBLE);

            linear.setGravity(Gravity.LEFT);
            relativeParam.addRule(Gravity.CENTER);
            relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            linear.setLayoutParams(relativeParam);
        }else{
            tvMsg.setGravity(Gravity.RIGHT);
            tvUser.setGravity(Gravity.RIGHT);
            tvUser.setVisibility(View.GONE);

            relativeParam.addRule(Gravity.CENTER);
            linear.setGravity(Gravity.RIGHT);
            relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            linear.setLayoutParams(relativeParam);
        }

    }
}
