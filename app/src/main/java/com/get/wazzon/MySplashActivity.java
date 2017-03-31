package com.get.wazzon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.app.model.MyUtill;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by manish on 8/1/2016.
 */
public class MySplashActivity extends Activity{

    Handler handler;
    String TAG="PLAYSERVICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_splash_activity);


        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
                //Toast.makeText(this,"please udpate your google play service",Toast.LENGTH_SHORT).show();
                try {
                    int v = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
                    //Toast.makeText(this,""+v,Toast.LENGTH_SHORT).show();
                    checkGooglePlayServices();
                }
                catch (PackageManager.NameNotFoundException e){
                    e.printStackTrace();
                }

            }
            else {
                //Toast.makeText(this, "please download the google play service", Toast.LENGTH_SHORT).show();
            }
        }else{
            //Toast.makeText(this,"please udpate your google play service",Toast.LENGTH_SHORT).show();
            try {
                int v = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
               // Toast.makeText(this,""+v,Toast.LENGTH_SHORT).show();
                checkGooglePlayServices();
            }
            catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
        }
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

    private boolean checkGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, GooglePlayServicesUtil.getErrorString(status));

            // ask user to update google play services.
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
            return false;
        } else {
            Log.i(TAG, GooglePlayServicesUtil.getErrorString(status));
            // google play services is updated.
            //your code goes here...
            return true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Verifies the proper version of Google Play Services exists on the device.
        if(MyUtill.checkGooglePlaySevices(this)){
            System.out.println("true");
        }else {
            System.out.println("false");
        }
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