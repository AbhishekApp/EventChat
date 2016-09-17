package appy.com.wazznowapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.EventData;
import com.app.model.EventDetail;
import com.app.model.EventModel;
import com.firebase.client.Firebase;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.mylist.adapters.EventAdapter;
import com.mylist.adapters.EventModelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView listMain;
    ArrayList<EventData> al;
    private static boolean firstFlag = false;
    GoogleApiClient mGoogleApiClient;
    EventAdapter adapter;
    Firebase firebase;
   // Firebase alanRef;
    ArrayList<EventModel> alModel;
    ArrayList<EventDetail> arrayListEvent;
    EventModelAdapter eventAdapter;
    Map<String, String> alanisawesomeMap;

    private String firebaseURL = MyApp.FIREBASE_BASE_URL;
    String eventURL = "https://wazznow-cd155.firebaseio.com/EventList.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Google Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d("MainActivity", "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });

        if(!firstFlag) {
            firstFlag = true;
            Intent ii = new Intent(this, MySplashActivity.class);
            startActivity(ii);
            init();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent iChat = new Intent(this, EventChatFragment.class);
        TextView eventName = (TextView) view.findViewById(R.id.tvCatRow);
        iChat.putExtra("CateName", eventName.getText().toString());
        startActivity(iChat);
    }

    private void init(){
        firebase = new Firebase(firebaseURL);
        // alanRef = firebase.child("EventList/Cricket");
        listMain = (ListView) findViewById(R.id.listMainEvent);
        al = new ArrayList<EventData>();
        alModel = new ArrayList<EventModel>();
        arrayListEvent = new ArrayList<EventDetail>();
        eventAdapter = new EventModelAdapter(this, arrayListEvent);
        listMain.setAdapter(eventAdapter);
        // adapter = new AdapterMainFirst(this, al);
        //   addUserDetail();

        EventTask task = new EventTask();
        task.execute();
       /* Firebase usersRef = firebase.child("users");//.child(""+length);
        final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
      //  length++;
        System.out.println("USER List length : " + 80);
        users.put(" "+ 10, alanisawesomeMap);

        usersRef.setValue(users);*/

      /*  EventData data = new EventData("Cricket", "IPL");
        alanRef.setValue(data);
        data = new EventData("Tennis", "Wimblondon");
        alanRef.setValue(data);
        data = new EventData("Football", "DLADSFJ");
        alanRef.setValue(data);*/

     /*  Map<String, String> alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("event_category", "IPL");
        alanisawesomeMap.put("event_title", "MI vs XXR");
        alanisawesomeMap.put("event_meta", "Match 25, MC stadium, Bangalore");
        Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
        users.put("User1 "+MyApp.getDeviveID(this), alanisawesomeMap);*/
    /*    final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
        //  length++;
        System.out.println("USER List length : " + 80);
        users.put(" "+ 10, alanisawesomeMap);
        firebase.child("users").setValue(users);*/

     /*    alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("event_category", "Ranji");
        alanisawesomeMap.put("event_title", "Delhi vs Punjab");
        alanisawesomeMap.put("event_meta", "Match 21, MC stadium, Bangalore");
        users.put("RANJI", alanisawesomeMap);
        alanRef.setValue(users);*/

       /* Firebase usersRef = firebase.child("users");
        Map<String, String> alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("birthYear", "1912");
        alanisawesomeMap.put("fullName", "Alan Turing");
        Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
        users.put("alanisawesome0", alanisawesomeMap);
        usersRef.setValue(users);

        alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("birthYear", "1888");
        alanisawesomeMap.put("fullName", "Alan Mishra");

        users.put("alanisawesome1", alanisawesomeMap);
        usersRef.setValue(users);

        alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("birthYear", "1975");
        alanisawesomeMap.put("fullName", "Amitabh");

        users.put("alanisawesome2", alanisawesomeMap);
        usersRef.setValue(users);*/

      /*  firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                JSONArray jsonArray = new JSONArray();
                int k = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    System.out.println("There are KEY : " + postSnapshot.getKey() + " K= "+k);
                    System.out.println("There are Value : " + postSnapshot.getValue() + " ");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(postSnapshot.getKey(), postSnapshot.getValue());
                        jsonArray.put(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    k++;
                }
                for(int i=0; i < jsonArray.length() ; i++){
                    try {
                        EventData post = new EventData();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        post.setEvent_super_cate_name(jsonObject.optString("event_super_cate_name"));
                        post.setEvent_cate_name(jsonObject.optString("event_cate_name"));
                        al.add(post);
                        adapter.notifyDataSetChanged();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
       // adapter = new EventAdapter(alanRef.limit(50), this, R.layout.main_row, "ABHI");
       // listMain.setAdapter(adapter);
        listMain.setOnItemClickListener(this);
        addUserDetail();
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
        if(id == R.id.menu_signup){
            Intent ii = new Intent(this, SignUpActivity.class);
            startActivity(ii);
        }else if(id == R.id.menu_info){
            Intent ii = new Intent(this, InfoActivity.class);
            startActivity(ii);
        }else if(id == R.id.menu_more){
            Toast.makeText(this,"More is coming soon", Toast.LENGTH_SHORT).show();
        }
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

    class EventTask extends AsyncTask<Void, Void, Void>{

        HttpURLConnection urlConnection;
        JSONArray jsonArray;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(eventURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                JSONArray jsonObject = new JSONArray(sb.toString());
                System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
                int length =  jsonObject.length();
                System.out.println("EVENT DATA length : " + length);
                for(int i = 0 ; i < length; i++){
                    JSONObject jSon = jsonObject.getJSONObject(i);
                    EventModel model = new EventModel();
                    String superCateName = jSon.optString("event_superCategory");
                    model.setEvent_super_category(superCateName);
                    model.alEvent = new ArrayList<EventDetail>();
                    JSONArray jArray = jSon.getJSONArray("Cate");
                    for(int j = 0; j <jArray.length() ; j++){
                        EventDetail detail = new EventDetail();
                        JSONObject jsonDetail = jArray.getJSONObject(j);
                        detail.setSuper_category_name(superCateName);
                        detail.setCategory_name(jsonDetail.optString("event_category"));
                        detail.setEvent_meta(jsonDetail.optString("event_meta"));
                        detail.setEvent_title(jsonDetail.optString("event_title"));
                        model.alEvent.add(detail);
                        arrayListEvent.add(detail);
                    }
                    alModel.add(model);
                }

                System.out.println("EVENT DETAIL : "+alModel.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            eventAdapter.notifyDataSetChanged();
        }
    }

    private void addUserDetail(){

        alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("name", MyApp.preferences.getString(SignUpActivity.USER_NAME, null));
        alanisawesomeMap.put("lastName", MyApp.preferences.getString(SignUpActivity.USER_LAST_NAME, null));
        alanisawesomeMap.put("passKey", MyApp.preferences.getString(SignUpActivity.USER_PASSWORD, null));
        alanisawesomeMap.put("phone", MyApp.preferences.getString(SignUpActivity.USER_PHONE, null));
        alanisawesomeMap.put("email", MyApp.preferences.getString(SignUpActivity.USER_EMAIL, null));
        System.out.println("User Name " + MyApp.preferences.getString(SignUpActivity.USER_NAME, null));
        UserDetailTask task = new UserDetailTask();
        task.execute();
    }

    class UserDetailTask extends AsyncTask<Void, Void, Void> {

        HttpURLConnection urlConnection;
        JSONArray jsonArray;
        int length = -1;
        boolean flagExist = false;
        String deviceID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deviceID =  MyApp.getDeviveID(MainActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(firebaseURL+"/users.json");
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
             //   System.out.println("GetUSER jsonObject : " + sb.toString());
                JSONObject jsonObject = new JSONObject(sb.toString());
                System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
                length =  jsonObject.length();
                System.out.println("EVENT DATA length : " + length);
                JSONObject jUser = jsonObject.getJSONObject(deviceID);
                flagExist = jsonObject.has(deviceID);
            } catch (MalformedURLException e) {
                System.out.println("MalfomedURL Exception : " + e.toString());
            } catch (IOException e) {
                System.out.println("IO Exception : " + e.toString());
            } catch (JSONException e) {
               System.out.println("JSON Exception : " + e.toString());
            } catch (Exception e){
                System.out.println("Exception : "+e.toString());
            }finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Firebase usersRef = firebase.child("users");//.child(""+length);
          /*  final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
            length++;
            System.out.println("USER List length : " + length);
            users.put("" + length, alanisawesomeMap);

            usersRef.setValue(users);*/
            if(flagExist){
                System.out.println("User Already Registered");
            }else {
                System.out.println("USER List length : " + length);
                String email = MyApp.preferences.getString(SignUpActivity.USER_EMAIL, null);
                if (!TextUtils.isEmpty(email)) {

                    final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
                    length++;
                    System.out.println("USER List new length : " + length);
                    System.out.println("USER List new deviceID : " + deviceID);
                    users.put("0", alanisawesomeMap);
                    firebase.child("users/" + deviceID).setValue(users);
                }
            }
        }
    }

}
