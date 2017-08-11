package com.get.wazzon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.AdminMessage;
import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.app.model.MyUtill;
import com.app.model.UserProfile;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mylist.adapters.CannedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.get.wazzon.EventChatActivity.CateName;
import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.MyApp.DEEPLINK_BASE_URL;
import static com.get.wazzon.MyApp.StadiumMsgLimit;
import static com.get.wazzon.MyApp.preferences;
import static com.get.wazzon.R.id.etChatMsg;
import static com.get.wazzon.R.id.tvAdminMsg1;


/**
 * Created by manish on 8/2/2016.
 */
public class ChatStadiumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ConnectDetector connectDetector;
    ListView listView;
    ImageView imgEmoji,keyboard, selectImage;
    ImageView send;
    EditText etMsg;
    static LinearLayout linearCanMsg;
    GridView viewLay;
    LinearLayout linearLayout;
    //Firebase myFirebaseRef;
    Firebase alanRef;
    Firebase alanNotiNode;
    private SwipeRefreshLayout swipeRefreshLayout;
    CannedAdapter cannedAdapter;
    static boolean addHousePartyFLAG = false;
    static boolean addTuneFLAG = false;
    public final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
    SharedPreferences.Editor editor;
    String userName="";
    //InputMethodManager imm;
    String subscribedGroup;
    int noSend;
    ArrayList<ChatData> alList;
    ArrayList<String> mKeys;
    Handler handler;
    String prediction;
    InputMethodManager imm;
    private static int PICK_PHOTO_FOR_AVATAR = 1101;
    private static int PREVIEW_CHECKED = 1015;
    Uri mFileUri;
    private static int REQUEST_CAMERA = 1123;

    NewAdapter chatAdapter;
    String longDeepLink = DEEPLINK_BASE_URL+"?link=$" +
            "&apn=com.get.wazzon"+
            "&afl=$"+
            "&st=" +
            "&sd=" +
            "&si="+
            "&utm_source=";
    public static ProgressBar pd;
    //String shortLinkURL="";
    String msg;
    //private DatabaseReference mDatabaseRefrenceSync;
    View v;
    //int mPageEndOffsetStadium = 0;
    Query alanQuery;
    public LayoutInflater inflater;
    ViewGroup container;
    View view;
    ChildEventListener childEventListener;
    public static boolean nahClicked=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        MyApp.CustomEventAnalytics("fragment_selected", "std");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.inflater = inflater;
        this.container = container;
        connectDetector = new ConnectDetector(getActivity());
        if(connectDetector.getConnection()) {
            //myFirebaseRef = new Firebase(firebaseURL);
            //myFirebaseRef.limitToFirst(10);
            alanRef = new Firebase(firebaseURL).child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name() + "/ " + eventDetail.getEvent_title() + "/ " + eventID).child("StadiumChat");
            alanNotiNode = new Firebase(firebaseURL).child(EventChatActivity.SuperCateName).child("StadiumChat");
            alanQuery = alanRef.limitToLast(StadiumMsgLimit);
            userName = preferences.getString(MyApp.USER_NAME, null);
        }

        view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);
        try {
            if (!preferences.getBoolean(eventDetail.getCatergory_id(), false)) {
                EventChatActivity.localFlag = true;
                handler = new Handler();
                handler.postDelayed(runn, 4 * 1000);
                if(eventDetail.getEvent_id().length() > 5){
                    String evntid = eventDetail.getEvent_id().substring(0, 5);
                    MyUtill.subscribeUserForEvents(evntid+"_stad");
                }else{
                    MyUtill.subscribeUserForEvents(eventDetail.getEvent_id()+"_stad");
                }
                MyApp.PreDefinedEventAnalytics("join_group",eventDetail.getCategory_name(),eventID);
                editor = preferences.edit();
                editor.putBoolean(eventDetail.getCatergory_id(), true);
                editor.commit();
                /* Sending Notification */
                AdminMessage tuneMsg = MyApp.alAdmMsg.get(6);
                String adTuneMsg = tuneMsg.get_admin_message().replace("<event>","");
                    MyUtill.sendNotification(getActivity(), adTuneMsg+eventDetail.getEvent_title(), "Welcome to WazzOn", "eventID", eventDetail.getEvent_id());
                        /*  Update user, Subscribe this event */
                getAdminSecondMessage();

            }else{
//                EventChatActivity.localFlag = false;
            }
        }catch (Exception ex){ex.printStackTrace();}

        return view;
    }
    int i = 0;
    Runnable runn = new Runnable() {
        @Override
        public void run() {
            if(EventChatActivity.localFlag) {
                ArrayList<String> localData = eventDetail.getLocalMsg();
                Log.e("ChatStadiumFragment", "localData.size() : "+localData.size());
                while(i < localData.size()) {
                    ChatData chatData = new ChatData();
                    chatData.setAuthor("");
                    chatData.setAuthorType("com");
                    chatData.setMessageType("");
                    chatData.setTimestamp("");
                    chatData.setTitle(localData.get(i));
                    chatData.setToUser("");
                    alList.add(chatData);
                    mKeys.add(String.valueOf(i));
                    chatAdapter.notifyDataSetChanged();
                    handler.removeCallbacks(runn);
                    handler.postDelayed(runn, 2 * 1000);
                    i++;
                    break;
                }
                if(i >= localData.size()){
                    handler.removeCallbacks(runn);
                    EventChatActivity.localFlag = false;
                }
                chatAdapter.notifyDataSetChanged();
//                EventChatActivity.localFlag = false;
            }
//            handler.removeCallbacks(runn);
            chatAdapter.notifyDataSetChanged();
        }
    };


    private void init(final View v) {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        pd = (ProgressBar) v.findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);
        linearLayout = (LinearLayout) v.findViewById(R.id.linearTopChat);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) v.findViewById(R.id.listMain);
        imgEmoji = (ImageView) v.findViewById(R.id.imgEmoji);
        keyboard = (ImageView) v.findViewById(R.id.keyboard);
        selectImage = (ImageView) v.findViewById(R.id.imgSelectImage);
        send = (ImageView) v.findViewById(R.id.imgSendChat);
        etMsg = (EditText) v.findViewById(R.id.etChatMsg);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        linearCanMsg = (LinearLayout) v.findViewById(R.id.linearCanMsg);
        viewLay = (GridView) v.findViewById(R.id.viewLay);
        try {
            prediction = eventDetail.getPrediction();
            Log.i("ChatStadiumFragment", "Prediction "+prediction);
            if(!TextUtils.isEmpty(prediction)){
                if (prediction.contains(",")) {
                    String predict[] = prediction.split(",");
                    for(int i = 0 ; i < predict.length ; i++) {
                        eventDetail.getCannedMessage().set(i, predict[i]+" #prediction");
                    }
                }
            }
        }catch (Exception ex){}
        cannedAdapter = new CannedAdapter(getActivity(), eventDetail.getCannedMessage());
        viewLay.setAdapter(cannedAdapter);
        linearCanMsg.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        keyboard.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
        selectImage.setOnClickListener(this);

       /* etMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                viewLay.setVisibility(View.GONE);
                linearCanMsg.setVisibility(View.GONE);
                imgEmoji.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);
                etMsg.setFocusable(true);
                final View view = getActivity().getCurrentFocus();
                if (view != null && flagKey) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    etMsg.requestFocus();
                    flagKey = false;
                }
                return true;
            }
        });*/

        etMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    viewLay.setVisibility(View.GONE);
                    linearCanMsg.setVisibility(View.GONE);
                    imgEmoji.setVisibility(View.VISIBLE);
                    keyboard.setVisibility(View.GONE);
                }

            }
        });

        viewLay.setOnItemClickListener(this);
        alList = new ArrayList<ChatData>();
        mKeys = new ArrayList<String>();
