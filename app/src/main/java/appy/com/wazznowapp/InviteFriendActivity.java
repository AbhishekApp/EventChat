package appy.com.wazznowapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

/**
 * Created by admin on 8/3/2016.
 */
public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener {


    ActionBar actionBar;
    Button btnShare;
    int REQUEST_INVITE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends_activity);
        init();

        btnShare.setOnClickListener(this);

    }
    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setTitle("Invite Friends");
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        intent.setPackage("com.whatsapp");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Install WazzNow App.");
       // intent.setType("text/plain");

        //  startActivityForResult(intent, REQUEST_INVITE);
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
        //    Intent waIntent = new Intent(Intent.ACTION_SEND);
        //    waIntent.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(intent, "Choose an email client from..."));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Whatsapp couldn't open", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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
