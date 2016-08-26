package com.mylist.adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

    Activity activity;
    TextView tvUser;
    TextView tvMsg;
    LinearLayout linear, linearBtn;
    RelativeLayout.LayoutParams relativeParam;

    public StadiumChatListAdapter(Query ref, Activity activity, int layout, String mUsername){
        super(ref, ChatData.class, layout, activity);
        this.activity = activity;
    }

    @Override
    protected void populateView(View v, ChatData model) {
        tvUser = (TextView) v.findViewById(R.id.tvChatUser);
        tvMsg = (TextView) v.findViewById(R.id.tvChat);
        linear = (LinearLayout) v.findViewById(R.id.linearMsgChat);
        linearBtn = (LinearLayout) v.findViewById(R.id.linearBtn);

//        tvMsg.loadData(model.getTitle(), "text/utf-8", "iso-8");
        tvMsg.setText(model.getTitle());
        tvUser.setText(model.getAuthor());
        linearBtn.setVisibility(View.GONE);


        tvMsg.setPadding(2, 2, 2, 2);
        tvUser.setPadding(2, 2, 2, 2);
        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(model.getAuthor().contains("Abhi")) {
            tvMsg.setGravity(Gravity.RIGHT);
            tvUser.setGravity(Gravity.RIGHT);
            tvUser.setVisibility(View.GONE);

            linear.setGravity(Gravity.RIGHT);
            relativeParam.addRule(Gravity.CENTER);
            relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            linear.setLayoutParams(relativeParam);
            linearBtn.setVisibility(View.GONE);
        }else if(model.getAuthor().equalsIgnoreCase("Admin")) {

            tvMsg.setGravity(Gravity.LEFT);
            tvUser.setGravity(Gravity.LEFT);
            linear.setGravity(Gravity.LEFT);
            linearBtn.setVisibility(View.VISIBLE);
            //v.setBackgroundColor(activity.getResources().getColor(R.color.chat_btn_back));
        }
        else{
            tvMsg.setGravity(Gravity.LEFT);
            tvUser.setGravity(Gravity.LEFT);
            tvUser.setVisibility(View.VISIBLE);

            relativeParam.addRule(Gravity.CENTER);
            linear.setGravity(Gravity.LEFT);
            relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            linear.setLayoutParams(relativeParam);
            linearBtn.setVisibility(View.GONE);
        }

    }
}