//        handler = new Handler();
//        handler.postDelayed(runn, 4 * 1000);
        chatAdapter = new NewAdapter(getActivity(),R.layout.chat_layout, alList);
        longDeepLink = longDeepLink + eventDetail.getEvent_id()+"&utm_medium=Whatsapp&utm_campaign="+ eventDetail.getEvent_title();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.e("ChatStadiumFragment", "onChildAdded:" + dataSnapshot.getKey());
                ChatData model = dataSnapshot.getValue(ChatData.class);
                String key = dataSnapshot.getKey();
                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    alList.add(0, model);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == alList.size()) {
                        alList.add(model);
                        mKeys.add(key);
                    } else {
                        alList.add(alList.size(), model);
                        mKeys.add(mKeys.size(), key);
//                        alList.add(nextIndex, model);
//                        mKeys.add(nextIndex, key);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                //listView.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("ChatStadiumFragment", "onChildChanged:" + dataSnapshot.getKey());
                Log.d("ChatStadiumFragment", "onChildChanged previousChildName :" + previousChildName);
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                String key = dataSnapshot.getKey();
                ChatData newModel = dataSnapshot.getValue(ChatData.class);
                int index = mKeys.indexOf(key);
                alList.set(index, newModel);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("ChatStadiumFragment", "onChildRemoved:" + dataSnapshot.getKey());
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();
                Log.d("ChatStadiumFragment", "onChildRemoved:" + commentKey.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("ChatStadiumFragment", "onChildMoved:" + dataSnapshot.getKey());
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
                Log.d("ChatStadiumFragment", "onChildMoved:" + movedComment.toString());
                Log.d("ChatStadiumFragment", "onChildMoved:" + commentKey.toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w("ChatStadiumFragment", "postComments:onCancelled", firebaseError.toException());
                Toast.makeText(getActivity(), "Failed to load comments.",Toast.LENGTH_SHORT).show();
            }

        };
        alanQuery.addChildEventListener(childEventListener);
        /*if (StadiumMsgLimit >alList.size()&& alList.size()>3){
            System.out.println("previous :"+StadiumMsgLimit);
            StadiumMsgLimit= alList.size();
            System.out.println("new size :"+StadiumMsgLimit);
        }*/

        pd.setVisibility(View.GONE);


    }

    @Override
    public void onStart() {
        super.onStart();
        try {

            if(!preferences.getBoolean(eventID+"HouseParty", false) && !addHousePartyFLAG){
                getAdminSecondMessage();
            }else{
                linearLayout.removeAllViews();
            }
            //if (MyApp.StadiumMsgLimit>1){
                chatAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            //}

        }catch (Exception ex){
            Log.e("StadiumFragment", "onStart method ERROR: " + ex.toString());
        }
    }

    private void getAdminSecondMessage(){
        UserProfile profile = new UserProfile();
        profile.updateUserGroup(getActivity(), eventDetail.getCatergory_id());
        updateEventList();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(eventDetail.getCatergory_id(), true);
        editor.commit();
        if(!addHousePartyFLAG){
            addHousePartyFLAG = true;
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.admin_msg, null);
            LinearLayout linearAdminLay = (LinearLayout) vi.findViewById(R.id.linearAdmin);

            listView.addHeaderView(vi);
            LinearLayout linearAdminBtn = (LinearLayout) vi.findViewById(R.id.linearAdminBtn);
            linearAdminBtn.setVisibility(View.GONE);
            linearAdminBtn.setGravity(Gravity.CENTER);
            TextView tvAdminMsg = (TextView) vi.findViewById(tvAdminMsg1);
            LinearLayout.LayoutParams relativeParam;
            relativeParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            relativeParam.setMargins(0,5,0,0);
            tvAdminMsg.setBackgroundResource(R.drawable.new_admin_image);
            TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
            TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);

            ImageView like = (ImageView) vi.findViewById(R.id.like);
            ImageView dislike = (ImageView) vi.findViewById(R.id.dislike);
            like.setImageResource(R.drawable.add);
            dislike.setImageResource(R.drawable.nothanks);

            LinearLayout innerAdmin1 = (LinearLayout) vi.findViewById(R.id.innerAdmin1);
            innerAdmin1.setVisibility(View.GONE);

            LinearLayout innerAdmin2 = (LinearLayout) vi.findViewById(R.id.innerAdmin2);
            innerAdmin2.setVisibility(View.GONE);

            btnYes.setText("Invite Friends");
            btnNo.setText("No, Thanks");
            //tvAdminMsg.setText("Start a House Party. There are most fun.");

            if(MyUtill.isTimeBetweenTwoTime(eventDetail.getEvent_start(),eventDetail.getEvent_exp()))
            {   //LIVE
                String toDisplay = MyApp.alAdmMsg.get(1).get_admin_message().replace("<Event>",CateName);
                tvAdminMsg.setText(toDisplay);
            }else{
                //FUTURE OR PAST
                //Toast.makeText(getActivity(), ""+MyUtill.getDaysDifference(eventDetail.getEvent_start()), Toast.LENGTH_SHORT).show();
                if(MyUtill.getDaysDifference(eventDetail.getEvent_start()).contains("-")){
                    //PAST
                    tvAdminMsg.setText(MyApp.alAdmMsg.get(4).get_admin_message());
                }else{
                    //FUTURE LESS THAN 30 DAYS
                    tvAdminMsg.setText(
                            MyApp.alAdmMsg.get(2).get_admin_message()
                            .replace("<Event>",CateName)
                            .replace(
                                    "<days>",MyUtill.getDaysDifference(
                                            eventDetail.getEvent_start()
                                    )
                            )
                    );
                    //FUTURE MORE THAN 30 DAYS
                    if(tvAdminMsg.getText().toString().contains(" is coming soon")){
                        tvAdminMsg.setText(MyApp.alAdmMsg.get(5).get_admin_message().replace("<Event>",CateName));
                    }else{
                        //Toast.makeText(getActivity(), "in Else", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    housePartyStarted();
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.removeAllViews();
                }
            });
        }

        if(MyUtill.isTimeBetweenTwoTime(eventDetail.getEvent_start(),eventDetail.getEvent_exp())){
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.watching_test, null);
            TextView tvMsg = (TextView) vi.findViewById(R.id.tvAdminMsg1);
            TextView tvSubMsg = (TextView) vi.findViewById(R.id.tvSubMsg);
            tvMsg.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
            tvSubMsg.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
