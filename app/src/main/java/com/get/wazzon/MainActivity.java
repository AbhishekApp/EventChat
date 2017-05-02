package com.get.wazzon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.model.AdminMessage;
import com.app.model.ConnectDetector;
import com.app.model.EventDetail;
import com.app.model.EventDtList;
import com.app.model.EventModel;
import com.app.model.EventSubCateList;
import com.app.model.MyUtill;
import com.app.model.Sub_cate;
import com.app.model.UserProfile;
import com.app.model.WonData;
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
import com.google.firebase.iid.FirebaseInstanceId;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.MyApp.HOUSE_PARTY_INVITATIONS;
import static com.get.wazzon.MyApp.alAdmMsg;
import static com.get.wazzon.MyApp.hashMapEvent;
import static com.get.wazzon.MyApp.preferences;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listMain;
    private static boolean firstFlag = false;
    GoogleApiClient mGoogleApiClient;
    EventAdapter adapter;
    Firebase firebase;
    Firebase firebaseEvent;
    ArrayList<EventModel> alModel;
    static ArrayList<EventDetail> arrayListEvent, arrayListEvent_previous,arrayListEvent_live;
    static EventModelAdapter eventAdapter;
    Map<String, String> alanisawesomeMap;
    //ProgressDialog progressDialog;
    ConnectDetector connectDetector;
    final String TAG = "MainActivity";
    Handler handler;
    boolean getInvited = false;
    String invitedEventid;
    private String firebaseURL = MyApp.FIREBASE_BASE_URL;
    String eventURL = MyApp.FIREBASE_BASE_URL+"/EventList.json";
    String cannedCricketURL = MyApp.FIREBASE_BASE_URL+"/Canned/$.json";
    String AdminURL = MyApp.FIREBASE_BASE_URL+"/admin_msg.json";
    String appInfo = MyApp.FIREBASE_BASE_URL+"/AppInfo.json";
    int REQUEST_INVITE = 111;
    //static boolean eventFLAG = false;
    InputMethodManager inputMethodManager;
    private String NotificationMessageToShow = "";
    ProgressBar pd;
    Double VersionOnNet = 0.0;
    String invitedEevntID = "";
    SharedPreferences.Editor editor = MyApp.preferences.edit();
    Firebase wonHistoryRef;
    Firebase engagementRef;
    String inviterId;
    String token;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        //setIntent(intent);
