package com.app.model;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import appy.com.wazznowapp.R;

/**
 * Created by admin on 2/2/2017.
 */

public class DeviceInfoActivity extends Activity {

    public static ProgressBar progressBar;
    public static TextView tv;
    TextView tv1;
    String s="";
    String s1="";
    String IMEI,IMSI;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.deviceinfo);
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        tv= (TextView)findViewById(R.id.tv);
        tv1= (TextView)findViewById(R.id.splash) ;
        new FetchInfoTask().execute();
    }

    private class FetchInfoTask extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Long doInBackground(URL... urls) {

            s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += "\n OS API Level: "+android.os.Build.VERSION.RELEASE + "("+android.os.Build.VERSION.SDK_INT+")";
            s += "\n Device: " + android.os.Build.DEVICE;
            s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";

            String serviceName = Context.TELEPHONY_SERVICE;
            TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
            IMEI = m_telephonyManager.getDeviceId();
            IMSI = m_telephonyManager.getSubscriberId();

            s1="\n \nDEVICE_VERSION: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_VERSION )+"\n"+
                    "DEVICE_CURRENT_DATE_TIME: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_CURRENT_DATE_TIME )+"\n"+
                    "DEVICE_CURRENT_DATE_TIME_ZERO_GMT: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_CURRENT_DATE_TIME_ZERO_GMT )+"\n"+
                    "DEVICE_CURRENT_YEAR: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_CURRENT_YEAR )+"\n"+
                    "DEVICE_FREE_MEMORY: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_FREE_MEMORY )+"\n"+
                    "DEVICE_HARDWARE_MODEL: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_HARDWARE_MODEL )+"\n"+
                    "DEVICE_IN_INCH: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_IN_INCH )+"\n"+
                    "DEVICE_IP_ADDRESS_IPV4: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_IP_ADDRESS_IPV4 )+"\n"+
                    "DEVICE_IP_ADDRESS_IPV6: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_IP_ADDRESS_IPV6 )+"\n"+
                    "DEVICE_LOCAL_COUNTRY_CODE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_LOCAL_COUNTRY_CODE )+"\n"+
                    "DEVICE_LOCALE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_LOCALE )+"\n"+
                    "DEVICE_MAC_ADDRESS: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_MAC_ADDRESS )+"\n"+
                    "DEVICE_MANUFACTURE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_MANUFACTURE )+"\n"+
                    "DEVICE_LANGUAGE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_LANGUAGE )+"\n"+
                    "DEVICE_TIME_ZONE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_TIME_ZONE )+"\n"+
                    "CONTACT_ID: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.CONTACT_ID )+"\n"+
                    "IPHONE_TYPE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.IPHONE_TYPE )+"\n"+
                    "DEVICE_USED_MEMORY: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_USED_MEMORY )+"\n"+
                    "DEVICE_TOKEN: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_TOKEN )+"\n"+
                    "DEVICE_TOTAL_CPU_USAGE_SYSTEM: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_TOTAL_CPU_USAGE_SYSTEM )+"\n"+
                    "DEVICE_TOTAL_CPU_USAGE: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_TOTAL_CPU_USAGE )+"\n"+
                    "DEVICE_NUMBER_OF_PROCESSORS: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_NUMBER_OF_PROCESSORS )+"\n"+
                    "DEVICE_TOTAL_MEMORY: "+DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_TOTAL_MEMORY )+
                     DeviceInfo.getDeviceInfo(DeviceInfoActivity.this,Device.DEVICE_NETWORK_TYPE);
            return 2L;
        }

        @Override
        protected void onPostExecute(Long result) {
            tv1.setText(s+"\n IMEI: "+IMEI);
            tv.setText(s1);
            progressBar.setVisibility(View.GONE);
        }
    }
}
