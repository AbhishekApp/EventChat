package appy.com.wazznowapp;

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
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by admin on 8/3/2016.
 */
public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    Button btnShare;
    int REQUEST_INVITE = 111;
    String msg;
    TextView tvMsg;
    String userName;
    String eventID, eventCategory;
    String longDeepLink = "https://ry5a4.app.goo.gl/?link=$" +
            "&apn=appy.com.wazznowapp"+
            "&afl=$"+
            "&st=WazzNow+Title" +
            "&sd=House+Party+Chat+Invitation" +
            "&si=http://media.appypie.com/appypie-slider-video/images/logo_new.png"+
            "&utm_source=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends_activity);
        new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(InviteFriendActivity.this, "Google Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        init();
        eventCategory = getIntent().getStringExtra("EventName");
        eventID = getIntent().getStringExtra("EventID");
      //http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk?utm_source=STR123&utm_medium=Whatsapp&utm_campaign=RN123
        longDeepLink = longDeepLink + eventID+"&utm_medium=Whatsapp&utm_campaign="+eventCategory;
        btnShare.setOnClickListener(this);

    }
    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setTitle("Invite Friends");
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvMsg = (TextView) findViewById(R.id.tvInviteText);
        userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
        if(!TextUtils.isEmpty(userName)){
            if(!userName.contains("user")){
                tvMsg.setText("Hi..! This is "+ userName +" lets watch Mumbai vs Pune match on June 21 on Pune together");
            }else{
                tvMsg.setText("Hi..! lets watch Mumbai vs Pune match on June 21 on Pune together");
            }
        }else{
            tvMsg.setText("Hi..! lets watch Mumbai vs Pune match on June 21 on Pune together");
        }
        btnShare = (Button) findViewById(R.id.btnInviteFriend);

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
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res=null;
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
                e.printStackTrace();
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
            System.out.println("JSON RESP:" + s);
            String response=s;
            try {
                JSONObject jsonObject=new JSONObject(response);
                String id=jsonObject.getString("id");
                Intent sendIntent = new Intent();
                if(!TextUtils.isEmpty(userName)){
                    if(!userName.contains("user")){
                        msg = "Hi, This is "+userName+". Watch the " + id+" with me right here on WazzNow.";
                    }else{
                        msg = "Hi,  Watch the " + id+" with me right here on WazzNow.";
                    }
                }else{
                    msg = "Hi,  Watch the " + id+" with me right here on WazzNow.";
                }
                //Uri uri = buildDeepLink("http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk", 2, true);
                //  String dLink = longDeepLink.replace("SenderID", eventID);
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                //sendIntent.setPackage("com.whatsapp");
                try{
                    startActivityForResult(sendIntent, REQUEST_INVITE);
                }catch (Exception ex){
                    Toast.makeText(getBaseContext(), "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    private void onInviteClicked() {
        //abhishek
       /* try{
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
            intent.putExtra("eventid", eventID);
            Intent sendIntent = new Intent();
            sendIntent .setPackage("com.whatsapp");
            sendIntent.putExtra("intent", intent);
            startActivityForResult(sendIntent, REQUEST_INVITE);
        }catch (Exception ex){
            Log.d("InviteFriendActivity", "onInviteClicked: Exception" + ex.toString());
        }*/

        new newShortAsync().execute();

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
        onInviteClicked();
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
                // ...
            }
        }
    }

}