//        getEventDetail();
        if (intent != null && intent.getExtras()!=null) {
        //    getInvited = true;
            Object value;
            for (String key : intent.getExtras().keySet()) {
                value = intent.getExtras().get(key);
                //Log.d(TAG, "Key: " + key + " Value: " + value);
                try {
                    String string_value = value.toString();
                    //Toast.makeText(MainActivity.this, "title: " + string_value.split(" $")[0], Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, "message : " + string_value.split(" $")[1], Toast.LENGTH_LONG).show();

                    System.out.println("Key:Value "+key +":"+ string_value);
                    //System.out.println(string_value.split(" $")[0]);
                    //xxxSystem.out.println(string_value.split(" $")[1]);

                    if (key.equals("click_action")){
                        NotificationMessageToShow = string_value;
                    }

                    if (key.equals("eventID")){
                        //UserDetailTask task = new UserDetailTask();
                        //task.execute();
                        invitedEventid = string_value;
                        invitedEevntID= string_value;
                        getInvited = true;
                    }
                    //Toast.makeText(MainActivity.this, "from notification: custom message: " + getIntent().getStringExtra("data").toString().split("$")[1], Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //System.out.println("from notification: "+getIntent().getStringExtra("data").toString());
            try{

                if(getInvited){
                    if(invitedEevntID.contains("invi"))
                        invitedEevntID = invitedEevntID.replace("invi","");
                    if (hashMapEvent.containsKey(invitedEevntID)){
                        EventDetail detail = hashMapEvent.get(invitedEevntID);
                        if(detail.getEvent_id().length()>0){
                            Intent iChat = new Intent(MainActivity.this, EventChatActivity.class);
                            iChat.putExtra("EventDetail", detail);
                            iChat.putExtra("NotificationMessageToShow",NotificationMessageToShow);
                            startActivity(iChat);
                        }else{
                            Toast.makeText(MainActivity.this, "Event not exist anymore!!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                        Intent iAct= new Intent(MainActivity.this, MainActivity.class);
                        iAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(iAct);
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }else{
            getInvited = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        pd = (ProgressBar) findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);
        connectDetector = new ConnectDetector(this);

        if(connectDetector.getConnection()) {
            wonHistoryRef = new Firebase(firebaseURL).child("WonHistory");
            engagementRef = new Firebase(firebaseURL).child("Engagement");
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]

        if (getIntent().getExtras() != null) {
            Object value;
            for (String key : getIntent().getExtras().keySet()) {
                value = getIntent().getExtras().get(key);
                //Log.d(TAG, "Key: " + key + " Value: " + value);
                try {
                    String string_value = value.toString();
                    //Toast.makeText(MainActivity.this, "title: " + string_value.split(" $")[0], Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, "message : " + string_value.split(" $")[1], Toast.LENGTH_LONG).show();

                    System.out.println(key +":"+ string_value);
                    //System.out.println(string_value.split(" $")[0]);
                    //xxxSystem.out.println(string_value.split(" $")[1]);

                    if (key.equals("click_action")){
                        NotificationMessageToShow = string_value;
                    }

                    if (key.equals("eventID")){
                        //UserDetailTask task = new UserDetailTask();
                        //task.execute();
                        invitedEventid = string_value;
                        invitedEevntID= string_value;
                        getInvited = true;
                    }
                    //Toast.makeText(MainActivity.this, "from notification: custom message: " + getIntent().getStringExtra("data").toString().split("$")[1], Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    //e.printStackTrace();
                }
            }
            //System.out.println("from notification: "+getIntent().getStringExtra("data").toString());
//            getEventDetail();
            if (invitedEevntID.length()>0) {
                if(invitedEevntID.contains("invi"))
                     invitedEevntID = invitedEevntID.replace("invi", "");
                try {
                    if (hashMapEvent.containsKey(invitedEevntID)) {
                        EventDetail detail = hashMapEvent.get(invitedEevntID);
                        if (detail.getEvent_id().length() > 0) {
                            Intent iChat = new Intent(MainActivity.this, EventChatActivity.class);
                            iChat.putExtra("EventDetail", detail);
                            iChat.putExtra("NotificationMessageToShow", NotificationMessageToShow);
                            startActivity(iChat);
                            getInvited = false;
                        } else {
                            Toast.makeText(MainActivity.this, "Event not exist anymore!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //          Toast.makeText(MainActivity.this, "no", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){ex.printStackTrace();}
            }
        }else{
            getInvited = false;
        }
        // [END handle_data_extras]

        // Get token
        token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);
        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();


        if(connectDetector.getConnection()) {
            if (!firstFlag) {
            /* Below code runs only first time. When app opens after that same data will be used. When you close and reopen app then below code will execute again */
                firstFlag = true;
                UserDetailTask task = new UserDetailTask();
                task.execute();
                Intent ii = new Intent(this, MySplashActivity.class);
                startActivity(ii);
            }

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
            AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink).setResultCallback(
                    new ResultCallback<AppInviteInvitationResult>() {
                        @Override
                        public void onResult(AppInviteInvitationResult result) {
                        Log.d("MainActivity", "getInvitation:onResult:" + result.getStatus());
                        if (result.getStatus().isSuccess()) {
                            // Extract information from the intent
                            Intent intent = result.getInvitationIntent();
                            String deepLink = AppInviteReferral.getDeepLink(intent);
                            //String invitationId = AppInviteReferral.getInvitationId(intent);
                            //String inviterDeviceID = intent.getStringExtra("UserDeviceID");
                            Uri uri = intent.getData();
                            //invitedEventid = uri.getQueryParameter("eventid");

                            inviterId = uri.getQueryParameter("utm_medium");

                            Log.e("MainActivity", "get Deep link inviterId "+inviterId);

                           try{
                               Log.e("MainActivity", "get Deep link URL "+deepLink);
                               Log.e("MainActivity", "get Deep link uri "+uri.toString());

                               try {
                                   invitedEventid = deepLink.split("utm_source=")[1].split("&")[0];
                                   Log.e("MainActivity", "get Deep link eventid " + invitedEventid);


                               }
                               catch (Exception e){
                                   e.printStackTrace();
                               }

                               try {
                                   invitedEevntID = deepLink.split("utm_campaign=")[1];
                                   Log.e("MainActivity", "get Deep link group " + invitedEevntID);
                               }
                               catch (Exception e){
                                   e.printStackTrace();
                                   invitedEevntID=invitedEventid;
                               }
                               getInvited = true;

                               String invitation = uri.getQueryParameter("utm_source");

                               if (invitation.contains("invi")){
                                   //invited from house party
                                    invitation = invitation.replace("invi","");
                                    UserProfile profile = new UserProfile();
                                    profile.update_house_party_invitations(MainActivity.this, invitation);
                                   MyUtill.subscribeUserForEvents(invitation);
                                   if(invitation.length() > 5) {
                                       String invi = invitation.substring(0, 5);
                                       MyUtill.subscribeUserForEvents(invi + "_hp");
                                   }else{
                                       MyUtill.subscribeUserForEvents(invitation + "_hp");
                                   }
                               }else{
                                   //invited from somewhere else
                                   if(invitation.length() > 5) {
                                       String invi = invitation.substring(0, 5);
                                       MyUtill.subscribeUserForEvents(invi + "_stad");
                                   }else{
                                       MyUtill.subscribeUserForEvents(invitation + "_stad");
                                   }
                               }

                           }catch (Exception ex){
                               Log.e("MainActivity", "get Deep link ERROR: "+ex.toString());
                               getInvited = false;

                               //Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                           }
                            // Because autoLaunchDeepLink = true we don't have to do anything
                            // here, but we could set that to false and manually choose
                            // an Activity to launch to handle the deep link here.
                        }else{
                            //Toast.makeText(MainActivity.this, ""+result.getStatus(), Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                    try {
                        init();
                    }catch (Exception e){
                        // for now eat exceptions
                        e.printStackTrace();
                    }
        } else{
            //Toast.makeText(this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
            //finish();
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
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventDetail eventDetail = arrayListEvent.get(position);
        MyApp.PreDefinedEventAnalytics("select_content",eventDetail.getCategory_name(),eventDetail.getEvent_id());
        Intent iChat = new Intent(this, EventChatActivity.class);
        iChat.putExtra("EventDetail", eventDetail);
        startActivity(iChat);

    }


    private void init(){
        handler = new Handler();
        firebase = new Firebase(firebaseURL);
        firebaseEvent = firebase.child("EventList");
        listMain = (ListView) findViewById(R.id.listMainEvent);
        alModel = new ArrayList<EventModel>();
        arrayListEvent = new ArrayList<EventDetail>();
        arrayListEvent_previous = new ArrayList<EventDetail>();
        arrayListEvent_live = new ArrayList<EventDetail>();
        eventAdapter = new EventModelAdapter(this, arrayListEvent);
        listMain.setAdapter(eventAdapter);
        listMain.setOnItemClickListener(this);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
              // Log.e(TAG, "onChildAdded:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged previousChildName :" + previousChildName);
               // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                try{
                    EventDtList evList = dataSnapshot.getValue(EventDtList.class);
                    arrayListEvent.clear();
                    EventDetail eventDetail = new EventDetail();
                    for(int i = 0; i < evList.getCate().size(); i++){
                        EventSubCateList subCtList = evList.getCate().get(i);
                        //String subscribedUser = subCtList.getSubscribed_user();
                        for(int j = 0; j < subCtList.getSub_cate().size(); j++){
                            eventDetail = new EventDetail();
                            eventDetail.setSuper_category_name(evList.getEvent_super_category());
                            eventDetail.setCategory_name(subCtList.getEvent_category());
                            eventDetail.setCatergory_id(subCtList.getEvent_sub_id());
//                            eventDetail.setSubscribed_user(subCtList.getSubscribed_user());
                            Sub_cate subCate = subCtList.getSub_cate().get(j);
                            eventDetail.setEvent_id(subCate.getEvent_id());
                            eventDetail.setEvent_start(subCate.getEvent_date());
                            eventDetail.setEvent_meta(subCate.getEvent_meta());
                            eventDetail.setEvent_title(subCate.getEvent_title());
                            eventDetail.setEvent_exp(subCate.getEvent_exp());
                            eventDetail.setEvent_image_url(MyApp.FIREBASE_IMAGE_URL+subCate.getEvent_id());
                            eventDetail.setSubscribed_user(subCate.getSubscribed_user());
                            arrayListEvent.add(eventDetail);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            eventAdapter = new EventModelAdapter(MainActivity.this, arrayListEvent);
                            listMain.setAdapter(eventAdapter);
                            eventAdapter.notifyDataSetChanged();
                        }
                    });
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
                Toast.makeText(MainActivity.this, "Failed to load comments.",Toast.LENGTH_SHORT).show();
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
                eventAdapter = new EventModelAdapter(MainActivity.this, arrayListEvent);
                listMain.setAdapter(eventAdapter);
                handler.postDelayed(runEventTimer, 200);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
        MenuItem signup = menu.findItem(R.id.menu_signup);

        if (MyApp.preferences.getString("isSignupSuccessful", "false").equals("true")) {
            signup.setVisible(false);
        }else{
            signup.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_signup){
            Intent ii = new Intent(this, NewSignUpActivity.class);
            startActivity(ii);
        }else if(id == R.id.menu_info){
            Intent ii = new Intent(this, InfoActivity.class);
            startActivity(ii);
        }/*else if(id == R.id.menu_more){
            Toast.makeText(this,"More is coming soon", Toast.LENGTH_SHORT).show();
        }*/
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
        EventTask(){
            //progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                //progressDialog.setMessage("Event Detail Loading...");
                //progressDialog.show();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            myUtill = new MyUtill();
//            eventFLAG = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray jsonObject = myUtill.getJSONFromServer(eventURL);
             //   System.out.println("EVENT DATA jsonObject : " + jsonObject.toString());
                int length = 0;
                if (jsonObject!=null){

                length =  jsonObject.length();
                if (length>0) {
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putString("jsonEventData", jsonObject.toString());
                    editor.commit();
                }}
                try {
                    if(arrayListEvent != null)
                        arrayListEvent.clear();
                }catch (Exception ex){}
                System.out.println("EVENT DATA length : " + length);
                for(int i = 0 ; i < length; i++){
                    JSONObject jSon = jsonObject.getJSONObject(i);
                    EventModel model = new EventModel();
                    String superCateName = jSon.optString("event_superCategory");

                    cannedCricketURL = cannedCricketURL.replace("$",superCateName);

                    String superCateID = jSon.optString("event_super_id");
                    model.setEvent_super_category(superCateName);
                    model.setEvent_super_id(superCateID);
                    model.Cate = new ArrayList<EventDetail>();

                    JSONArray jArraycannedMessage = jSon.getJSONArray("cannedMessage");

                    ArrayList <String> cannedArray = new ArrayList<String>();
                    for(int k = 0; k <jArraycannedMessage.length() ; k++) {

                        cannedArray.add(jArraycannedMessage.getString(k));
                    }
                    ArrayList<String> localMsgArray = new ArrayList<String>();
                    try {
                        JSONArray jArrayLocalMsg = jSon.optJSONArray("OnBoarding");
                        for (int k = 0; k < jArrayLocalMsg.length(); k++) {

                            localMsgArray.add(jArrayLocalMsg.getString(k));
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    JSONArray jArray = jSon.getJSONArray("Cate");
                    EventDetail detail = new EventDetail();

                    for(int j = 0; j <jArray.length() ; j++){
                        JSONObject jsonDetail = jArray.getJSONObject(j);
                        String subCateName = jsonDetail.optString("event_category");
                        String subCateID = jsonDetail.optString("event_sub_id");
                        String subscribedUser ="";/* jsonDetail.optString("subscribed_user");*/
                        JSONArray jsArr = jsonDetail.getJSONArray("Sub_cate");
                        System.out.println("EVENT DATA SubCate Event length : " + jsArr.length());
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
                            detail.setEvent_start(jOBJ.optString("event_start"));
                            detail.setEvent_exp(jOBJ.optString("event_exp"));
                            detail.setEvent_visiblity(jOBJ.optString("visible"));
                            detail.setPrediction(jOBJ.optString("prediction"));
                            detail.setPredictionUpdateTill(jOBJ.optString("PredictionUpdateTill"));
                            detail.setEvent_image_url(MyApp.FIREBASE_IMAGE_URL+jOBJ.optString("event_id"));
                            detail.setSubscribed_user(jOBJ.optString("subscribed_user"));
                            detail.setCannedMessage(cannedArray);
                            detail.setLocalMsg(localMsgArray);
                            model.Cate.add(detail);

                            if(jOBJ.optString("visible").equals("true")) {

                                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                try{
                                    Date Date2 = format.parse(jOBJ.optString("event_start"));
                                    long mills = Date2.getTime() - System.currentTimeMillis();

                                    String temp = ""+mills;

                                   if(temp.contains("-")){
                                        // This is for past events store it in different list and merge afterwards.

                                        System.out.println(detail.getEvent_title());


                                       if(MyUtill.isTimeBetweenTwoTime(detail.getEvent_start(),detail.getEvent_exp())){

                                            //FOR LIVE!
                                           arrayListEvent_live.add(detail);
                                           hashMapEvent.put(detail.getEvent_id(), detail);

                                       }else{
                                            //FOR PAST
                                           arrayListEvent_previous.add(detail);
                                           hashMapEvent.put(detail.getEvent_id(), detail);
                                       }

                                    }else{
                                        // FOR UPCOMMING
                                        arrayListEvent.add(detail);
                                        hashMapEvent.put(detail.getEvent_id(), detail);
                                    }
                                    /*arrayListEvent.add(detail);
                                    hashMapEvent.put(detail.getEvent_id(), detail);*/
                                }catch (Exception e){

                                    // NO DATE
                                    arrayListEvent_previous.add(detail);
                                    hashMapEvent.put(detail.getEvent_id(), detail);
                                    e.printStackTrace();
                                }
                            }else if(jOBJ.optString("visible").equals("false"))
                            {
                                System.out.println("else if false");

                            }else{
                                System.out.println("in else can't parse");
                            }

                            String strTime = MyUtill.getTimeDifference(detail.getEvent_start()).trim();

                        }
                    }
                    alModel.add(model);
                }

                arrayListEvent.addAll(arrayListEvent_live);

                Collections.sort(arrayListEvent, new CustomComparator());
                Collections.sort(arrayListEvent_previous, new CustomComparator());


                arrayListEvent.addAll(arrayListEvent_previous);
                //System.out.println("EVENT DETAIL : "+alModel.toString());
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
            //progressDialog.hide();
            CannedCricketTask cannedCricketTask = new CannedCricketTask();
            cannedCricketTask.execute();
        }
    }


    public static class CustomComparator implements Comparator<EventDetail> {
        @Override
        public int compare(EventDetail o1, EventDetail o2) {
            return o1.getEvent_start().compareTo(o2.getEvent_start());
        }
    }



    private void addUserDetail(){
        alanisawesomeMap = new HashMap<String, String>();
        alanisawesomeMap.put("name", MyApp.preferences.getString(MyApp.USER_NAME, ""));
        alanisawesomeMap.put("lastName", MyApp.preferences.getString(MyApp.USER_LAST_NAME, ""));
        alanisawesomeMap.put("passKey", MyApp.preferences.getString(MyApp.USER_PASSWORD, ""));
        alanisawesomeMap.put("phone", MyApp.preferences.getString(MyApp.USER_PHONE, ""));
        alanisawesomeMap.put("email", MyApp.preferences.getString(MyApp.USER_EMAIL, ""));
        alanisawesomeMap.put("userType", MyApp.preferences.getString(MyApp.USER_TYPE, ""));
        alanisawesomeMap.put("fcm_id", token);
        //System.out.println("User Name " + MyApp.preferences.getString(MyApp.USER_NAME, null));
        //UserDetailTask task = new UserDetailTask();
        //task.execute();
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
            //System.out.println("EVENT DATA deviceID : " + deviceID);
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
                //System.out.println("EVENT DATA JSONOBJECT : " + jsonObject.toString());
                flagExist = jsonObject.has(deviceID);

                //System.out.println("EVENT DATA  Found : "+flagExist);
                //jUser = jsonObject.getJSONObject(deviceID);
                jsonArray = jsonObject.getJSONArray(deviceID);
                jUser = jsonArray.getJSONObject(0);
                String devID = jUser.optString(deviceID);
                String joinedGroup = jUser.optString("joined_group");
                String HousePartyChat = jUser.optString("house_party_invitations");

                SharedPreferences.Editor editor =  MyApp.preferences.edit();
                editor.putString(HOUSE_PARTY_INVITATIONS, HousePartyChat);
                editor.putString("commentator_privilege", jUser.optString("commentator_privilege"));
                //if (HousePartyChat.contains(",")){
                //    FireBaseHousePartyChatNode =  HousePartyChat.split(",")[0];
                // }else{
                 com.get.wazzon.MyApp.FireBaseHousePartyChatNode =  HousePartyChat;
                // }
                String jnGrp[] = joinedGroup.split(",");

                for(int i=0; i < jnGrp.length ; i++){
                    editor.putBoolean(jnGrp[i], true);
                }
                editor.commit();
                if(devID.equalsIgnoreCase(deviceID)){
                    //System.out.println("EVENT DATA Device Id found : " + devID.toString());
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
           try {
               Firebase usersRef = firebase.child("users");//.child(""+length);
               if (flagExist) {
                    //Toast.makeText(MainActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                   System.out.println("EVENT DATA User Already Registered");
                   MyApp.preferences.getString(MyApp.USER_NAME, "");
                   String uName = jUser.optString("name");
                   String uLastName = jUser.optString("lastName");
                   String uEmail = jUser.optString("email");
                   String uType = jUser.optString("userType");
                   String uPhone = jUser.optString("phone");
                   String uJoinedGroup = jUser.optString("joined_group");
                   String house_party_invitations = jUser.optString(HOUSE_PARTY_INVITATIONS);
                   SharedPreferences.Editor editor = MyApp.preferences.edit();
                   editor.putString(MyApp.USER_NAME, uName);
                   editor.putString(MyApp.USER_LAST_NAME, uLastName);
                   editor.putString(MyApp.USER_PHONE, uPhone);
                   editor.putString(MyApp.USER_EMAIL, uEmail);
                   editor.putString(MyApp.USER_TYPE, uType);
                   editor.putString(MyApp.USER_PASSWORD, deviceID);
                   editor.putString(MyApp.USER_JOINED_GROUP, uJoinedGroup);
                   editor.putString(HOUSE_PARTY_INVITATIONS, house_party_invitations);
                   editor.putString("user_enabled", jUser.optString("user_enabled"));

                   editor.commit();
               } else {
                   String email = MyApp.preferences.getString(MyApp.USER_EMAIL, "");
                   if (!TextUtils.isEmpty(email)) {
                       final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
                       //System.out.println("USER List new deviceID : " + deviceID);
                       users.put("0", alanisawesomeMap);
                       firebase.child("users/" + deviceID).setValue(users);
                   }
               }


               if(flagExist){
               /*old user*/
                    /*check for user already signed in */
                   //isSignupSuccessful = true;
                   editor = MyApp.preferences.edit();
                   editor.putString("isSignupSuccessful", "true");
                   editor.commit();
               }
               else{
                   /*new user*/
                   //isSignupSuccessful = false;
                   editor = MyApp.preferences.edit();
                   editor.putString("isSignupSuccessful", "false");
                   editor.commit();
               }
           }catch (Exception ex){
               System.out.println("EVENT DATA onPostExecute Exception : "+ex.toString());
           }finally {
               //progressDialog.hide();

               try {
                   EventTask task = new EventTask();
                   task.execute();
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
            pd.setVisibility(View.GONE);
        }
    }

    class CannedCricketTask extends AsyncTask<Void, Void, Void>{
        MyUtill myUtill;
        JSONArray jsonArray;
        //CannedCricketMessage message;
        AdminMessage admessage;
        String local="";
        String wonMsg, wonTitle;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myUtill = new MyUtill();
            //message = new CannedCricketMessage();
        }

        @Override
        protected Void doInBackground(Void... params) {

            alAdmMsg = new ArrayList<AdminMessage>();
            jsonArray = myUtill.getJSONFromServer(AdminURL);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                try {
                    admessage = new AdminMessage();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    admessage.set_admin_message(jsonObject.optString("Msg"));
                    alAdmMsg.add(admessage);
                } catch (JSONException e) {
                    Log.e("AdminTask","get admin message ERROR: "+e.toString());
                }
            }


            HttpURLConnection urlConnection = null;
            URL url = null;
            try {
                url = new URL(appInfo);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                JSONObject jsonObject = new JSONObject(sb.toString());
                MyUtill.popupTitle = jsonObject.getString("PopupTitle");
                MyUtill.popupMessage = jsonObject.getString("PopupMessage");
                //VersionOnNet =jsonObject.getString("AppVersionInfo");
                local = jsonObject.getString("AppVersionInfo").replace("\n","").replaceAll("^\"|\"$", "");
                wonMsg = jsonObject.optString("FirstWonMessage");
                wonTitle = jsonObject.optString("WonTitle");
                if(!TextUtils.isEmpty(wonMsg)){
                    editor = MyApp.preferences.edit();
                    editor.putString("WonFirstMessage", wonMsg);
                    if(!TextUtils.isEmpty(wonTitle)){
                        editor.putString("WonTitle", wonTitle);
                    }
                    editor.commit();
                }
                System.out.println("VERSION : "+sb.toString());
            } catch (MalformedURLException e) {
                System.out.println("VERSION DATA MalfomedURL Exception : " + e.toString());
            } catch (IOException e) {
                System.out.println("VERSION DATA IO Exception : " + e.toString());
            } catch (Exception e){
                System.out.println("VERSION DATA Exception : "+e.toString());
            }finally {
                urlConnection.disconnect();
            }


            //System.out.println(""+alAdmMsg.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressDialog.hide();
            try{
                //String versionName = BuildConfig.VERSION_NAME;
                Double versioName = Double.valueOf(BuildConfig.VERSION_NAME);
                VersionOnNet = Double.valueOf(local);



                int retrieval = Double.compare(versioName, VersionOnNet);

                if(retrieval > 0) {
                    System.out.println("versioName is greater than VersionOnNet");
                } else if(retrieval < 0) {
                    System.out.println("versioName is less than VersionOnNet");
                    MyUtill.alertDialogShowUpdate(MainActivity.this);
                } else {
                    System.out.println("versioName is equal to VersionOnNet");
                }



                /*if (VersionOnNet.equals(versioName)){
                }else{
                    MyUtill.alertDialogShowUpdate(MainActivity.this);
                }*/
                if(getInvited){
                    invitedEevntID = invitedEevntID.replace("invi","");
                    if (hashMapEvent.containsKey(invitedEevntID)){
                        EventDetail detail = hashMapEvent.get(invitedEevntID);
                        if(detail.getEvent_id().length()>0){
                            Intent iChat = new Intent(MainActivity.this, EventChatActivity.class);
                            iChat.putExtra("EventDetail", detail);
                            iChat.putExtra("NotificationMessageToShow",NotificationMessageToShow);
                            startActivity(iChat);
                        }else{
                            Toast.makeText(MainActivity.this, "Event not exist anymore!!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "no", Toast.LENGTH_SHORT).show();
                    }
                }
                EngagementTask engTask = new EngagementTask();
                engTask.execute();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }


    class EngagementTask extends AsyncTask<Void, Void, Void>{

        MyUtill myUtill;
        JSONObject jsonObject;
        JSONObject burnJSON, earnJSON, msgJSON;
        HttpURLConnection urlConnection;
        URL url = null;
        SharedPreferences.Editor editor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myUtill = new MyUtill();
            //message = new CannedCricketMessage();
        }


        @Override
        protected Void doInBackground(Void... params) {
            String urlStr = firebaseURL+"/Engagement.json";
             try {
                //System.out.println(sb.toString());
                jsonObject = myUtill.getJSONObjectFromServer(urlStr);
                burnJSON = jsonObject.optJSONObject("Burn");
                earnJSON = jsonObject.optJSONObject("Earn");
                editor = MyApp.preferences.edit();
                editor.putInt("BurnPrediction", burnJSON.getInt("Prediction"));
                editor.putInt("RedeemLimit", burnJSON.getInt("Redeem"));
                editor.putInt("FirstLaunchBonus", earnJSON.getInt("FirstLaunch"));
                editor.putInt("InviteFriendPoint", earnJSON.getInt("InviteFriend"));
                editor.putInt("EarnPrediction", earnJSON.getInt("Prediction"));
                msgJSON = earnJSON.optJSONObject("NiceMsg");
                editor.putInt("CharLimit", msgJSON.getInt("Chars"));
                editor.putInt("WonBonus", msgJSON.getInt("Won"));
                editor.commit();
            }  catch (JSONException e) {
                e.printStackTrace();// for now eat exceptions
            } catch (Exception ex){
                Log.e("MyUtill", "Get Engagement Data From Server ERROR: "+ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            WonHistoryTask task = new WonHistoryTask();
            task.execute();

        }
    }
    String wonMsg;
    JSONObject wonHistoryJSON;
    class WonHistoryTask extends AsyncTask<Void,Void,Void>{
        MyUtill myUtill;
        JSONObject userHistory, metaJSON;
        String wonHistoryStr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myUtill = new MyUtill();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String urlStr = firebaseURL+"/WonHistory.json";
            wonHistoryJSON = myUtill.getJSONObjectFromServer(urlStr);
            userHistory = wonHistoryJSON.optJSONObject(MyApp.getDeviveID(MainActivity.this));
            wonHistoryStr = String.valueOf(userHistory);
            Log.e("MainActivity", "Won History "+wonHistoryStr);
            if(TextUtils.isEmpty(wonHistoryStr) || wonHistoryStr.equalsIgnoreCase("null")){
                    try {
                        Map<String, Object> userMap = new HashMap<String, Object>();
                        Map<String, Object> meta = new HashMap<String, Object>();
                        Map<String, Object> myObj = new HashMap<String, Object>();
//                        meta.put("Burned",0);
//                        meta.put("TimeStamp",HousePartyFragment.getCurrentTimeStamp());
//                        meta.put("Earned",MyApp.preferences.getInt("FirstLaunchBonus", 30));
                       // AdminMessage msg = new AdminMessage();
                        WonData wonData = new WonData();
                        wonData.setEarned(String.valueOf(MyApp.preferences.getInt("FirstLaunchBonus", 30)));
                        wonData.setBurned("0");
                        wonData.setTimeStamp(HousePartyFragment.getCurrentTimeStamp());
                        wonMsg = MyApp.preferences.getString("WonFirstMessage",  "");
                        if(TextUtils.isEmpty(wonMsg))
                            wonMsg = "WooHoo! we have given you "+MyApp.preferences.getInt("FirstLaunchBonus", 30)+" won for joining in. Your balance : "+MyApp.preferences.getInt("FirstLaunchBonus", 30);
                        else{
                            wonMsg = wonMsg.replace("<Amt>", String.valueOf(MyApp.preferences.getInt("FirstLaunchBonus", 30)));
                            wonMsg = wonMsg.replace("<redeemamt>", String.valueOf(MyApp.preferences.getInt("RedeemLimit", 0)));
                        }
                     //   meta.put("Description", msg);
                        wonData.setDesc(wonMsg);
                        myObj.put("balance", MyApp.preferences.getInt("FirstLaunchBonus", 30));
                        myObj.put("Meta", meta);
                        userMap.put(MyApp.getDeviveID(MainActivity.this), myObj);
                        wonHistoryRef.updateChildren(userMap);
                        editor = MyApp.preferences.edit();
                        editor.putInt("BalanceWon",  MyApp.preferences.getInt("FirstLaunchBonus", 30));
                        editor.commit();
                        inviterGetWon();
                        wonHistoryRef.child( MyApp.getDeviveID(MainActivity.this)+ "/Meta").push().setValue(wonData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertForWon(MainActivity.this);
                            }
                        });
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }

            }else {
                int balance = userHistory.optInt("balance");
//                metaJSON = userHistory.optJSONObject("Meta");
//                int burnedWon = metaJSON.optInt("Burned");
//                int earnedWon = metaJSON.optInt("Earned");

                editor = MyApp.preferences.edit();
                editor.putInt("BalanceWon", balance);
                editor.commit();
            }
            return null;
        }
    }


    public void showAlertForWon(final Context activity)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(MyApp.preferences.getString("WonTitle", ""));
        alertDialog.setMessage(wonMsg);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.playon),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // DO TASK
//                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                        EventDetail eventDetail = arrayListEvent.get(0);
                        MyApp.PreDefinedEventAnalytics("select_content",eventDetail.getCategory_name(),eventDetail.getEvent_id());
                        Intent iChat = new Intent(activity, EventChatActivity.class);
                        iChat.putExtra("EventDetail", eventDetail);
                        activity.startActivity(iChat);
                    }
                });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.negative_btn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // DO TASK
                    }
                });
        alertDialog.show();
    }


    public void inviterGetWon(){
        try {
            if (!TextUtils.isEmpty(inviterId)) {
                JSONObject jsonObj = wonHistoryJSON.optJSONObject(inviterId);
                int inviterBalance = jsonObj.optInt("balance");
                JSONObject inviterMeta = jsonObj.optJSONObject("Meta");
//                int inviterEarn = inviterMeta.optInt("Earned");
//                int inviterBurn = inviterMeta.optInt("Burned");
                WonData wonData = new WonData();
                wonData.setEarned(String.valueOf(MyApp.preferences.getInt("InviteFriendPoint", 0)));
                wonData.setBurned("0");
//                Map<String, Object> inMeta = new HashMap<String, Object>();
//                inviterEarn = inviterEarn + MyApp.preferences.getInt("InviteFriendPoint", 0);
                inviterBalance = inviterBalance + MyApp.preferences.getInt("InviteFriendPoint", 0);
                wonHistoryRef.child(inviterId + "/balance").setValue(inviterBalance);
//                wonHistoryRef.child(inviterId + "/Meta/Earned").setValue(inviterEarn);
//                wonHistoryRef.child(inviterId + "/Meta/TimeStamp").setValue(HousePartyFragment.getCurrentTimeStamp());
                String inviterMsg = "Your invitee joins group. You earn " + MyApp.preferences.getInt("InviteFriendPoint", 0) + " won.";
                wonData.setDesc(inviterMsg);
                wonHistoryRef.child(inviterId + "/Meta/").push().setValue(wonData);

            }
        }catch (Exception ex){
            Log.e("MainActivity", "inviterGetWon ERROR: "+ex.toString());
        }
    }


}
