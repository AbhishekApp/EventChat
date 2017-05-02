package com.get.wazzon;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.MyUtill;
import com.firebase.client.Firebase;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.get.wazzon.EventChatActivity.CatID;
import static com.get.wazzon.MyApp.DEEPLINK_BASE_URL;
import static com.get.wazzon.MyApp.FireBaseHousePartyChatNode;

/**
 * Created by manish on 8/3/2016.
 */
public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    Button btnShare;
    int REQUEST_INVITE = 111;
    String msg;
    TextView tvMsg;
    String userName;
    String eventID, eventCategory,eventName="",eventTime="";
    String longDeepLink = DEEPLINK_BASE_URL+"?link=$" +
            "&apn=com.get.wazzon"+
            "&afl=$"+
            "&st=" +
            "&sd=" +
            "&si="+
            "&utm_source=";
    ProgressBar pd;
    String shortLinkURL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends_activity);
        pd = (ProgressBar) findViewById(R.id.pd);

        msg = getApplicationContext().getResources().getString(R.string.share_msg);

        new GoogleApiClient.Builder(this)
        .addApi(AppInvite.API)
        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Toast.makeText(InviteFriendActivity.this, "Google Connection Failed", Toast.LENGTH_SHORT).show();
            }
        }).build();

        eventCategory = getIntent().getStringExtra("EventName");
        eventID = getIntent().getStringExtra("EventID");
        eventName  = getIntent().getStringExtra("Event");
        eventTime  = getIntent().getStringExtra("EventTime");

        eventTime = eventTime.split(" ")[0];
        init();
    }

    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setTitle("Invite Friends");
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvMsg = (TextView) findViewById(R.id.tvInviteText);
        userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
        tvMsg.setText(msg.replace("event",eventName).replace("DeepLink",""));
        btnShare = (Button) findViewById(R.id.btnInviteFriend);
        btnShare.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public class newShortAsync extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res=null;
            Log.i("InviteFriendActivity", "LongDeepURL+ "+longDeepLink);
            String json = "{\"longUrl\": \""+longDeepLink.replace("$",getResources().getString(R.string.apk_link))+"\"}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key="+getResources().getString(R.string.google_shortlink_api_key));
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

                int status=con.getResponseCode();
                InputStream inputStream;
                if(status== HttpURLConnection.HTTP_OK)
                    inputStream=con.getInputStream();
                else
                    inputStream = con.getErrorStream();

                reader= new BufferedReader(new InputStreamReader(inputStream));
                buffer= new StringBuffer();
                String line="";
                while((line=reader.readLine())!=null)
                {
                    buffer.append(line);
                }
                res= buffer.toString();
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
            String response=s;
            try {
                if(!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = new JSONObject(response);
                    String id = jsonObject.optString("id");
                    shortLinkURL = id;
                    msg = msg.replace("event", eventName).replace("DeepLink", shortLinkURL);
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_SEND);
                    intent2.setType("text/plain");
                    intent2.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent2, "Share "));
                    pd.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void onInviteClicked() {
        String userGroup = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, "");
        String SubDomain = "";
        if(FireBaseHousePartyChatNode.length()>0) {
            if ((userGroup.contains(CatID))) {
                //Toast.makeText(InviteFriendActivity.this, "userGroup already have house_party_invitations key", Toast.LENGTH_SHORT).show();
                if (FireBaseHousePartyChatNode.contains(CatID)){
                    List<String> items = Arrays.asList(FireBaseHousePartyChatNode.split(","));
                    for (int i = 0; i <items.size() ; i++) {
                        String j = items.get(i);
                        if(j.contains(CatID)){
                            SubDomain = j;
                        }
                    }
                    MyUtill.subscribeUserForEvents(SubDomain);
                    longDeepLink = longDeepLink +"invi"+SubDomain+"&utm_medium="+MyApp.getDeviveID(this)+"&utm_campaign="+eventID;
                }
                //abhishek
                if (shortLinkURL.length()<=0 ){
                    new newShortAsync().execute();
                }else{
                    if(msg.length()>0) {
                        //Intent sendIntent = new Intent(InviteFriendActivity.this, ShareEventActivity.class);
                        Intent intent2 = new Intent();
                        intent2.setAction(Intent.ACTION_SEND);
                        intent2.setType("text/plain");
                        intent2.putExtra(Intent.EXTRA_TEXT, msg );
                        startActivity(Intent.createChooser(intent2, "Share"));
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "no content", Toast.LENGTH_SHORT).show();
                    }}
            } else {
                new addRandomAlphaNumericKeytoEventNode().execute();
            }
        }
        else{
            //Toast.makeText(getApplicationContext(), "key is blank", Toast.LENGTH_SHORT).show();
            new addRandomAlphaNumericKeytoEventNode().execute();
        }
    }



    class addRandomAlphaNumericKeytoEventNode extends AsyncTask<String, Void, String> {
        public String myRandom = "";

        addRandomAlphaNumericKeytoEventNode(){
            Random random = new Random();
            myRandom = String.format("%04d", random.nextInt(10000));
        }
        protected String doInBackground(String... urls) {
            Firebase usersRef = new Firebase(MyApp.FIREBASE_BASE_URL);
            String deviceID = MyApp.getDeviveID(InviteFriendActivity.this);
            Firebase alanRef = usersRef.child("users/" + deviceID + "/0");
            Map<String, Object> nickname = new HashMap<String, Object>();
            if (FireBaseHousePartyChatNode.length()>0 ){
                if (!FireBaseHousePartyChatNode.contains(CatID)){
                    nickname.put("house_party_invitations", FireBaseHousePartyChatNode+","+myRandom + CatID);
                }else{
                    nickname.put("house_party_invitations", FireBaseHousePartyChatNode);
                }
            }else {
                nickname.put("house_party_invitations", myRandom + CatID);
            }
            alanRef.updateChildren(nickname);
            FireBaseHousePartyChatNode = nickname.get("house_party_invitations").toString();
            return "";
        }

        protected void onPostExecute(String feed) {
            //onPost
            //Toast.makeText(getApplicationContext(), "key is blank inserted :"+myRandom + CatID, Toast.LENGTH_SHORT).show();
            MyUtill.subscribeUserForEvents(myRandom+ eventCategory);
            longDeepLink = longDeepLink +"invi"+myRandom+ eventCategory+"&utm_medium="+MyApp.getDeviveID(InviteFriendActivity.this)+"&utm_campaign="+eventID;
            //abhishek
            if (shortLinkURL.length()<=0 ){
                new newShortAsync().execute();
            }else{
                if(msg.length()>0) {
                    //Intent sendIntent = new Intent(InviteFriendActivity.this, ShareEventActivity.class);
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_SEND);
                    intent2.setType("text/plain");
                    intent2.putExtra(Intent.EXTRA_TEXT, msg );
                    startActivity(Intent.createChooser(intent2, "Share"));
                }
                else{
                    Toast.makeText(getApplicationContext(), "no content", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @VisibleForTesting
    public Uri buildDeepLink(@NonNull String deepLink, int minVersion, boolean isAd) {
        // Get the unique appcode for this app.
        String appCode = getString(R.string.app_code);
        // Get this app's package name.
        String packageName = getApplicationContext().getPackageName();

        // Build the link with all required parameters
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority(appCode + ".app.goo.gl/pGuk")
                .path("/")
                .appendQueryParameter("link", deepLink.toString())
                .appendQueryParameter("apn", packageName)
                .appendQueryParameter("inviterid", MyApp.getDeviveID(this))
                .appendQueryParameter("eventid", eventID);

        // Minimum version is optional.
        if (minVersion > 0) {
            builder.appendQueryParameter("amv", Integer.toString(minVersion));
        }
        // Return the completed deep link.
        return builder.build();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //        Intent iChat = new Intent(this, SignUpActivity.class);
        //        startActivity(iChat);
        try {
            if (id == R.id.btnInviteFriend) {
                onInviteClicked();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("InviteFriendActivity", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("InviteFriendActivity", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shortLinkURL="";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        shortLinkURL="";
    }
}