//            tvSubMsg.setTextColor(getActivity().getColor(R.color.white));
            LinearLayout linearBtn = (LinearLayout) vi.findViewById(R.id.linearBtn);
            linearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
            tvMsg.setText("Watching "+eventDetail.getEvent_title()+" on TV?");
            tvSubMsg.setText("Take a selfie with your TV and share Get 20 Won");

            listView.addHeaderView(vi);
        }

        if(alList.size() > 0) {
            listView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }
    }


    private void updateEventList(){
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("jsonEventData", null));
            if(!TextUtils.isEmpty(jsonArray.toString())){
            String eventUpdateUrl = MyApp.FIREBASE_BASE_URL;
            for(int i = 0; i < jsonArray.length() ; i++){
                //EventChatActivity.eventID;
                JSONObject jSon = jsonArray.getJSONObject(i);
                String superCate = jSon.optString("event_superCategory");
                String cateID = jSon.optString("event_super_id");
                if(superCate.equalsIgnoreCase(EventChatActivity.SuperCateName)){
                    JSONArray jArray = jSon.getJSONArray("Cate");
                    for(int j = 0; j < jArray.length() ; j++) {
                        JSONObject jsonDetail = jArray.getJSONObject(j);
                        String subCateID = jsonDetail.optString("event_sub_id");
                        String subscribedUser = jsonDetail.optString("subscribed_user");
                        if (eventDetail.getCatergory_id().equalsIgnoreCase(subCateID)){
                        try{
                            int noOfSubscrbedUser = Integer.parseInt(subscribedUser);
                            noOfSubscrbedUser++;
                            jsonDetail.put("subscribed_user", String.valueOf(noOfSubscrbedUser));
                            eventUpdateUrl = eventUpdateUrl + "/EventList/" +i+ "/Cate/" + j;
                            Map<String, Object> subscribeUserMap = new HashMap<String, Object>();
                            subscribeUserMap.put("subscribed_user", noOfSubscrbedUser);
                            Firebase eventFire =  new Firebase(eventUpdateUrl);
                            eventFire.updateChildren(subscribeUserMap);
                        }catch (Exception ex){
                            Log.i("StadiumFragment", "Subscribed user ERROR: "+ex.toString());
                        }
                        }
                    }
                }
            }
            }
        } catch (JSONException e) {
            Log.i("StadiumFragment", "Subscribed user Data ERROR: " + e.toString());
        }
    }


    private void housePartyStarted(){
        editor = preferences.edit();
        editor.putBoolean(eventID + "HouseParty", true);
        editor.commit();
        Intent ii = new Intent(getActivity(), InviteFriendActivity.class);
        ii.putExtra("EventName", eventDetail.getCatergory_id());
        ii.putExtra("EventID", eventDetail.getEvent_id());
        ii.putExtra("Event", eventDetail.getEvent_title());
        ii.putExtra("EventTime", eventDetail.getEvent_start());
        ii.putExtra("message", "");
        startActivity(ii);
    }


    @Override
    public void onResume() {
        super.onResume();

        if(nahClicked || EventChatActivity.localFlag){
//            etMsg.setText("");
//            etMsg.setFocusable(false);
            viewLay.setAdapter(cannedAdapter);
            linearCanMsg.setVisibility(View.VISIBLE);
            MyUtill.hideKeyBoard(getActivity(),linearCanMsg, true);

            imgEmoji.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);

//            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else{
            imgEmoji.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);
            /*etMsg.setText("");
            viewLay.setAdapter(cannedAdapter);
            linearCanMsg.setVisibility(View.VISIBLE);
            MyUtill.hideKeyBoard(getActivity(),linearCanMsg,true);*/

            try {
                //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                //View view = getActivity().getCurrentFocus();
                userName = preferences.getString(MyApp.USER_NAME, null);
                noSend = Integer.parseInt(preferences.getString("SendTime: " + eventID, "-1"));
                if(!(noSend >= 0 && noSend < 3)){
                    if(noSend == 0) {
//                        linearCanMsg.setVisibility(View.GONE);
//                        MyUtill.hideKeyBoard(getActivity(),linearCanMsg,false);
                    }else {
                     //
                        //   linearCanMsg.setVisibility(View.GONE);
                    }
                }else{
//                    linearCanMsg.setVisibility(View.GONE);
//                    MyUtill.hideKeyBoard(getActivity(),linearCanMsg,false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        userName = preferences.getString(MyApp.USER_NAME, "");

        subscribedGroup = preferences.getString(MyApp.USER_JOINED_GROUP, "");
        if(subscribedGroup.contains(eventDetail.getCatergory_id())) {
            listView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
        EventChatActivity.localFlag = false;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onClick(View v) {
        try{
            int id = v.getId();
            final View view = getActivity().getCurrentFocus();

            if (id == R.id.keyboard) {
                /*Handler mHandler= new Handler();
                mHandler.post(
                        new Runnable() {
                            public void run() {*/
                if (view != null) {
                    imm.toggleSoftInputFromWindow(etMsg.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    etMsg.requestFocus();
                }
                /*}
            });*/

                viewLay.setVisibility(View.GONE);
                linearCanMsg.setVisibility(View.GONE);
                imgEmoji.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);
            }
            if (id == R.id.imgEmoji) {
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                etMsg.setText("");
                viewLay.setVisibility(View.VISIBLE);
                linearCanMsg.setVisibility(View.VISIBLE);
                imgEmoji.setVisibility(View.GONE);
                keyboard.setVisibility(View.VISIBLE);

            } else if (id == etChatMsg) {
                linearCanMsg.setVisibility(View.GONE);
//                imgEmoji.setVisibility(View.GONE);
//                keyboard.setVisibility(View.VISIBLE);
                viewLay.setVisibility(View.GONE);
                linearCanMsg.setVisibility(View.GONE);
                imgEmoji.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);

            } else if (id == R.id.imgSendChat) {
                if(!TextUtils.isEmpty(userName)) {
                  if(!TextUtils.isEmpty(userName) && !userName.contains("Guest")) {

                      if (preferences.getString("user_enabled", "").length() == 0 || preferences.getString("user_enabled", "").equals("true")) {
                          msg = etMsg.getText().toString();
                          subscribedGroup = preferences.getString(MyApp.USER_JOINED_GROUP, "");
                          if (!subscribedGroup.contains(eventDetail.getCatergory_id())) {
                              getAdminSecondMessage();
                          }
                          if (!TextUtils.isEmpty(msg)) {
                              // chatAdapter.notifyDataSetChanged();
                              if(eventDetail.getEvent_id().length() > 5) {
                                  String evntid = eventDetail.getEvent_id().substring(0, 5);
                                  MyUtill.subscribeUserForEvents(evntid + "_stad");
                              }else{
                                  MyUtill.subscribeUserForEvents(eventDetail.getEvent_id() + "_stad");
                              }
                              if(msg.contains("#prediction"))
                                    sendMsg(msg, "prediction");
                              else
                                    sendMsg(msg, "normal");

                              etMsg.setText("");
                              if (preferences.getString(MyApp.USER_TYPE, "").equals("com")) {
                                  System.out.println("com");
                              } else {
                                  System.out.println("user");
                              }
                          } else {
                              Toast.makeText(getActivity(), "Blank message not send", Toast.LENGTH_SHORT).show();
                          }
                      }else{
                          Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.user_disabled), Toast.LENGTH_SHORT).show();
                      }
                    }else{
                        Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                        startActivityForResult(ii, 111);
                    }
                }else{
                    if (view != null) {
                      //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    msg = etMsg.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                    startActivityForResult(ii, 111);
                }else {
                   // Toast.makeText(getActivity(), "Blank message not send", Toast.LENGTH_SHORT).show();
                }
                }
            }
            else if(id == R.id.imgSelectImage){
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);

                selectImage();
            }
        }catch (Exception ex){
            Log.e("StadiumFrament", "On Click Exception : "+ex.toString());
        }
    }

    String imageName;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111){
            if(resultCode == 101){
                noSend = Integer.parseInt(preferences.getString("SendTime: " + eventID, "-1"));
                if(noSend == -1){
                    noSend++;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SendTime: " + eventID, String.valueOf(noSend));
                    editor.putBoolean(eventID, true);
                    editor.commit();
                }
                if(noSend < 3) {
                    linearCanMsg.setVisibility(View.VISIBLE);
                }else{
                   Toast.makeText(getActivity(), "Let's put a Name to the message, Please Register",Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode == 102){
                getAdminSecondMessage();
            }
        }
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getActivity(), "Image Not Found", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                mFileUri = data.getData();
                //ExifInterface exif = new ExifInterface(new File(mFileUri.getPath()).toString());
                //exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                //Log.i("ChatStadiumFragment", "Path : "+mFileUri + " orientation "+exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                /*if(resultCode == Activity.RESULT_OK && data != null){
                    String realPath;
                    // SDK < API11
                    if (Build.VERSION.SDK_INT < 11)
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());

                        // SDK >= 11 && SDK < 19
                    else if (Build.VERSION.SDK_INT < 19)
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), data.getData());

                        // SDK > 19 (Android 4.4)
                    else
                        realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());
                    System.out.println(realPath);
                }*/


                Bundle bundle = new Bundle();
                bundle.putParcelable("FileURI", mFileUri);
                Intent ii = new Intent(getActivity(), PreviewImage.class);
                ii.putExtra("FileData", bundle);
                startActivityForResult(ii, PREVIEW_CHECKED);
            }
            catch (Exception ex){
                Toast.makeText(getActivity(), "File Not Found..", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PREVIEW_CHECKED && resultCode == Activity.RESULT_OK){
            uploadImage();
        }
        else if (requestCode == REQUEST_CAMERA) {
            File file = new File(mCurrentPhotoPath);
            Bundle bundle = new Bundle();
            mFileUri = Uri.fromFile(file);
            bundle.putParcelable("FileURI", mFileUri);
            Intent ii = new Intent(getActivity(), PreviewImage.class);
            ii.putExtra("FileData", bundle);
            startActivityForResult(ii, PREVIEW_CHECKED);
        }
    }


    @Override
    public void onRefresh() {
        StadiumMsgLimit = StadiumMsgLimit+5;
        swipeRefreshLayout.setRefreshing(false);
        alList = new ArrayList<ChatData>();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(ChatStadiumFragment.this).commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (preferences.getString("user_enabled", "").length() == 0 || preferences.getString("user_enabled", "").equals("true")) {
            //StadiumMsgLimit+=2;
            String msg = eventDetail.getCannedMessage().get(position);
            if(msg.contains("#prediction")) {
                String dateLimit = eventDetail.getPredictionUpdateTill();
                String status = MyUtill.getTimeDifference(dateLimit);
                if(status.contains("Ago")){
                    Toast.makeText(getActivity(), "We are not taking Predictions any Longer", Toast.LENGTH_SHORT).show();
                }else {
                    sendMsg(msg, "prediction"); //cannned message click
                }
            }
            else
                sendMsg(msg, "canned"); //cannned message click
                etMsg.setText("");
        }else
        {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.user_disabled), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMsg(String msg,String messageType){
       String deviceID = MyApp.getDeviveID(getActivity());
       String sender = preferences.getString(MyApp.USER_NAME, "");
        if(sender.contains("Guest") || TextUtils.isEmpty(sender)) {
            if (TextUtils.isEmpty(sender) && false) {
                Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                startActivityForResult(ii, 111);
            } else {
            try {
                if (!preferences.getBoolean("HousePartyMessage" + eventID, false)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("HousePartyMessage" + eventID, true);
                    editor.commit();
                }
            } catch (Exception ex) {
                Log.e("StadiumFragment", "sendMsg ERROR: " + ex.toString());
            }
            if (noSend < 3) {
                if (noSend == 0) {
                   getAdminSecondMessage();
                }
                noSend++;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SendTime: " + eventID, String.valueOf(noSend));
                editor.putBoolean(eventID, true);
                editor.commit();
                String commentator_privilege = preferences.getString("commentator_privilege", "");
                ChatData alan;
                if (commentator_privilege.contains(eventDetail.getCatergory_id())){
                    alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),"com",messageType);
                    if(msg.contains("#notifier")){
                        MyUtill.addMsgToCommentatorNotifier(getActivity(), msg);
                    }
                }else{
                     alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(), preferences.getString(MyApp.USER_TYPE, ""),messageType);
                }

                alanRef.push().setValue(alan);
                alanNotiNode.push().setValue(alan);
                if (messageType.equals("normal")) {
                    MyApp.CustomEventAnalytics("chat_sent",  eventDetail.getEvent_id()+"_stadium");
                }
                else if (messageType.equals("canned")){
                    MyApp.CustomEventAnalytics("canned_sent", eventDetail.getEvent_id()+"_stadium");
                }
                else if(messageType.equals("prediction")){
                    MyApp.CustomEventAnalytics("canned_sent", eventDetail.getEvent_id()+"_stadium");
                }
                if (msg.contains("#featured")||msg.contains("#Featured")||msg.contains("#FEATURED")){
                    MyUtill.addMsgtoFeatured(getActivity(),msg);
                }
            } else {
                Toast.makeText(getActivity(), "Let's put a Name to the message, Please Register", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                startActivityForResult(ii, 111);
            }
         }
        }else{
            String commentator_privilege = preferences.getString("commentator_privilege", "");
            ChatData alan;
            if (commentator_privilege.contains(eventDetail.getCatergory_id())){
                alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),"com",messageType);
                if(msg.contains("#notifier")){
                    MyUtill.addMsgToCommentatorNotifier(getActivity(), msg);
                }
            }else{
                alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(), preferences.getString(MyApp.USER_TYPE, ""),messageType);
            }
            //ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),messageType);
            alanRef.push().setValue(alan);
            alanNotiNode.push().setValue(alan);
                try {
                    if (messageType.equals("normal")) {
                        MyApp.CustomEventAnalytics("chat_sent", eventDetail.getEvent_id()+"_stadium");
                    } else if (messageType.equals("canned")) {
                        MyApp.CustomEventAnalytics("canned_sent", eventDetail.getEvent_id()+"_stadium");
                    } else if(messageType.equals("prediction")){
                        MyApp.CustomEventAnalytics("canned_sent", eventDetail.getEvent_id()+"_stadium");
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            //onRefresh();
            //chatAdapter.notifyDataSetChanged();
            if (msg.contains("#featured")||msg.contains("#Featured")||msg.contains("#FEATURED")){
                MyUtill.addMsgtoFeatured(getActivity(),msg);
            }
        }
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addTuneFLAG = false;
        addHousePartyFLAG = false;
        editor = preferences.edit();
        editor.putBoolean(eventID + "HouseParty", false);
        //System.out.println("~~~~~~~~~~"+ eventDetail.getEvent_id()+" :"+StadiumMsgLimit);
        editor.commit();
        //PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(eventDetail.getEvent_id(),""+StadiumMsgLimit).commit();
        nahClicked= false;
    }

    public String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
