package appy.com.wazznowapp;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.ChatData;
import com.logger.Log;

import java.util.ArrayList;

import static appy.com.wazznowapp.EventChatActivity.eventDetail;
import static appy.com.wazznowapp.MyApp.StadiumMsgLimit;

/**
 * Created by admin on 2/15/2017.
 */

public class NewAdapter extends ArrayAdapter<ChatData> {
    Context con;
    ArrayList<ChatData> alList;
    RelativeLayout comRL;
    TextView tvUser,tvMsg,tvComMsg1;
    LinearLayout linear;//, linearBtn;
    RelativeLayout.LayoutParams relativeParam;
    ImageView imgIcon;

    public NewAdapter(Context context, int resource, ArrayList<ChatData> list) {
        super(context, resource, list);
        con = context;
        alList = list;
        Log.i("NewAdapterRAvi",""+list.size());
        for(int i=0;i<list.size();i++){
            Log.i("NewAdapterRAvi",""+list.get(i).getTitle());
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(con.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_layout, null);
            comRL = (RelativeLayout)view.findViewById(R.id.comRL);
            tvComMsg1 = (TextView)comRL.findViewById(R.id.tvComMsg1);
            imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            tvUser = (TextView) view.findViewById(R.id.tvChatUser);
            tvMsg = (TextView) view.findViewById(R.id.tvChat);
            linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
        }else{
            imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            tvUser = (TextView) view.findViewById(R.id.tvChatUser);
            tvMsg = (TextView) view.findViewById(R.id.tvChat);
            linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
            comRL = (RelativeLayout)view.findViewById(R.id.comRL);
            tvComMsg1 = (TextView)comRL.findViewById(R.id.tvComMsg1);
        }


        ImageView share = (ImageView) view.findViewById(R.id.share);
        if(!share.isShown())
            share.setVisibility(View.VISIBLE);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PreDefinedEventAnalytics("share",eventDetail.getEvent_title(), EventChatActivity.eventID);
            }
        });

        if(position < alList.size() || StadiumMsgLimit < alList.size()) {
            try {
                ChatData model = alList.get(position);
                populateView(view, model);
            }
            catch (Exception e){
                e.printStackTrace(); //eats exceptions for now
            }
        }
        return view;
    }


    protected void populateView(final View v, ChatData model) {
        tvUser.setTypeface(MyApp.authorFont);
        tvMsg.setTypeface(MyApp.authorMsg);
        tvComMsg1.setText(model.getTitle());
        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        String sender = model.getAuthor();
        String fromUser = model.getToUser();
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        boolean isEqual = sender.equalsIgnoreCase(userName);
        tvMsg.setText(model.getTitle());
        tvUser.setText(model.getAuthor());

        if(model.getAuthorType().equals("com")){
            comRL.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
        }else {
            linear.setVisibility(View.VISIBLE);
            comRL.setVisibility(View.GONE);
            if((fromUser.equals(MyApp.getDeviveID(con)))) {
                tvMsg.setTextColor(con.getResources().getColor(R.color.white));
                tvMsg.setPadding(25,15,70,15);
                tvUser.setGravity(Gravity.RIGHT);
                tvUser.setVisibility(View.GONE);
                linear.setGravity(Gravity.RIGHT);
                relativeParam.addRule(Gravity.CENTER);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeParam.setMargins(0,5,105,5);
                linear.setLayoutParams(relativeParam);
                linear.setBackgroundResource(R.drawable.outgoing_message_bg);
            }
            else{
                tvMsg.setGravity(Gravity.LEFT);
                tvMsg.setPadding(35,5,10,15);
                tvMsg.setTextColor(con.getResources().getColor(R.color.chat_text_color));
                tvUser.setGravity(Gravity.LEFT);
                tvUser.setVisibility(View.VISIBLE);
                tvUser.setPadding(35,5,10,5);
                relativeParam.addRule(Gravity.LEFT);
                linear.setGravity(Gravity.LEFT);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeParam.setMargins(105,5,0,5);
                linear.setLayoutParams(relativeParam);
                linear.setBackgroundResource(R.drawable.chat_new);
                linear.setPadding(35,5,80,5);
            }
            if(model.getAuthor().equalsIgnoreCase("Admin")) {
                imgIcon.setVisibility(View.VISIBLE);
            }else{
                imgIcon.setVisibility(View.GONE);
            }
        }
    }
}
