package appy.com.wazznowapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.model.CannedMessage;
import com.app.model.ConnectDetector;
import com.app.model.EventDetail;
import com.app.model.EventDtList;
import com.app.model.EventModel;
import com.app.model.EventSubCateList;
import com.app.model.MyUtill;
import com.app.model.Sub_cate;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
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
import org.w3c.dom.Comment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listMain;
    private static boolean firstFlag = false;
    GoogleApiClient mGoogleApiClient;
    EventAdapter adapter;
    Firebase firebase;
    Firebase firebaseEvent;
    ArrayList<EventModel> alModel;
    static ArrayList<EventDetail> arrayListEvent;
    static EventModelAdapter eventAdapter;
    Map<String, String> alanisawesomeMap;
    static ProgressDialog progressDialog;
    ConnectDetector connectDetector;
    final String TAG = "MainActivity";
    Handler handler;
    boolean getInvited = false;
    String invitedEventid, invitedGroup;
    HashMap<String,EventDetail> hashMapEvent;
    private String firebaseURL = MyApp.FIREBASE_BASE_URL;
    String eventURL = MyApp.FIREBASE_BASE_URL+"/EventList.json";
    String cannedURL = MyApp.FIREBASE_BASE_URL+"/Canned.json";
    int REQUEST_INVITE = 111;
    //    static boolean eventFLAG = false;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectDetector = new ConnectDetector(this);
        if(connectDetector.getConnection()) {
            if (!firstFlag) {
            /* Below code runs only first time. When app opens after that same data will be used. When you close and reopen app then below code will execute again */
                firstFlag = true;

                UserDetailTask task = new UserDetailTask();
                task.execute();
                Intent ii = new Intent(this, MySplashActivity.class);
                startActivity(ii);
            }
            getInvited = false;
            hashMapEvent = new HashMap<String,EventDetail>();
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
                                        String inviterDeviceID = intent.getStringExtra("UserDeviceID");

                                        Uri uri = intent.getData();
                                        invitedEventid = uri.getQueryParameter("eventid");
                                       try{
                                           Log.e("MainActivity", "get Deep link URL "+deepLink);
                                           Log.e("MainActivity", "get Deep link uri "+uri.toString());
                                           invitedEventid = deepLink.split("utm_source=")[1].split("&")[0];
                                           Log.e("MainActivity", "get Deep link eventid "+invitedEventid);
                                           invitedGroup = deepLink.split("utm_campaign=")[1];
                                           Log.e("MainActivity", "get Deep link group "+invitedGroup);
                                            getInvited = true;

                                       }catch (Exception ex){
                                           Log.e("MainActivity", "get Deep link ERROR: "+ex.toString());
                                           getInvited = false;
                                       }
                                        // Because autoLaunchDeepLink = true we don't have to do anything
                                        // here, but we could set that to false and manually choose
                                        // an Activity to launch to handle the deep link here.
                                        // ...
                                    }
                                }
                            });
            init();
        } else{
            Toast.makeText(this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
        }
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
                    Log.d("InviteFriendActivity", "get Deep link onActivityResult: sent invitation " + id);
                    Toast.makeText(MainActivity.this, "get Deep link onActivityResult Invite Device ID "+id, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventDetail eventDetail = arrayListEvent.get(position);
        Intent iChat = new Intent(this, EventChatFragment.class);
        iChat.putExtra("EventDetail", eventDetail);
        startActivity(iChat);

      /*  for(int i = 0; i < arrayListEvent.size() ; i++){
            EventDetail event = arrayListEvent.get(i);
            if(eventName.equals(event.getCategory_name())){
                Intent iChat = new Intent(this, EventChatFragment.class);
                iChat.putExtra("EventDetail", event);
                startActivity(iChat);
                break;
            }
        }*/

    }

    private void init(){
            handler = new Handler();
            firebase = new Firebase(firebaseURL);
            firebaseEvent = firebase.child("EventList");
            progressDialog = new ProgressDialog(this);
            listMain = (ListView) findViewById(R.id.listMainEvent);
            alModel = new ArrayList<EventModel>();
            arrayListEvent = new ArrayList<EventDetail>();
            eventAdapter = new EventModelAdapter(this, arrayListEvent);
            listMain.setAdapter(eventAdapter);
            listMain.setOnItemClickListener(this);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
               Log.e(TAG, "onChildAdded:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged previousChildName :" + previousChildName);
               // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                try{
                    EventDtList evList = dataSnapshot.getValue(EventDtList.class);

                    arrayListEvent = new ArrayList<EventDetail>();
                    EventDetail eventDetail = new EventDetail();
                    for(int i = 0; i < evList.getCate().size(); i++){
                        EventSubCateList subCtList = evList.getCate().get(i);
                        String subscribedUser = subCtList.getSubscribed_user();

                        for(int j = 0; j < subCtList.getSub_cate().size(); j++){
                            eventDetail = new EventDetail();
                            eventDetail.setSuper_category_name(evList.getEvent_super_category());

                            eventDetail.setCategory_name(subCtList.getEvent_category());
                            eventDetail.setCatergory_id(subCtList.getEvent_sub_id());
                            eventDetail.setSubscribed_user(subCtList.getSubscribed_user());

                            Sub_cate subCate = subCtList.getSub_cate().get(j);
                            eventDetail.setEvent_id(subCate.getEvent_id());
                            eventDetail.setEvent_date(subCate.getEvent_date());
                            eventDetail.setEvent_meta(subCate.getEvent_meta());
                            eventDetail.setEvent_time(subCate.getEvent_time());
                            eventDetail.setEvent_title(subCate.getEvent_title());
                            eventDetail.setEvent_exp_time(subCate.getEvent_exp_time());
                            eventDetail.setEvent_image_url(MyApp.FIREBASE_IMAGE_URL+subCate.getEvent_id());
                            eventDetail.setSubscribed_user(subscribedUser);
                            arrayListEvent.add(eventDetail);

                        }
                    }
                    eventAdapter = new EventModelAdapter(MainActivity.this, arrayListEvent);
                    listMain.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                }catch (Exception ex){
                    Log.e(TAG, "onChildChanged ERROR: "+ex.toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();
                Log.d(TAG, "onChildRemoved:" + commentKey.toString());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
                Log.d(TAG, "onChildMoved:" + movedComment.toString());
                Log.d(TAG, "onChildMoved:" + commentKey.toString());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w(TAG, "postComments:onCancelled", firebaseError.toException());
                Toast.makeText(MainActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }


        };
        firebaseEvent.addChildEventListener(childEventListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(connectDetector.getConnection()) {
            try {
                if (inputMethodManager != null) {
                    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            eventAdapter = new EventModelAdapter(MainActivity.this, arrayListEvent);
            listMain.setAdapter(eventAdapter);
            handler.postDelayed(runEventTimer, 200);
        }
    }


    Runnable runEventTimer = new Runnable() {
        @Override
        public void run() {
            if(connectDetector.getConnection()) {
                eventAdapter.notifyDataSetChanged();
                handler.removeCallbacks(runEventTimer);
                handler.postDelayed(runEventTimer, 80 * 1000);
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
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
//        eventFLAG = false;
        SharedPreferences.Editor editor = MyApp.preferences.edit();
        editor.putString("jsonEventData", "");
        editor.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firstFlag = false;
    }


    class EventTask extends AsyncTask<Void, Void, Void>{

        HttpURLConnection urlConnection;
        JSONArray jsonArray;
        MyUtill myUtill;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                progressDialog.setMessage("Event Detail Loading...");
                progressDialog.show();
            }catch (Exception ex){}
            myUtill = new MyUtill();
//            eventFLAG = false;

        }

        @Override
        protected Void doInBackground(Void... params) {
//            URL url = null;
            try {
//                url = new URL(eventURL);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String line;
//                while ((line = br.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                br.close();

//                JSONArray jsonObject = new JSONArray(sb.toString());
                JSONArray jsonObject = myUtill.getJSONFromServer(eventURL);
                System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
                SharedPreferences.Editor editor = MyApp.preferences.edit();
                editor.putString("jsonEventData",jsonObject.toString());
                editor.commit();
                int length =  jsonObject.length();
                System.out.println("EVENT DATA length : " + length);
                for(int i = 0 ; i < length; i++){
                    JSONObject jSon = jsonObject.getJSONObject(i);
                    EventModel model = new EventModel();
                    String superCateName = jSon.optString("event_superCategory");
                    String superCateID = jSon.optString("event_super_id");
                    model.setEvent_super_category(superCateName);
                    model.setEvent_super_id(superCateID);
                    model.Cate = new ArrayList<EventDetail>();
                    JSONArray jArray = jSon.getJSONArray("Cate");
                    EventDetail detail = new EventDetail();
                    for(int j = 0; j <jArray.length() ; j++){

                        JSONObject jsonDetail = jArray.getJSONObject(j);

                        String subCateName = jsonDetail.optString("event_category");
                        String subCateID = jsonDetail.optString("event_sub_id");
                        String subscribedUser = jsonDetail.optString("subscribed_user");

                        JSONArray jsArr = jsonDetail.getJSONArray("Sub_cate");
                        for(int t = 0 ; t < jsArr.length(); t++ ){
                            detail = new EventDetail();
                            JSONObject jOBJ = jsArr.getJSONObject(t);
                            detail.setSuper_category_name(superCateName);
                            detail.setCategory_name(subCateName);
                            detail.setCatergory_id(subCateID);
                      //    detail.setCatergory_id(jOBJ.optString("event_sub_id"));
                            detail.setEvent_id(jOBJ.optString("event_id"));
                            detail.setEvent_meta(jOBJ.optString("event_meta"));
                            detail.setEvent_title(jOBJ.optString("event_title"));
                            detail.setEvent_date(jOBJ.optString("event_date"));
                            detail.setEvent_time(jOBJ.optString("event_time"));
                            detail.setEvent_image_url(MyApp.FIREBASE_IMAGE_URL+jOBJ.optString("event_id"));
                            detail.setSubscribed_user(subscribedUser);
                            String strTime = myUtill.getTimeDifference(detail.getEvent_date(), detail.getEvent_time()).trim();
                            if(!TextUtils.isEmpty(strTime)) {
                                model.Cate.add(detail);
                                arrayListEvent.add(detail);
                                hashMapEvent.put(detail.getEvent_id(), detail);
                            }else{
                                System.out.println("Event Expire Date & Time:  "+detail.getEvent_date()+", "+detail.getEvent_time());
                            }
                        }


                    }
                    alModel.add(model);
                }

                System.out.println("EVENT DETAIL : "+alModel.toString());
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listMain.setAdapter(eventAdapter);
            eventAdapter.notifyDataSetChanged();
            progressDialog.hide();
            CannedTask cannedTask = new CannedTask();
            cannedTask.execute();
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
            System.out.println("EVENT DATA deviceID : " + deviceID);

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
                String joinedGroup = jUser.optString("joined_group");
                String jnGrp[] = joinedGroup.split(",");
                SharedPreferences.Editor editor =  MyApp.preferences.edit();
                for(int i=0; i < jnGrp.length ; i++){
                    editor.putBoolean(jnGrp[i], true);
                }
                editor.commit();
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
               progressDialog.hide();
               EventTask task = new EventTask();
               task.execute();
           }

        }
    }

    class CannedTask extends AsyncTask<Void, Void, Void>{

        MyUtill myUtill;
        JSONArray jsonArray;
        CannedMessage message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myUtill = new MyUtill();
            message = new CannedMessage();
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyApp.alCanMsg = new ArrayList<CannedMessage>();
            jsonArray = myUtill.getJSONFromServer(cannedURL);

            for(int i = 0 ; i < jsonArray.length() ; i++){
                try {
                    message = new CannedMessage();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    message.setCanned_message(jsonObject.optString("Msg"));
                    MyApp.alCanMsg.add(message);
                } catch (JSONException e) {
                    Log.e("CannedTask","get canned message ERROR: "+e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.hide();
            try{
                if(getInvited){
                    EventDetail detail = hashMapEvent.get(invitedEventid);
                    Intent iChat = new Intent(MainActivity.this, EventChatFragment.class);
                    iChat.putExtra("EventDetail", detail);
                    startActivity(iChat);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }


}
