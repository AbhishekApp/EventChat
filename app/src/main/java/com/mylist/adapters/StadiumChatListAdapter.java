package com.mylist.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


    public StadiumChatListAdapter(Query ref, Activity activity, int layout, String mUsername){
        super(ref, ChatData.class, layout, activity);
        this.activity = activity;
    }

    @Override
    protected void populateView(final View v, ChatData model) {
        tvUser = (TextView) v.findViewById(R.id.tvChatUser);
        tvMsg = (TextView) v.findViewById(R.id.tvChat);
        linear = (LinearLayout) v.findViewById(R.id.linearMsgChat);
        linearBtn = (LinearLayout) v.findViewById(R.id.linearBtn);
        btnYes = (TextView) v.findViewById(R.id.btnYesTuneOrInvite);
        btnNo = (TextView) v.findViewById(R.id.btnNoThanks);

//      tvMsg.loadData(model.getTitle(), "text/utf-8", "iso-8");
        tvMsg.setText(model.getTitle());
        tvUser.setText(model.getAuthor());
        linearBtn.setVisibility(View.GONE);
        final String adminMsg = tvMsg.getText().toString();
//        tvMsg.setPadding(2, 2, 2, 2);
//        tvUser.setPadding(2, 2, 2, 2);
        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        String sender = model.getAuthor();
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        boolean isEqual = sender.equalsIgnoreCase(userName);
        if(!TextUtils.isEmpty(userName) && isEqual) {
                tvMsg.setGravity(Gravity.RIGHT);
                tvUser.setGravity(Gravity.RIGHT);
                tvUser.setVisibility(View.GONE);

                linear.setGravity(Gravity.RIGHT);
                relativeParam.addRule(Gravity.CENTER);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                linear.setLayoutParams(relativeParam);
                linear.setBackgroundResource(R.drawable.outgoing_message_bg);
                linearBtn.setVisibility(View.GONE);

        }
        else{
            tvMsg.setGravity(Gravity.LEFT);
            tvUser.setGravity(Gravity.LEFT);
            tvUser.setVisibility(View.VISIBLE);

            relativeParam.addRule(Gravity.LEFT);
            linear.setGravity(Gravity.LEFT);
            linear.setBackgroundResource(R.drawable.incoming_message_bg);
            relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            linear.setLayoutParams(relativeParam);
            linearBtn.setVisibility(View.GONE);
        }
      /*  if (adminMsg.contains("Start a house party")){
        //    tvMsg.setVisibility(View.GONE);
            tvMsg.setText(adminMsg);
            linear.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.chat_back));
        }else if(adminMsg.contains("Congrates you are now part of 2.5k People in stadium following this match")){
          //  tvMsg.setVisibility(View.GONE);
            tvMsg.setText(adminMsg);
            tvMsg.setTextColor(activity.getResources().getColor(R.color.white));
            linear.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.chat_back));
        }else{
            linear.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.incoming_message_bg));
        }*/
        if(model.getAuthor().equalsIgnoreCase("Admin")) {
            try{
                if (!model.getToUser().equalsIgnoreCase(MyApp.preferences.getString("Android_ID", null))) {
                    v.setVisibility(View.GONE);
                } else {
                 //   tvMsg.setBackgroundColor(activity.getResources().getColor(R.color.chat_msg_back));
//                    tvMsg.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.patch));
                  //  tvMsg.setText(adminMsg);
                    if(adminMsg.contains("Congrates you are now part of"))
                    {
                        tvMsg.setPadding(25, 2, 10, 15);
                        linearBtn.setPadding(4, 4, 4, 4);
                    }
                    else {
                        tvMsg.setPadding(25, 8, 25, 8);
                        linearBtn.setPadding(6, 22, 6, 6);
                    }
                    tvMsg.setTextColor(activity.getResources().getColor(R.color.white));
                    linear.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.chat_img_b));
                    linearBtn.setVisibility(View.VISIBLE);
                    tvUser.setVisibility(View.GONE);
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (adminMsg.contains("Start a house party")) {
                                Intent ii = new Intent(activity, InviteFriendActivity.class);
                                activity.startActivity(ii);
                            } else {
                                UserProfile profile = new UserProfile();
                                profile.updateUserGroup(activity, EventChatFragment.eventID);
                                SharedPreferences.Editor editor = MyApp.preferences.edit();
                                editor.putBoolean(EventChatFragment.eventID, true);
                                editor.commit();
                                Toast.makeText(activity, "You subscribe this group", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        //    TextView tvTT = (TextView) v.findViewById(R.id.tvChat);
                            if (adminMsg.contains("Start a house party")) {
                                Intent ii = new Intent(activity, InviteFriendActivity.class);
                                activity.startActivity(ii);
                            } else {
                                activity.finish();
                            }
                        }
                    });
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            tvMsg.setBackgroundColor(Color.TRANSPARENT);
            linear.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.incoming_message_bg));
        }
        if(model.getAuthor().equalsIgnoreCase("Guest User")) {
         //   tvUser.setVisibility(View.GONE);
        }
    }


}
