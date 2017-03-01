package com.mylist.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.ChatData;
import com.firebase.client.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import appy.com.wazznowapp.EventChatActivity;
import appy.com.wazznowapp.MyApp;
import appy.com.wazznowapp.R;

import static appy.com.wazznowapp.EventChatActivity.eventDetail;
import static appy.com.wazznowapp.HousePartyFragment.pdh;

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
    String longDeepLink = "https://ry5a4.app.goo.gl/?link=$" +
            "&apn=appy.com.wazznowapp"+
            "&afl=$"+
            "&st=WazzNow+Title" +
            "&sd=House+Party+Chat+Invitation" +
            "&si=http://media.appypie.com/appypie-slider-video/images/logo_new.png"+
            "&utm_source=";

    String shortLinkURL = "";
    String msg ="";

    public HouseChatListAdapter(Query ref, Activity activity, int layout){
        super(ref, ChatData.class, layout, activity);
        this.activity = activity;
    }

    /*private void housePartyStarted(String message){
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
    }*/




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

                /*housePartyStarted(model.getTitle());*/

                longDeepLink =longDeepLink+ "&utm_medium="+model.getTitle();
                msg = model.getTitle();
                new newShortAsync().execute();


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
                linear.setBackgroundResource(R.drawable.chat_out);
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


    public class newShortAsync extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd = new android.widget.ProgressBar(InviteFriendActivity.this,null,android.R.attr.progressBarStyleLarge);
            //pd.getIndeterminateDrawable().setColorFilter(0xFFFF0000,android.graphics.PorterDuff.Mode.MULTIPLY);
            //pd.setCancelable(false);
            pdh.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res = null;
            String json = "{\"longUrl\": \"" + longDeepLink.replace("$", activity.getResources().getString(R.string.apk_link)) + "\"}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key=" + activity.getResources().getString(R.string.google_shortlink_api_key));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(40000);
                con.setConnectTimeout(40000);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.flush();
                writer.close();
                os.close();
                int status = con.getResponseCode();
                InputStream inputStream;
                if (status == HttpURLConnection.HTTP_OK)
                    inputStream = con.getInputStream();
                else
                    inputStream = con.getErrorStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                res = buffer.toString();
            } catch (MalformedURLException e) {
                //e.printStackTrace();// for now eat exceptions
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //System.out.println("JSON RESP:" + s);
            String response = s;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String id = jsonObject.getString("id");
                shortLinkURL = id;
                /*Intent sendIntent = new Intent(con, ShareEventActivity.class);
                userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
                if (!TextUtils.isEmpty(userName)) {
                    if (!userName.contains("user")) {
                        msg = "Hi, This is " + userName + ". Watch the " + id + " with me right here on WazzNow.";
                    } else {
                        msg = "Hi,  Watch the " + id + " with me right here on WazzNow.";
                    }
                } else {
                    msg = "Hi,  Watch the " + id + " with me right here on WazzNow.";
                }
                //Uri uri = buildDeepLink("http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk", 2, true);
                //  String dLink = longDeepLink.replace("SenderID", eventID);
                //sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra("share", msg);
                *//*sendIntent.setType("text/plain");*//*
                //sendIntent.setPackage("com.whatsapp");
                try {
                    con.startActivity(sendIntent);
                    //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                } catch (Exception ex) {
                    Toast.makeText(con, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                }*/

                //msg =msg.replace("event",eventDetail.getEvent_title()).replace("DeepLink",shortLinkURL);
                msg = msg +" "+shortLinkURL;

                //Uri uri = buildDeepLink("http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk", 2, true);
                //  String dLink = longDeepLink.replace("SenderID", eventID);
                //sendIntent.setAction(Intent.ACTION_SEND);
                // sendIntent.putExtra("share", msg);
                /*sendIntent.setType("text/plain");*/
                //sendIntent.setPackage("com.whatsapp");

                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, msg );
                activity.startActivity(Intent.createChooser(intent2, "Share "));

                pdh.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
