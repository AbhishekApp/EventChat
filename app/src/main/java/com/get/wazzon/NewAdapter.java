package com.get.wazzon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.ChatData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mylist.adapters.FirebaseImageLoader;

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

import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.MyApp.DEEPLINK_BASE_URL;

/**
 * Created by manish on 2/15/2017.
 */

public class NewAdapter extends ArrayAdapter<ChatData> {
    Context con;
    ArrayList<ChatData> alList;
    RelativeLayout comRL;
    TextView tvUser,tvMsg,tvComMsg1;
    LinearLayout linear,ltvChatImg;//, linearBtn;
    RelativeLayout.LayoutParams relativeParam;
    ImageView imgIcon, imgMsg;
    ProgressBar progressBar;

    String shortLinkURL = "";
    String msg;
    String longDeepLink = DEEPLINK_BASE_URL+"?link=$" +
            "&apn=com.get.wazzon"+
            "&afl=$"+
            "&st=" +
            "&sd=" +
            "&si="+
            "&utm_source=";
    String userName = "";
    int width;

    public NewAdapter(Context context, int resource, ArrayList<ChatData> list) {
        super(context, resource, list);
        con = context;
        alList = list;
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        width = (width * 65) / 100;

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
            imgMsg = (ImageView) view.findViewById(R.id.tvChatImg);
            ltvChatImg = (LinearLayout) view.findViewById(R.id.ltvChatImg);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }else{
            imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            tvUser = (TextView) view.findViewById(R.id.tvChatUser);
            tvMsg = (TextView) view.findViewById(R.id.tvChat);
            linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
            comRL = (RelativeLayout)view.findViewById(R.id.comRL);
            tvComMsg1 = (TextView)comRL.findViewById(R.id.tvComMsg1);
            imgMsg = (ImageView) view.findViewById(R.id.tvChatImg);
            ltvChatImg = (LinearLayout) view.findViewById(R.id.ltvChatImg);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
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
        if (model.getTitle().contains("http") && !model.getTitle().contains("t.co")){
            tvComMsg1.setText(Html.fromHtml(model.getTitle()));
            tvMsg.setText(Html.fromHtml(model.getTitle()));
            //tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
            //tvMsg.setAutoLinkMask();
            Linkify.addLinks(tvMsg, Linkify.ALL);
            Linkify.addLinks(tvComMsg1, Linkify.ALL);

        }else
        {
            if(model.getMessageType().equals("image")){
                progressBar.setVisibility(View.VISIBLE);
                tvComMsg1.setVisibility(View.GONE);
                tvMsg.setVisibility(View.GONE);
                imgMsg.setVisibility(View.VISIBLE);
                ltvChatImg.setVisibility(View.VISIBLE);
                StorageReference storageRef =  FirebaseStorage.getInstance().getReference();
                StorageReference photoRef = storageRef.child(EventChatActivity.SuperCateName + "/" + eventDetail.getCategory_name()).child(model.getTitle());
//                Glide.with(con)
//                    .using(new FirebaseImageLoader())
//                    .load(photoRef)
//                    .placeholder(R.drawable.def_orig_)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(imgMsg);

                Glide.with(con)
                        .using(new FirebaseImageLoader())
                        .load(photoRef)
                        .placeholder(R.drawable.def_orig_)
                        .listener(new RequestListener() {
                            @Override
                            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .into(imgMsg);

//                Glide.with(con)
//                        .using(new FirebaseImageLoader())
//                        .load(photoRef)
//                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                Log.d("NewAdapter", "exception " + e.getMessage());
//                                progressBar.setVisibility(View.INVISIBLE);
//                                e.printStackTrace();
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                progressBar.setVisibility(View.INVISIBLE);
//                                return false;
//                            }
//                        }).into(imgMsg);

            }else {
                tvComMsg1.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.VISIBLE);
                imgMsg.setVisibility(View.GONE);
                ltvChatImg.setVisibility(View.GONE);
                tvComMsg1.setText(model.getTitle());
                tvMsg.setText(model.getTitle());
            }
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
                MyApp.PreDefinedEventAnalytics("share",eventDetail.getEvent_title(), eventID);
                longDeepLink =longDeepLink+eventID+ "&utm_medium="+MyApp.getDeviveID(con)+"&utm_campaign="+eventID;
                msg = model.getTitle();
                new newShortAsync().execute();
            }
        });

        if(model.getAuthorType().equals("com")){
            comRL.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
            LinearLayout.LayoutParams rlParam = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        //    rlParam.gravity = Gravity.CENTER_HORIZONTAL;
            tvComMsg1.setLayoutParams(rlParam);
            tvComMsg1.setPadding(40, 10, 5, 10);
            tvComMsg1.setGravity(Gravity.CENTER_VERTICAL);
        }else {
            linear.setVisibility(View.VISIBLE);
            comRL.setVisibility(View.GONE);
            if((fromUser.equals(MyApp.getDeviveID(con)))) {
                tvMsg.setTextColor(con.getResources().getColor(R.color.white));
                tvMsg.setLinkTextColor(con.getResources().getColor(R.color.white));
                tvMsg.setPadding(25,5,70,5);
                tvUser.setGravity(Gravity.RIGHT);
                tvUser.setVisibility(View.GONE);
                linear.setGravity(Gravity.RIGHT);
                relativeParam.addRule(Gravity.CENTER);
                relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeParam.setMargins(0,5,40,5);
                linear.setLayoutParams(relativeParam);
                if(!model.getMessageType().equals("image")) {
                    linear.setBackgroundResource(R.drawable.chat_out);
                }else{
                    ltvChatImg.setBackgroundResource(R.drawable.chat_outgoing_background);
                    ltvChatImg.setPadding(10,5,30,5);
                    LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imgParam.setMargins(4,4,20,4);
                    imgMsg.setPadding(10,5,50,5);
                    linear.setBackgroundResource(android.R.color.transparent);
                }
            }
            else{
                tvMsg.setGravity(Gravity.LEFT);
                tvMsg.setLinkTextColor(Color.parseColor("#8884f6"));
                tvMsg.setPadding(40,5,10,5);
                tvMsg.setTextColor(con.getResources().getColor(R.color.chat_text_color));
                tvUser.setGravity(Gravity.LEFT);

                if(tvUser.getText().toString().length()>0){
                    tvUser.setPadding(35,5,10,5);
                    tvUser.setVisibility(View.VISIBLE);
                }else{
                    tvUser.setVisibility(View.GONE);
                }

                if(!model.getMessageType().equals("image")) {
                    linear.setBackgroundResource(R.drawable.incoming_message_bg);
                    relativeParam.addRule(Gravity.LEFT);
                    linear.setGravity(Gravity.LEFT);
                    relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    relativeParam.addRule(RelativeLayout.RIGHT_OF,R.id.imgIcon);
                    relativeParam.setMargins(30,5,0,5);
                    linear.setLayoutParams(relativeParam);
                }else{
                    ltvChatImg.setBackgroundResource(R.drawable.chat_incomin_background);
                    ltvChatImg.setPadding(20,5,10,5);
                    LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imgParam.setMargins(20,4,4,4);
                    imgMsg.setPadding(50,5,10,5);
                    linear.setBackgroundResource(android.R.color.transparent);
                }
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
            ChatStadiumFragment.pd.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res = null;
            Log.i("NewAdapter", "LongDeepURL+ "+longDeepLink);
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
                e.printStackTrace();// for now eat exceptions
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

                msg = msg +" "+shortLinkURL;
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
