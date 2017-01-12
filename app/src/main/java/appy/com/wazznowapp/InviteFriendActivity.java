package appy.com.wazznowapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.google.android.gms.appinvite.AppInviteInvitation;

import org.w3c.dom.Text;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends_activity);
        init();
        eventCategory = getIntent().getStringExtra("EventName");
        eventID = getIntent().getStringExtra("EventID");
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
//        menuInflater.inflate(R.menu.main_menu, menu);
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

    private void onInviteClicked() {
        Intent sendIntent = new Intent();

        if(!TextUtils.isEmpty(userName)){
            if(!userName.contains("user")){
                 msg = "Hi, This is "+userName+". Watch the " + getString(R.string.invitation_deep_link)+" with me right here on WazzNow.";
            }else{
                msg = "Hi,  Watch the " + getString(R.string.invitation_deep_link)+" with me right here on WazzNow.";
            }
        }else{
            msg = "Hi,  Watch the " + getString(R.string.invitation_deep_link)+" with me right here on WazzNow.";
        }
        Uri uri = buildDeepLink("http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk", 2, true);

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        try{
            startActivityForResult(sendIntent, REQUEST_INVITE);
        }catch (Exception ex){
            Toast.makeText(this, "Whatsapp couldn't open", Toast.LENGTH_SHORT).show();
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
                .appendQueryParameter("eventid", eventID);

        // If the deep link is used in an advertisement, this value must be set to 1.
        if (isAd) {
            builder.appendQueryParameter("ad", "1");
        }

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
