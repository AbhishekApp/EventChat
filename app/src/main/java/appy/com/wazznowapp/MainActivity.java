package appy.com.wazznowapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mylist.adapters.AdapterMainFirst;
import com.mylist.adapters.MainData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView listMain;
    LinearLayout linearMain;
    ArrayList<MainData> al;
    private static boolean firstFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!firstFlag) {
            firstFlag = true;
            Intent ii = new Intent(this, MySplashActivity.class);
            startActivity(ii);
        }
        init();
        al = new ArrayList<MainData>();
        AdapterMainFirst adapter = new AdapterMainFirst(this, al);
        listMain.setAdapter(adapter);

        listMain.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent iChat = new Intent(this, InviteFriendActivity.class);
        startActivity(iChat);
    }

    private void init(){
         listMain = (ListView) findViewById(R.id.listMain);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firstFlag = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firstFlag = false;
    }
}
