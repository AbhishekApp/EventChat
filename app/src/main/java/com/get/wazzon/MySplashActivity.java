package com.get.wazzon;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by manish on 8/1/2016.
 */
public class MySplashActivity extends Activity{

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_splash_activity);

        //System.out.println("HASHKEY:::::::: "+getHashKey(getPackageName(),MySplashActivity.this));


        handler = new Handler();
        handler.postDelayed(runn, 3 * 1000);
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
    super.onBackPressed();
    }



    public static String getHashKey(String packageName, Context context) {
        String str = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName.trim(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                str = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("Hask Key Value=="+str);

        return str;
    }
}