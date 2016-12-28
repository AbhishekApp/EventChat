package com.mylist.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.UserProfile;
import com.firebase.client.Query;

import appy.com.wazznowapp.EventChatFragment;
import appy.com.wazznowapp.InviteFriendActivity;
import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.R;
import appy.com.wazznowapp.SignUpActivity;

/**
 * Created by admin on 8/11/2016.
 */
public class StadiumChatListAdapter extends FirebaseListAdapter<ChatData> {

    Activity activity;
    TextView tvUser;
    TextView tvMsg;
    TextView btnYes, btnNo;
    LinearLayout linear, linearBtn;
    RelativeLayout.LayoutParams relativeParam;
    ImageView imgIcon;


    public StadiumChatListAdapter(Query ref, Activity activity, int layout){
        super(ref, ChatData.class, layout, activity);
        this.activity = activity;
    }

    @Override
    protected void populateView(final View v, ChatData model, int position) {
        imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
        tvUser = (TextView) v.findViewById(R.id.tvChatUser);
        tvMsg = (TextView) v.findViewById(R.id.tvChat);
        linear = (LinearLayout) v.findViewById(R.id.linearMsgChat);
        linearBtn = (LinearLayout) v.findViewById(R.id.linearBtn);
        btnYes = (TextView) v.findViewById(R.id.btnYesTuneOrInvite);
        btnNo = (TextView) v.findViewById(R.id.btnNoThanks);

        tvMsg.setText(model.getTitle());
        tvUser.setText(model.getAuthor());
        linearBtn.setVisibility(View.GONE);

        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        String sender = model.getAuthor();
        String fromUser = model.getToUser();
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        boolean isEqual = sender.equalsIgnoreCase(userName);
        if((!TextUtils.isEmpty(userName) && isEqual) || (fromUser.equals(MyApp.getDeviveID(activity)))) {
                tvMsg.setGravity(Gravity.RIGHT);
                tvMsg.setTextColor(activity.getResources().getColor(R.color.white));
                tvMsg.setPadding(15,15,90,15);
                tvUser.setGravity(Gravity.RIGHT);
                tvUser.setVisibility(View.GONE);

                linear.setGravity(Gravity.RIGHT);
                relativeParam.addRule(Gravity.CENTER);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                linear.setLayoutParams(relativeParam);
                linear.setBackgroundResource(R.drawable.chat_outgoing_background);

                linearBtn.setVisibility(View.GONE);

        }
        else{
            tvMsg.setGravity(Gravity.LEFT);
            tvUser.setGravity(Gravity.LEFT);
            tvUser.setVisibility(View.VISIBLE);

            relativeParam.addRule(Gravity.LEFT);
            linear.setGravity(Gravity.LEFT);

            relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            linear.setLayoutParams(relativeParam);
            linear.setBackgroundResource(R.drawable.incoming_message_bg);

            linearBtn.setVisibility(View.GONE);
        }

        if(model.getAuthor().equalsIgnoreCase("Admin")) {
            imgIcon.setVisibility(View.VISIBLE);

        }else{
            imgIcon.setVisibility(View.GONE);
            tvMsg.setBackgroundColor(Color.TRANSPARENT);

        }
        if(model.getAuthor().equalsIgnoreCase("Guest User")) {
         //   tvUser.setVisibility(View.GONE);
        }
    }


}
