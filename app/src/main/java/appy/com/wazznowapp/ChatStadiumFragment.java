package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.app.model.MyUtill;
import com.app.model.UserProfile;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mylist.adapters.CannedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static appy.com.wazznowapp.EventChatActivity.eventDetail;
import static appy.com.wazznowapp.MyApp.StadiumMsgLimit;

/**
 * Created by admin on 8/2/2016.
 */
public class ChatStadiumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ConnectDetector connectDetector;
    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    //StadiumChatListAdapter adapter;
    EditText etMsg;
    static LinearLayout linearCanMsg;
    GridView viewLay;
    LinearLayout linearLayout;
    Firebase myFirebaseRef;
    Firebase alanRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    CannedAdapter cannedAdapter;
    static boolean addHousePartyFLAG = false;
    static boolean addTuneFLAG = false;
    public final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
    SharedPreferences.Editor editor;
    String userName="";
    InputMethodManager imm;
    String subscribedGroup;
    int noSend;
    ArrayList<ChatData> alList;
    ArrayList<String> mKeys;
    StadiumChatAdapter chatAdapter;
    String longDeepLink = "https://ry5a4.app.goo.gl/?link=$" +
            "&apn=appy.com.wazznowapp"+
            "&afl=$"+
            "&st=WazzNow+Title" +
            "&sd=House+Party+Chat+Invitation" +
            "&si=http://media.appypie.com/appypie-slider-video/images/logo_new.png"+
            "&utm_source=";
    ProgressBar pd;
    String shortLinkURL="";
    String msg;
    private DatabaseReference mDatabaseRefrenceSync;
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        connectDetector = new ConnectDetector(getActivity());
        if(connectDetector.getConnection()) {
            myFirebaseRef = new Firebase(firebaseURL);
            alanRef = myFirebaseRef.child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name() + "/ " + eventDetail.getEvent_title() + "/ " + EventChatActivity.eventID).child("StadiumChat");
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            alanRef.keepSynced(true); //synk db from live

            mDatabaseRefrenceSync=FirebaseDatabase.getInstance().getReference().child(EventChatActivity.SuperCateName + "/ " + EventChatActivity.CateName + "/ " + EventChatActivity.eventID).child("StadiumChat");
            mDatabaseRefrenceSync.keepSynced(true); //sync db from disk
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        pd = (ProgressBar) v.findViewById(R.id.pd);
        linearLayout = (LinearLayout) v.findViewById(R.id.linearTopChat);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) v.findViewById(R.id.listMain);
        imgEmoji = (ImageView) v.findViewById(R.id.imgEmoji);
        send = (ImageView) v.findViewById(R.id.imgSendChat);
        etMsg = (EditText) v.findViewById(R.id.etChatMsg);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        linearCanMsg = (LinearLayout) v.findViewById(R.id.linearCanMsg);
        viewLay = (GridView) v.findViewById(R.id.viewLay);
        cannedAdapter = new CannedAdapter(getActivity(), MyApp.alCanMsg);
        viewLay.setAdapter(cannedAdapter);
 //     al = new ArrayList<String>();