//          boolean result=Utility.checkPermission(getActivity());
            if (items[item].equals("Take Photo")) {
                dispatchTakePictureIntent();
            } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PHOTO_FOR_AVATAR);
    }




    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }



    void uploadImage(){
        try{
//            InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
        //    Toast.makeText(getActivity(), "Image selected..", Toast.LENGTH_SHORT).show();

            File f = new File(mFileUri.getLastPathSegment());
            imageName = f.getName();
            Log.e("ChatStadiumFragment", "imageName "+imageName);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference photoRef = storageReference.child(EventChatActivity.SuperCateName + "/" + eventDetail.getCategory_name()).child(mFileUri.getLastPathSegment());
            photoRef.putFile(mFileUri).addOnProgressListener(progressListener)
                                      .addOnSuccessListener(onSuccessListener)
                                      .addOnFailureListener(onFailureListener);


        }catch (Exception ex){
            Toast.makeText(getActivity(), "File Not Found", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    OnProgressListener progressListener =  new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                         /*   showProgressNotification(getString(R.string.progress_uploading),
                                    taskSnapshot.getBytesTransferred(),
                                    taskSnapshot.getTotalByteCount());*/
//            Toast.makeText(getActivity(), "Image Loading....", Toast.LENGTH_SHORT).show();
        }
    };

    OnSuccessListener onSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // Upload succeeded
//            Toast.makeText(getActivity(), "Image Successful Loaded....", Toast.LENGTH_SHORT).show();
            sendMsg(imageName, "image");
        }
    };


    OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Upload failed
            Toast.makeText(getActivity(), "Image Loading Failed....", Toast.LENGTH_SHORT).show();
            Log.e("ChatStadiumFragment", "Image Loading Failed ERROR : "+exception.toString());
            // [END_EXCLUDE]
        }
    };

}

