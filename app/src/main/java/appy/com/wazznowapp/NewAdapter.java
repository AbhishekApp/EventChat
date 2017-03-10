package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.util.Linkify;
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
import java.util.ArrayList;

import static appy.com.wazznowapp.EventChatActivity.eventDetail;
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
    String shortLinkURL = "";
    String msg;
    String longDeepLink = "https://ry5a4.app.goo.gl/?link=$" +
            "&apn=appy.com.wazznowapp"+
            "&afl=$"+
            "&st=WazzNow+Title" +
            "&sd=House+Party+Chat+Invitation" +
            "&si=http://media.appypie.com/appypie-slider-video/images/logo_new.png"+
            "&utm_source=";
    String userName = "";

    public NewAdapter(Context context, int resource, ArrayList<ChatData> list) {
        super(context, resource, list);
        con = context;
        alList = list;
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



        if(position < alList.size() ) {
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


    protected void populateView(final View v, final ChatData model) {
        tvUser.setTypeface(MyApp.authorFont);
        tvMsg.setTypeface(MyApp.authorMsg);


        if (model.getTitle().contains("http")){
            tvComMsg1.setText(Html.fromHtml(model.getTitle()));
            tvMsg.setText(Html.fromHtml(model.getTitle()));
            //tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
            //tvMsg.setAutoLinkMask();
            Linkify.addLinks(tvMsg, Linkify.ALL);
            Linkify.addLinks(tvComMsg1, Linkify.ALL);

        }else
        {
            tvComMsg1.setText(model.getTitle());
            tvMsg.setText(model.getTitle());

        }

        relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        String sender = model.getAuthor();
        String fromUser = model.getToUser();
        String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        boolean isEqual = sender.equalsIgnoreCase(userName);


        if (model.getAuthor().equals("Guest User"))
        {
            //tvUser.setText("");
            tvUser.setVisibility(View.GONE);
        }else {
            tvUser.setText(model.getAuthor());
            tvUser.setVisibility(View.VISIBLE);
        }

        ImageView share = (ImageView) v.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PreDefinedEventAnalytics("share",eventDetail.getEvent_title(), EventChatActivity.eventID);
                //openShareScreen

/*                housePartyStarted(model.getTitle());*/
                longDeepLink =longDeepLink+ "&utm_medium="+model.getTitle();
                msg = model.getTitle();
                new newShortAsync().execute();

            }
        });


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
                relativeParam.setMargins(0,5,40,5);
                linear.setLayoutParams(relativeParam);
                linear.setBackgroundResource(R.drawable.chat_out);
            }
            else{
                tvMsg.setGravity(Gravity.LEFT);
                tvMsg.setPadding(40,5,10,15);
                tvMsg.setTextColor(con.getResources().getColor(R.color.chat_text_color));
                tvUser.setGravity(Gravity.LEFT);

                if(tvUser.getText().toString().length()>0){
                    tvUser.setPadding(35,5,10,5);
                    tvUser.setVisibility(View.VISIBLE);
                }else{

                }

                relativeParam.addRule(Gravity.LEFT);
                linear.setGravity(Gravity.LEFT);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeParam.setMargins(40,5,0,5);
                linear.setLayoutParams(relativeParam);
                linear.setBackgroundResource(R.drawable.incoming_message_bg);
                //linear.setPadding(35,5,80,5);
            }
            if(model.getAuthor().equalsIgnoreCase("Admin")) {
                imgIcon.setVisibility(View.VISIBLE);
            }else{
                imgIcon.setVisibility(View.GONE);
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
            ChatStadiumFragment.pd.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res = null;
            String json = "{\"longUrl\": \"" + longDeepLink.replace("$", con.getResources().getString(R.string.apk_link)) + "\"}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key=" + con.getResources().getString(R.string.google_shortlink_api_key));
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
                con.startActivity(Intent.createChooser(intent2, "Share "));

                ChatStadiumFragment.pd.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
