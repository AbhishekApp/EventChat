package appy.com.wazznowapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
    ProgressDialog progressDialog;

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
        init();
        if(!firstFlag) {
            /* Below code runs only first time. When app opens after that same data will be used. When you close and reopen app then below code will execute again */
            firstFlag = true;
            UserDetailTask task = new UserDetailTask();
            task.execute();
            Intent ii = new Intent(this, MySplashActivity.class);
            startActivity(ii);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent iChat = new Intent(this, EventChatFragment.class);
        iChat.putExtra("EventDetail", arrayListEvent.get(position));
        startActivity(iChat);
    }

    private void init(){
        firebase = new Firebase(firebaseURL);

        progressDialog = new ProgressDialog(this);
        listMain = (ListView) findViewById(R.id.listMainEvent);
        al = new ArrayList<EventData>();
        alModel = new ArrayList<EventModel>();
        arrayListEvent = new ArrayList<EventDetail>();
        eventAdapter = new EventModelAdapter(this, arrayListEvent);
        listMain.setAdapter(eventAdapter);
        listMain.setOnItemClickListener(this);
        // adapter = new AdapterMainFirst(this, al);
        //   addUserDetail();

//        EventTask task = new EventTask();
//        task.execute();

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        eventAdapter.notifyDataSetChanged();
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
            progressDialog.setMessage("Event Detail Loading...");
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
                        detail.setCatergory_id(jsonDetail.optString("event_id"));
                        detail.setEvent_meta(jsonDetail.optString("event_meta"));
                        detail.setEvent_title(jsonDetail.optString("event_title"));
                        detail.setEvent_date(jsonDetail.optString("event_date"));
                        detail.setEvent_time(jsonDetail.optString("event_time"));
                        detail.setSubscribed_user(jsonDetail.optString("subscribed_user"));
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
            progressDialog.hide();

        }
    }

    private void addUserDetail(){

        alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("name", MyApp.preferences.getString(MyApp.USER_NAME, null));
        alanisawesomeMap.put("lastName", MyApp.preferences.getString(MyApp.USER_LAST_NAME, null));
        alanisawesomeMap.put("passKey", MyApp.preferences.getString(MyApp.USER_PASSWORD, null));
        alanisawesomeMap.put("phone", MyApp.preferences.getString(MyApp.USER_PHONE, null));
        alanisawesomeMap.put("email", MyApp.preferences.getString(MyApp.USER_EMAIL, null));
        System.out.println("User Name " + MyApp.preferences.getString(MyApp.USER_NAME, null));
//        UserDetailTask task = new UserDetailTask();
//        task.execute();
    }

    class UserDetailTask extends AsyncTask<Void, Void, Void> {

        HttpURLConnection urlConnection;
        JSONArray jsonArray;
        JSONObject jUser;
        boolean flagExist = false;
        String deviceID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deviceID =  MyApp.getDeviveID(MainActivity.this);
            addUserDetail();
            System.out.println("EVENT DATA deviceID : "+deviceID);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
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

                JSONObject jsonObject = new JSONObject(sb.toString());
                System.out.println("EVENT DATA JSONOBJECT : " + jsonObject.toString());
                flagExist = jsonObject.has(deviceID);
                System.out.println("EVENT DATA  Found : "+flagExist);
          //    jUser = jsonObject.getJSONObject(deviceID);
                jsonArray = jsonObject.getJSONArray(deviceID);
                jUser = jsonArray.getJSONObject(0);
                String devID = jUser.optString(deviceID);
                if(devID.equalsIgnoreCase(deviceID)){
                    System.out.println("EVENT DATA Device Id found : " + devID.toString());
                    flagExist = true;
                }

            } catch (MalformedURLException e) {
                System.out.println("EVENT DATA MalfomedURL Exception : " + e.toString());
            } catch (IOException e) {
                System.out.println("EVENT DATA IO Exception : " + e.toString());
            } catch (JSONException e) {
               System.out.println("EVENT DATA JSON Exception : " + e.toString());
            } catch (Exception e){
                System.out.println("EVENT DATA Exception : "+e.toString());
            }finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Firebase usersRef = firebase.child("users");//.child(""+length);
           try {
               if (flagExist) {
//                   Toast.makeText(MainActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                   progressDialog.setMessage("User Found");
                   System.out.println("EVENT DATA User Already Registered");
                   MyApp.preferences.getString(MyApp.USER_NAME, null);
                   String uName = jUser.optString("name");
                   String uLastName = jUser.optString("lastName");
                   String uEmail = jUser.optString("email");
                   String uPhone = jUser.optString("phone");
                   String uJoinedGroup = jUser.optString("joined_group");
                   SharedPreferences.Editor editor = MyApp.preferences.edit();
                   editor.putString(MyApp.USER_NAME, uName);
                   editor.putString(MyApp.USER_LAST_NAME, uLastName);
                   editor.putString(MyApp.USER_PHONE, uPhone);
                   editor.putString(MyApp.USER_EMAIL, uEmail);
                   editor.putString(MyApp.USER_PASSWORD, deviceID);
                   editor.putString(MyApp.USER_JOINED_GROUP, uJoinedGroup);
                   editor.commit();
               } else {
                   String email = MyApp.preferences.getString(MyApp.USER_EMAIL, null);
                   if (!TextUtils.isEmpty(email)) {

                       final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
                       System.out.println("USER List new deviceID : " + deviceID);
                       users.put("0", alanisawesomeMap);
                       firebase.child("users/" + deviceID).setValue(users);
                   }
               }
           }catch (Exception ex){
               System.out.println("EVENT DATA onPostExecute Exception : "+ex.toString());
           }finally {
               EventTask task = new EventTask();
               task.execute();
           }

        }
    }

}
