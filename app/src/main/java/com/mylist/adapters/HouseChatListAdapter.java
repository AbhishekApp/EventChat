package com.mylist.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.ChatData;
import com.firebase.client.Query;

import appy.com.wazznowapp.EventChatActivity;
import appy.com.wazznowapp.InviteFriendActivity;
import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.R;

import static appy.com.wazznowapp.EventChatActivity.eventDetail;

/**
 * Created by admin on 8/11/2016.
 */
public class HouseChatListAdapter extends FirebaseListAdapter<ChatData> {

    Activity activity;
    TextView tvUser;
    TextView tvMsg;
    TextView btnYes, btnNo;
    LinearLayout linear;//, linearBtn;
    RelativeLayout.LayoutParams relativeParam;
    ImageView imgIcon;
    RelativeLayout comRL;
    TextView tvComMsg1;

    public HouseChatListAdapter(Query ref, Activity activity, int layout){
        super(ref, ChatData.class, layout, activity);
        this.activity = activity;
    }

    private void housePartyStarted(String message){
        //editor = MyApp.preferences.edit();
        //editor.putBoolean(EventChatActivity.eventID + "HouseParty", true);
        //editor.commit();
        Intent ii = new Intent(activity, InviteFriendActivity.class);
        ii.putExtra("EventName", eventDetail.getCatergory_id());
        ii.putExtra("EventID", eventDetail.getEvent_id());
        ii.putExtra("Event", eventDetail.getEvent_title());
        ii.putExtra("message", message);
        ii.putExtra("EventTime", eventDetail.getEvent_start());
        activity.startActivity(ii);
    }



    @Override
    protected void populateView(final View v,final ChatData model, int position) {
        comRL = (RelativeLayout)v.findViewById(R.id.comRL);
        tvComMsg1 = (TextView)comRL.findViewById(R.id.tvComMsg1);
        imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
        tvUser = (TextView) v.findViewById(R.id.tvChatUser);
        tvMsg = (TextView) v.findViewById(R.id.tvChat);
        linear = (LinearLayout) v.findViewById(R.id.linearMsgChat);
        //linearBtn = (LinearLayout) v.findViewById(linearBtn);

        tvMsg.setText(model.getTitle());
        tvUser.setText(model.getAuthor());
        tvComMsg1.setText(model.getTitle());
        //linearBtn.setVisibility(View.GONE);..



        ImageView share = (ImageView) v.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PreDefinedEventAnalytics("share",eventDetail.getEvent_title(), EventChatActivity.eventID);
                //openShareScreen

                housePartyStarted(model.getTitle());


            }
        });

        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        String sender = model.getAuthor();
        String fromUser = model.getToUser();
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        boolean isEqual = sender.equalsIgnoreCase(userName);

        if(model.getAuthorType().equals("com")){
            //System.out.println("commmmenttttttaaaatooorrrr");
            comRL.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
        }
        else{
            linear.setVisibility(View.VISIBLE);
            comRL.setVisibility(View.GONE);
            if((fromUser.equals(MyApp.getDeviveID(activity)))) {
//                tvMsg.setGravity(Gravity.RIGHT);
                tvMsg.setTextColor(activity.getResources().getColor(R.color.white));
                tvMsg.setPadding(25,15,70,15);
                tvUser.setGravity(Gravity.RIGHT);
                tvUser.setVisibility(View.GONE);

                linear.setGravity(Gravity.RIGHT);
                relativeParam.addRule(Gravity.CENTER);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeParam.setMargins(0,5,105,5);
                linear.setLayoutParams(relativeParam);
//              linear.setBackgroundResource(R.drawable.chat_outgoing_background);
                linear.setBackgroundResource(R.drawable.outgoing_message_bg);
                //linearBtn.setVisibility(View.GONE);

            }
            else{
                tvMsg.setGravity(Gravity.LEFT);
                tvMsg.setPadding(35,5,10,15);
                tvMsg.setTextColor(activity.getResources().getColor(R.color.chat_text_color));
                tvUser.setGravity(Gravity.LEFT);
                tvUser.setVisibility(View.VISIBLE);
                tvUser.setPadding(35,5,10,5);
                relativeParam.addRule(Gravity.LEFT);
                linear.setGravity(Gravity.LEFT);

                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeParam.setMargins(105,5,0,5);
                linear.setLayoutParams(relativeParam);
//            linear.setBackgroundResource(R.drawable.chat_incomin_background);
                linear.setBackgroundResource(R.drawable.incoming_message_bg);
                linear.setPadding(35,5,80,5);
                //linearBtn.setVisibility(View.GONE);
            }

            if(model.getAuthor().equalsIgnoreCase("Admin")) {
                imgIcon.setVisibility(View.VISIBLE);

            }else{
                imgIcon.setVisibility(View.GONE);
                //tvMsg.setBackgroundColor(Color.TRANSPARENT);

            }
            if(model.getAuthor().equalsIgnoreCase("Guest User")) {
                //   tvUser.setVisibility(View.GONE);
            }
        }

    }
}
