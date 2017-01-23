package appy.com.wazznowapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.app.model.UserProfile;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.mylist.adapters.CannedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
    SharedPreferences.Editor editor;
    String userName="";
    int msgLimit = 3;
    InputMethodManager imm;
    String subscribedGroup;
    int noSend;
    ArrayList<ChatData> alList;
    ArrayList<String> mKeys;
    ChatAdapter chatAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        connectDetector = new ConnectDetector(getActivity());
        if(connectDetector.getConnection()) {
            myFirebaseRef = new Firebase(firebaseURL);
            alanRef = myFirebaseRef.child(EventChatFragment.SuperCateName + "/ " + EventChatFragment.CateName + "/ " + EventChatFragment.eventID).child("StadiumChat");
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            alanRef.keepSynced(true);
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
        chatAdapter = new ChatAdapter(getActivity(), alList);
    //    listView.setAdapter(chatAdapter);

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
            if (!MyApp.preferences.getBoolean(EventChatFragment.eventDetail.getCatergory_id(), false)) {
                if(!addTuneFLAG){
                    addTuneFLAG = true;
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.admin_msg,null);
                    linearLayout.addView(v);
                    TextView tvAdminMsg = (TextView) v.findViewById(R.id.tvAdminMsg1);
                    TextView btnYes = (TextView) v.findViewById(R.id.btnAdminMsgYes);
                    TextView btnNo = (TextView) v.findViewById(R.id.btnAdminMsgNo);
                    btnNo.setText("NO I DON'T WANT TO TUNE IN");
                    tvAdminMsg.setText("Congrats now you are part of "+ EventChatFragment.eventDetail.getSubscribed_user()+"+ in stadium following the match");

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor = MyApp.preferences.edit();
                            editor.putBoolean(EventChatFragment.eventDetail.getCatergory_id(), true);
                            editor.commit();
                            /*  Update user, Subscribe this event */
                            getAdminSecondMessage();
                        }
                    });
                }
            }else if(!MyApp.preferences.getBoolean(EventChatFragment.eventID+"HouseParty", false) && !addHousePartyFLAG){
            //    linearLayout.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.admin_msg,null);
                linearLayout.removeAllViews();
                linearLayout.addView(vi);
                LinearLayout linearAdminBtn = (LinearLayout) vi.findViewById(R.id.linearAdminBtn);
                linearAdminBtn.setGravity(Gravity.RIGHT);
                TextView tvAdminMsg = (TextView) vi.findViewById(R.id.tvAdminMsg1);
                TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
                TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);
                btnYes.setText("INVITE FRIENDS");
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
        }catch (Exception ex){
            Log.e("StadiumFragment", "onStart method ERROR: " + ex.toString());
        }
    }

    private void getAdminSecondMessage(){
        UserProfile profile = new UserProfile();
        profile.updateUserGroup(getActivity(), EventChatFragment.eventDetail.getCatergory_id());
        updateEventList();
        /*  Update user, Subscribe this event */
        SharedPreferences.Editor editor = MyApp.preferences.edit();
        editor.putBoolean(EventChatFragment.eventDetail.getCatergory_id(), true);
        editor.commit();
        if(!addHousePartyFLAG){
            addHousePartyFLAG = true;
            //linearLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.admin_msg, null);
            linearLayout.addView(vi);
            LinearLayout linearAdminBtn = (LinearLayout) vi.findViewById(R.id.linearAdminBtn);
            linearAdminBtn.setGravity(Gravity.RIGHT);
            TextView tvAdminMsg = (TextView) vi.findViewById(R.id.tvAdminMsg1);
            TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
            TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);
            btnYes.setText("INVITE FRIENDS");
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
                    //EventChatFragment.eventID;
                    JSONObject jSon = jsonArray.getJSONObject(i);
                    String superCate = jSon.optString("event_superCategory");
                    String cateID = jSon.optString("event_super_id");
                    if(superCate.equalsIgnoreCase(EventChatFragment.SuperCateName)){
                        JSONArray jArray = jSon.getJSONArray("Cate");
                        for(int j = 0; j < jArray.length() ; j++) {
                            JSONObject jsonDetail = jArray.getJSONObject(j);
                            String subCateID = jsonDetail.optString("event_sub_id");
                            String subscribedUser = jsonDetail.optString("subscribed_user");
                            if (EventChatFragment.eventDetail.getCatergory_id().equalsIgnoreCase(subCateID)){
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
        editor.putBoolean(EventChatFragment.eventID + "HouseParty", true);
        editor.commit();
        Intent ii = new Intent(getActivity(), InviteFriendActivity.class);
        ii.putExtra("EventName", EventChatFragment.eventDetail.getCatergory_id());
        ii.putExtra("EventID", EventChatFragment.eventDetail.getEvent_id());
        startActivity(ii);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            View view = getActivity().getCurrentFocus();
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            //adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatFragment.eventID, "-1"));
             if(!(noSend >= 0 && noSend < 3)){
                 if(noSend == 0) {
                     linearCanMsg.setVisibility(View.VISIBLE);
                     imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                     imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                 }else {
                     imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                     linearCanMsg.setVisibility(View.GONE);
                 }
            }else{
                linearCanMsg.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(userName)) {
            subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
            if(subscribedGroup.contains(EventChatFragment.eventDetail.getCatergory_id())) {
                listView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        View view = getActivity().getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    @Override
    public void onClick(View v) {
        try{
            int id = v.getId();
            View view = getActivity().getCurrentFocus();
            if (id == R.id.imgEmoji) {
                if (view != null) {
                    //imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                imm.showSoftInput(etMsg, InputMethodManager.SHOW_IMPLICIT);
            } else if (id == R.id.etChatMsg) {
                linearCanMsg.setVisibility(View.GONE);
            } else if (id == R.id.imgSendChat) {
                if(!TextUtils.isEmpty(userName)) {
                  if(!TextUtils.isEmpty(userName) && !userName.contains("Guest")) {
                        String msg = etMsg.getText().toString();
                        subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
                        if (!subscribedGroup.contains(EventChatFragment.eventDetail.getCatergory_id())) {
                            getAdminSecondMessage();
                        }
                        if (!TextUtils.isEmpty(msg)) {
                          chatAdapter.notifyDataSetChanged();
                          etMsg.setText("");
                          sendMsg(msg);
                        } else {
                          Toast.makeText(getActivity(), "Blank message not send", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Intent ii = new Intent(getActivity(), SignUpActivity.class);
                        startActivityForResult(ii, 111);
                    }
                }else{
                    if (view != null) {
                      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatFragment.eventID, "-1"));
                if(noSend == -1){
                    noSend++;
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putString("SendTime: " + EventChatFragment.eventID, String.valueOf(noSend));
                    editor.putBoolean(EventChatFragment.eventID, true);
                    editor.commit();
                }
                if(noSend < 3) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        msgLimit+=5;
        chatAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        msgLimit+=2;
        String msg = MyApp.alCanMsg.get(position).getCanned_message();
        sendMsg(msg);
    }

    private void sendMsg(String msg){
       String deviceID = MyApp.getDeviveID(getActivity());
       String sender = MyApp.preferences.getString(MyApp.USER_NAME, "");
        if(sender.contains("Guest") || TextUtils.isEmpty(sender)) {
            if (TextUtils.isEmpty(sender)) {
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivityForResult(ii, 111);
            } else {
            try {
                if (!MyApp.preferences.getBoolean("HousePartyMessage" + EventChatFragment.eventID, false)) {
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putBoolean("HousePartyMessage" + EventChatFragment.eventID, true);
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
                editor.putString("SendTime: " + EventChatFragment.eventID, String.valueOf(noSend));
                editor.putBoolean(EventChatFragment.eventID, true);
                editor.commit();
                ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(), MyApp.preferences.getString(MyApp.USER_TYPE, ""));
                alanRef.push().setValue(alan);

            } else {
                Toast.makeText(getActivity(), "For send more messages you have to register", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivityForResult(ii, 111);
            }
         }
        }else{
            ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(), MyApp.preferences.getString(MyApp.USER_TYPE, ""));
            alanRef.push().setValue(alan);
            onRefresh();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        addTuneFLAG = false;
        addHousePartyFLAG = false;
        editor = MyApp.preferences.edit();
        editor.putBoolean(EventChatFragment.eventID + "HouseParty", false);
        editor.commit();
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

    class ChatAdapter extends BaseAdapter{
        Context con;
        ArrayList<ChatData> alList;
        TextView tvUser,tvMsg,tvComMsg1;
        //TextView btnYes, btnNo;
        LinearLayout linear;//, linearBtn;
        RelativeLayout.LayoutParams relativeParam;
        ImageView imgIcon;
        RelativeLayout comRL;
        //int limit;
        public ChatAdapter(Context context, ArrayList<ChatData> al){
            con = context;
            alList = al;
            //limit = msgLimit;
        }

        @Override
        public int getCount() {
            return msgLimit;
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
            if(position < alList.size() && msgLimit < alList.size()) {
                ChatData model = alList.get(alList.size()-msgLimit+position);
                try {
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
                    linear.setLayoutParams(relativeParam);
                    //linear.setBackgroundResource(R.drawable.chat_incomin_background);
                    linear.setBackgroundResource(R.drawable.incoming_message_bg);
                    linear.setPadding(35,5,80,5);
                    //linearBtn.setVisibility(View.GONE);
                }
                if(model.getAuthor().equalsIgnoreCase("Admin")) {
                    imgIcon.setVisibility(View.VISIBLE);
                }else{
                    imgIcon.setVisibility(View.GONE);
                    tvMsg.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }
}