//        adapter = new MyChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
        linearCanMsg.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
        viewLay.setOnItemClickListener(this);
        alList = new ArrayList<ChatData>();
        mKeys = new ArrayList<String>();
        chatAdapter = new StadiumChatAdapter(getActivity(), alList);
    //    listView.setAdapter(chatAdapter);


        //eventCategory = getIntent().getStringExtra("EventName");
        //eventID = getIntent().getStringExtra("EventID");
        //http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk?utm_source=STR123&utm_medium=Whatsapp&utm_campaign=RN123
        longDeepLink = longDeepLink + eventDetail.getEvent_id()+"&utm_medium=Whatsapp&utm_campaign="+ eventDetail.getEvent_title();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.e("ChatStadiumFragment", "onChildAdded:" + dataSnapshot.getKey());
                ChatData model = dataSnapshot.getValue(ChatData.class);
                String key = dataSnapshot.getKey();
                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    alList.add(0, model);
                    mKeys.add(0, key);
                    StadiumMsgLimit++;
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == alList.size()) {
                        alList.add(model);
                        mKeys.add(key);
                    } else {
                        alList.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                    if(StadiumMsgLimit < 3){
                        StadiumMsgLimit++;
                    }
                }
                chatAdapter.notifyDataSetChanged();
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
        alanRef.addChildEventListener(childEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (!MyApp.preferences.getBoolean(eventDetail.getCatergory_id(), false)) {
                if(!addTuneFLAG){
                    addTuneFLAG = true;
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.admin_msg,null);
                    listView.addHeaderView(v);
                    listView.setAdapter(null);

                    //linearLayout.addView(v);
                    TextView tvAdminMsg = (TextView) v.findViewById(R.id.tvAdminMsg1);
                    TextView btnYes = (TextView) v.findViewById(R.id.btnAdminMsgYes);
                    TextView btnNo = (TextView) v.findViewById(R.id.btnAdminMsgNo);

                    ImageView like = (ImageView) v.findViewById(R.id.like);
                    ImageView dislike = (ImageView) v.findViewById(R.id.dislike);

                    like.setImageResource(R.drawable.like);
                    dislike.setImageResource(R.drawable.dislike);

                    btnYes.setText("Yes I want to tune In");
                    btnNo.setText("No, I don't want to tune In");
                    tvAdminMsg.setText("Congrats now you are part of "+ eventDetail.getSubscribed_user()+"+ in stadium following the match");

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MyApp.PreDefinedEventAnalytics("join_group",eventDetail.getCategory_name());
                            editor = MyApp.preferences.edit();
                            editor.putBoolean(eventDetail.getCatergory_id(), true);
                            editor.commit();
                            /*  Update user, Subscribe this event */
                            getAdminSecondMessage();
                        }
                    });
                }
            }else if(!MyApp.preferences.getBoolean(EventChatActivity.eventID+"HouseParty", false) && !addHousePartyFLAG){
                //linearLayout.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                View vii = inflater.inflate(R.layout.admin_msg,null);
                //linearLayout.removeAllViews();
                //linearLayout.addView(vi);
                if(v!=null)
                listView.removeHeaderView(v);
                listView.addHeaderView(vii);
                LinearLayout linearAdminBtn = (LinearLayout) vii.findViewById(R.id.linearAdminBtn);
                TextView tvAdminMsg = (TextView) vii.findViewById(R.id.tvAdminMsg1);
                TextView btnYes = (TextView) vii.findViewById(R.id.btnAdminMsgYes);
                TextView btnNo = (TextView) vii.findViewById(R.id.btnAdminMsgNo);

                ImageView like = (ImageView) vii.findViewById(R.id.like);
                ImageView dislike = (ImageView) vii.findViewById(R.id.dislike);

                like.setImageResource(R.drawable.add);
                dislike.setImageResource(R.drawable.nothanks);

                btnYes.setText("Invite Friends");
                btnNo.setText("No, Thanks");
                tvAdminMsg.setText("Start a House Party. There are most fun.");
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
            }else{
                linearLayout.removeAllViews();
            }


            if (MyApp.StadiumMsgLimit>1){
                chatAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

        }catch (Exception ex){
            Log.e("StadiumFragment", "onStart method ERROR: " + ex.toString());
        }
    }

    private void getAdminSecondMessage(){
        UserProfile profile = new UserProfile();
        profile.updateUserGroup(getActivity(), eventDetail.getCatergory_id());
        updateEventList();
        /*  Update user, Subscribe this event */
        SharedPreferences.Editor editor = MyApp.preferences.edit();
        editor.putBoolean(eventDetail.getCatergory_id(), true);
        editor.commit();
        if(!addHousePartyFLAG){
            addHousePartyFLAG = true;
            //linearLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.admin_msg, null);
            //linearLayout.addView(vi);
            listView.addHeaderView(vi);
            LinearLayout linearAdminBtn = (LinearLayout) vi.findViewById(R.id.linearAdminBtn);
            linearAdminBtn.setGravity(Gravity.CENTER);
            TextView tvAdminMsg = (TextView) vi.findViewById(R.id.tvAdminMsg1);
            TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
            TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);

            ImageView like = (ImageView) vi.findViewById(R.id.like);
            ImageView dislike = (ImageView) vi.findViewById(R.id.dislike);

            like.setImageResource(R.drawable.add);
            dislike.setImageResource(R.drawable.nothanks);

            btnYes.setText("Invite Friends");
            btnNo.setText("No, Thanks");
            tvAdminMsg.setText("Start a House Party. There are most fun.");
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
        if(alList.size() > 0) {
            listView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }
    }


    private void updateEventList(){
        try {
            JSONArray jsonArray = new JSONArray(MyApp.preferences.getString("jsonEventData", null));
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
        editor = MyApp.preferences.edit();
        editor.putBoolean(EventChatActivity.eventID + "HouseParty", true);
        editor.commit();
        Intent ii = new Intent(getActivity(), InviteFriendActivity.class);
        ii.putExtra("EventName", eventDetail.getCatergory_id());
        ii.putExtra("EventID", eventDetail.getEvent_id());
        startActivity(ii);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            View view = getActivity().getCurrentFocus();
            //imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            //adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatActivity.eventID, "-1"));
             if(!(noSend >= 0 && noSend < 3)){
                 if(noSend == 0) {
                     linearCanMsg.setVisibility(View.VISIBLE);
                     //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                     //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                 }else {
                     //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                     linearCanMsg.setVisibility(View.GONE);
                 }
            }else{
                linearCanMsg.setVisibility(View.VISIBLE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        //if(!TextUtils.isEmpty(userName)) {
            subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
            if(subscribedGroup.contains(eventDetail.getCatergory_id())) {
                listView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }
        //}
    }


    @Override
    public void onPause() {
        super.onPause();
        View view = getActivity().getCurrentFocus();
        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    @Override
    public void onClick(View v) {
        try{
            int id = v.getId();
            View view = getActivity().getCurrentFocus();
            if (id == R.id.imgEmoji) {
                if (view != null) {
                    //imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //imm.showSoftInput(etMsg, InputMethodManager.SHOW_IMPLICIT);
            } else if (id == R.id.etChatMsg) {
                linearCanMsg.setVisibility(View.GONE);
            } else if (id == R.id.imgSendChat) {
                if(!TextUtils.isEmpty(userName)) {
                  if(!TextUtils.isEmpty(userName) && !userName.contains("Guest")) {
                        String msg = etMsg.getText().toString();
                        subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
                        if (!subscribedGroup.contains(eventDetail.getCatergory_id())) {
                            getAdminSecondMessage();
                        }
                        if (!TextUtils.isEmpty(msg)) {
                          chatAdapter.notifyDataSetChanged();
                          etMsg.setText("");
                          sendMsg(msg,"normal");

                          if(MyApp.preferences.getString(MyApp.USER_TYPE, "").equals("com")){
                              System.out.println("com");
                          }else{
                              System.out.println("user");
                          }

                        } else {
                          Toast.makeText(getActivity(), "Blank message not send", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Intent ii = new Intent(getActivity(), SignUpActivity.class);
                        startActivityForResult(ii, 111);
                    }
                }else{
                    if (view != null) {
                      //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    Intent ii = new Intent(getActivity(), SignUpActivity.class);
                    startActivityForResult(ii, 111);
                }




            }
        }catch (Exception ex){
            Log.e("StadiumFrament", "On Click Exception : "+ex.toString());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111){
            if(resultCode == 101){
                noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatActivity.eventID, "-1"));
                if(noSend == -1){
                    noSend++;
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putString("SendTime: " + EventChatActivity.eventID, String.valueOf(noSend));
                    editor.putBoolean(EventChatActivity.eventID, true);
                    editor.commit();
                }
                if(noSend < 3) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        //imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    linearCanMsg.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getActivity(), "You have already send free messages.", Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode == 102){
                getAdminSecondMessage();
            }
        }
    }

    @Override
    public void onRefresh() {
        if(alList.size() > StadiumMsgLimit)
            StadiumMsgLimit = alList.size()-1;
        chatAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StadiumMsgLimit+=2;
        String msg = MyApp.alCanMsg.get(position).getCanned_message();
        sendMsg(msg,"canned"); //cannned message click
    }

    private void sendMsg(String msg,String messageType){
       String deviceID = MyApp.getDeviveID(getActivity());
       String sender = MyApp.preferences.getString(MyApp.USER_NAME, "");
        if(sender.contains("Guest") || TextUtils.isEmpty(sender)) {
            if (TextUtils.isEmpty(sender)) {
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivityForResult(ii, 111);
            } else {
            try {
                if (!MyApp.preferences.getBoolean("HousePartyMessage" + EventChatActivity.eventID, false)) {
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putBoolean("HousePartyMessage" + EventChatActivity.eventID, true);
                    editor.commit();
                    //sendHousePartyMsg();
                }
            } catch (Exception ex) {
                Log.e("StadiumFragment", "sendMsg ERROR: " + ex.toString());
            }
            if (noSend < 3) {
                if (noSend == 0) {
                   getAdminSecondMessage();
                }
                noSend++;
                SharedPreferences.Editor editor = MyApp.preferences.edit();
                editor.putString("SendTime: " + EventChatActivity.eventID, String.valueOf(noSend));
                editor.putBoolean(EventChatActivity.eventID, true);
                editor.commit();
                ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),messageType);
                alanRef.push().setValue(alan);

                if (messageType.equals("normal")) {
                    MyApp.CustomEventAnalytics("chat_sent", EventChatActivity.SuperCateName, EventChatActivity.eventDetail.getCategory_name());
                }
                else if (messageType.equals("canned")){
                    MyApp.CustomEventAnalytics("canned_sent", EventChatActivity.SuperCateName, EventChatActivity.eventDetail.getCategory_name());
                }

                if (msg.contains("#featured")||msg.contains("#Featured")||msg.contains("#FEATURED")){
                    MyUtill.addMsgtoFeatured(getActivity(),msg);
                }

            } else {
                Toast.makeText(getActivity(), "For send more messages you have to register", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivityForResult(ii, 111);
            }
         }
        }else{
            ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),messageType);
            alanRef.push().setValue(alan);
            MyApp.CustomEventAnalytics("chat_sent ", EventChatActivity.SuperCateName , EventChatActivity.eventDetail.getCategory_name());

            onRefresh();
            if (msg.contains("#featured")||msg.contains("#Featured")||msg.contains("#FEATURED")){
                MyUtill.addMsgtoFeatured(getActivity(),msg);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addTuneFLAG = false;
        addHousePartyFLAG = false;
        editor = MyApp.preferences.edit();
        editor.putBoolean(EventChatActivity.eventID + "HouseParty", false);
        editor.commit();
        //MyApp.StadiumMsgLimit=0;
    }


    public String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    class StadiumChatAdapter extends BaseAdapter{
        Context con;
        ArrayList<ChatData> alList;
        TextView tvUser,tvMsg,tvComMsg1;
        //TextView btnYes, btnNo;
        LinearLayout linear;//, linearBtn;
        RelativeLayout.LayoutParams relativeParam;
        ImageView imgIcon;
        RelativeLayout comRL;
        //int limit;
        public StadiumChatAdapter(Context context, ArrayList<ChatData> al){
            con = context;
            alList = al;
            //limit = msgLimit;
        }

        @Override
        public int getCount() {
            return StadiumMsgLimit;
        }

        @Override
        public Object getItem(int position) {
            return  alList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view==null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(con.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chat_layout, null);
                comRL = (RelativeLayout)view.findViewById(R.id.comRL);
                tvComMsg1 = (TextView)comRL.findViewById(R.id.tvComMsg1);
                imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
                tvUser = (TextView) view.findViewById(R.id.tvChatUser);
                tvMsg = (TextView) view.findViewById(R.id.tvChat);
                linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
                //linearBtn = (LinearLayout) view.findViewById(R.id.linearBtn);
                //btnYes = (TextView) view.findViewById(R.id.btnYesTuneOrInvite);
                //btnNo = (TextView) view.findViewById(R.id.btnNoThanks);
            }else{
                imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
                tvUser = (TextView) view.findViewById(R.id.tvChatUser);
                tvMsg = (TextView) view.findViewById(R.id.tvChat);
                linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
                comRL = (RelativeLayout)view.findViewById(R.id.comRL);
                tvComMsg1 = (TextView)comRL.findViewById(R.id.tvComMsg1);
                //linearBtn = (LinearLayout) view.findViewById(R.id.linearBtn);
                //btnYes = (TextView) view.findViewById(R.id.btnYesTuneOrInvite);
                //btnNo = (TextView) view.findViewById(R.id.btnNoThanks);
            }




            RelativeLayout RLcomlay = (RelativeLayout)view.findViewById(R.id.RLcomlay);

            ImageView share = (ImageView) view.findViewById(R.id.share);
            //ImageView facebook = (ImageView) RLcomlay.findViewById(R.id.facebook);
            if(!share.isShown())
                share.setVisibility(View.VISIBLE);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "whatsapp", Toast.LENGTH_SHORT).show();
                    MyApp.PreDefinedEventAnalytics("share",eventDetail.getEvent_title()+" :"+ EventChatActivity.eventID);
                    new newShortAsync().execute();
                }
            });

            /*facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "facebook", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(),InviteFriendActivity.class));
                }
            });*/

            if(position < alList.size() || StadiumMsgLimit < alList.size()) {
                try {
                    ChatData model = alList.get(alList.size()-StadiumMsgLimit+position);
                    populateView(view, model);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return view;
        }

        protected void populateView(final View v, ChatData model) {
            tvMsg.setText(model.getTitle());
            tvUser.setText(model.getAuthor());
            tvUser.setTypeface(MyApp.authorFont);
            tvMsg.setTypeface(MyApp.authorMsg);

            tvComMsg1.setText(model.getTitle());
            //linearBtn.setVisibility(View.GONE);
            relativeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            String sender = model.getAuthor();
            String fromUser = model.getToUser();
            String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
            boolean isEqual = sender.equalsIgnoreCase(userName);

            if(model.getAuthorType().equals("com")){
                //System.out.println("commmmenttttttaaaatooorrrr");
                comRL.setVisibility(View.VISIBLE);
                linear.setVisibility(View.GONE);
            }else {
                //System.out.println("model.getAuthor: "+model.getAuthorType());
                linear.setVisibility(View.VISIBLE);
                comRL.setVisibility(View.GONE);
                if((fromUser.equals(MyApp.getDeviveID(con)))) {
                    //tvMsg.setGravity(Gravity.RIGHT);
                    tvMsg.setTextColor(con.getResources().getColor(R.color.white));
                    tvMsg.setPadding(25,15,70,15);
                    tvUser.setGravity(Gravity.RIGHT);
                    tvUser.setVisibility(View.GONE);
                    linear.setGravity(Gravity.RIGHT);
                    relativeParam.addRule(Gravity.CENTER);
                    relativeParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    relativeParam.setMargins(0,5,105,5);
                    linear.setLayoutParams(relativeParam);
                    //linear.setBackgroundResource(R.drawable.chat_outgoing_background);
                    linear.setBackgroundResource(R.drawable.outgoing_message_bg);
                    //linearBtn.setVisibility(View.GONE);
                }
                else{
                    tvMsg.setGravity(Gravity.LEFT);
                    tvMsg.setPadding(35,5,10,15);
                    tvMsg.setTextColor(con.getResources().getColor(R.color.chat_text_color));
                    tvUser.setGravity(Gravity.LEFT);
                    tvUser.setVisibility(View.VISIBLE);
                    tvUser.setPadding(35,5,10,5);
                    relativeParam.addRule(Gravity.LEFT);
                    linear.setGravity(Gravity.LEFT);
                    relativeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    relativeParam.setMargins(105,5,0,5);
                    linear.setLayoutParams(relativeParam);
                    //linear.setBackgroundResource(R.drawable.chat_incomin_background);
                    linear.setBackgroundResource(R.drawable.chat_new);
                    linear.setPadding(35,5,80,5);
                    //linearBtn.setVisibility(View.GONE);
                }
                if(model.getAuthor().equalsIgnoreCase("Admin")) {
                    imgIcon.setVisibility(View.VISIBLE);
                }else{
                    imgIcon.setVisibility(View.GONE);
                    //tvMsg.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }


        public class newShortAsync extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //pd = new android.widget.ProgressBar(InviteFriendActivity.this,null,android.R.attr.progressBarStyleLarge);
                //pd.getIndeterminateDrawable().setColorFilter(0xFFFF0000,android.graphics.PorterDuff.Mode.MULTIPLY);
                //pd.setCancelable(false);
                pd.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                BufferedReader reader;
                StringBuffer buffer;
                String res=null;
                String json = "{\"longUrl\": \""+longDeepLink.replace("$",getResources().getString(R.string.apk_link))+"\"}";
                try {
                    URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key="+getResources().getString(R.string.google_shortlink_api_key));
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

                    int status=con.getResponseCode();
                    InputStream inputStream;
                    if(status== HttpURLConnection.HTTP_OK)
                        inputStream=con.getInputStream();
                    else
                        inputStream = con.getErrorStream();

                    reader= new BufferedReader(new InputStreamReader(inputStream));
                    buffer= new StringBuffer();
                    String line="";
                    while((line=reader.readLine())!=null)
                    {
                        buffer.append(line);
                    }
                    res= buffer.toString();
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
                System.out.println("JSON RESP:" + s);
                String response=s;
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String id=jsonObject.getString("id");
                    shortLinkURL = id;
                    Intent sendIntent = new Intent(getActivity(),ShareEventActivity.class);
                    if(!TextUtils.isEmpty(userName)){
                        if(!userName.contains("user")){
                            msg = "Hi, This is "+userName+". Watch the " + id+" with me right here on WazzNow.";
                        }else{
                            msg = "Hi,  Watch the " + id+" with me right here on WazzNow.";
                        }
                    }else{
                        msg = "Hi,  Watch the " + id+" with me right here on WazzNow.";
                    }
                    //Uri uri = buildDeepLink("http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk", 2, true);
                    //  String dLink = longDeepLink.replace("SenderID", eventID);
                    //sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra("share", msg);
                /*sendIntent.setType("text/plain");*/
                    //sendIntent.setPackage("com.whatsapp");
                    try{
                        startActivity(sendIntent);
                        //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                    }catch (Exception ex){
                        Toast.makeText(getActivity(), "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                    }
                    pd.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}

