package appy.com.wazznowapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by admin on 8/3/2016.
 */
public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener {


    ActionBar actionBar;
    Button btnShare;

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
        menuInflater.inflate(R.menu.main_menu, menu);
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

    @Override
    public void onClick(View v) {
        Intent iChat = new Intent(this, SignUpActivity.class);
        startActivity(iChat);
    }
}
