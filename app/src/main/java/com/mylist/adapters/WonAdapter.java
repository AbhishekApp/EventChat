package com.mylist.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.WonData;
import com.get.wazzon.MyApp;
import com.get.wazzon.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.ArrayList;

import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.HousePartyFragment.pdh;
import static com.get.wazzon.MyApp.DEEPLINK_BASE_URL;

/**
 * Created by admin on 4/28/2017.
 */

public class WonAdapter extends BaseAdapter {

    Activity con;
    ArrayList<WonData> alList;
    ViewHolder viewHolder;
    int width;
    String longDeepLink = DEEPLINK_BASE_URL+"?link=$" +
            "&apn=com.get.wazzon"+
            "&afl=$"+
            "&st=" +
            "&sd=" +
            "&si="+
            "&utm_source=";

    String shortLinkURL = "";
    String msg;
    ProgressDialog progressDialog;

    public WonAdapter(Activity context, ArrayList<WonData> arrayList){
        con = context;
        alList = arrayList;
        viewHolder = new ViewHolder();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        width = width * 85 / 100;
    }

    @Override
    public int getCount() {
        return alList.size();
    }

    @Override
    public Object getItem(int position) {
        return alList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.won_row, null);
            viewHolder.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeWonRow);
            viewHolder.tvWonRow = (TextView) view.findViewById(R.id.tvWonMsg);
            viewHolder.imgShare = (ImageView) view.findViewById(R.id.imgWonShare);
            viewHolder.tvWonTime = (TextView) view.findViewById(R.id.tvWonMsgTime);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        viewHolder.relativeLayout.setPadding(3,3,3,3);
        viewHolder.relativeLayout.setLayoutParams(param);

        viewHolder.tvWonRow.setText(alList.get(position).getDesc());
        viewHolder.tvWonTime.setText(alList.get(position).getTimeStamp());

        viewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PreDefinedEventAnalytics("share",eventDetail.getEvent_title(), eventID);
                longDeepLink =longDeepLink+ "&utm_medium="+MyApp.getDeviveID(con)+"&utm_campaign="+eventID;
                msg = alList.get(position).getDesc();
                new NewShortAsync().execute();
            }
        });

        return view;
    }

    class ViewHolder{
        RelativeLayout relativeLayout;
        TextView tvWonRow;
        TextView tvWonTime;
        ImageView imgShare;
    }


    public class NewShortAsync extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(con);
            progressDialog.setTitle("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res = null;
            String json = "{\"longUrl\": \"" + longDeepLink.replace("$", con.getResources().getString(R.string.apk_link)) + "\"}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key=" + con.getResources().getString(R.string.google_shortlink_api_key));
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
                int status = con.getResponseCode();
                InputStream inputStream;
                if (status == HttpURLConnection.HTTP_OK)
                    inputStream = con.getInputStream();
                else
                    inputStream = con.getErrorStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                res = buffer.toString();
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
            progressDialog.hide();
            progressDialog.cancel();
            String response = s;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String id = jsonObject.getString("id");
                shortLinkURL = id;

                //msg =msg.replace("event",eventDetail.getEvent_title()).replace("DeepLink",shortLinkURL);
                msg = msg +" "+shortLinkURL;

                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, msg );
                con.startActivity(Intent.createChooser(intent2, "Share "));

                pdh.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}


