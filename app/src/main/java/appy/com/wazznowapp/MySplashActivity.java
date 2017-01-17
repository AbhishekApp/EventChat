package appy.com.wazznowapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by admin on 8/1/2016.
 */
public class MySplashActivity extends Activity{

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_splash_activity);

        handler = new Handler();
        handler.postDelayed(runn, 5 * 1000);
    }

    Runnable runn = new Runnable() {
        @Override
        public void run() {
            finish();
            overridePendingTransition(0, R.anim.fade_out);
        }
    };

    @Override
    public void onBackPressed() {
    //super.onBackPressed();
    }

}