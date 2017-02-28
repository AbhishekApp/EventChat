package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.firebase.client.Query;
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

import static appy.com.wazznowapp.EventChatActivity.CateName;
import static appy.com.wazznowapp.EventChatActivity.eventDetail;
import static appy.com.wazznowapp.EventChatActivity.eventID;
import static appy.com.wazznowapp.MyApp.StadiumMsgLimit;
/**
 * Created by admin on 8/2/2016.
 */
public class ChatStadiumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ConnectDetector connectDetector;
    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    EditText etMsg;
    static LinearLayout linearCanMsg;
    GridView viewLay;
    LinearLayout linearLayout;
    //Firebase myFirebaseRef;
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
    NewAdapter chatAdapter;
    String longDeepLink = "https://ry5a4.app.goo.gl/?link=$" +
            "&apn=appy.com.wazznowapp"+
            "&afl=$"+
            "&st=WazzNow+Title" +
            "&sd=House+Party+Chat+Invitation" +
            "&si=http://media.appypie.com/appypie-slider-video/images/logo_new.png"+
            "&utm_source=";
    public static ProgressBar pd;
    String shortLinkURL="";
    String msg;
    //private DatabaseReference mDatabaseRefrenceSync;
    View v;
    int mPageEndOffsetStadium = 0;
    Query alanQuery;
    public LayoutInflater inflater;
    ViewGroup container;
    View view;
    ChildEventListener childEventListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

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
            alanRef = new Firebase(firebaseURL).child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name() + "/ " + eventDetail.getEvent_title() + "/ " + EventChatActivity.eventID).child("StadiumChat");
            alanQuery = alanRef.limitToLast(StadiumMsgLimit);
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
        }

        view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);

        return view;
    }




    private void init(final View v) {
        pd = (ProgressBar) v.findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);
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
        linearCanMsg.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
        viewLay.setOnItemClickListener(this);
        alList = new ArrayList<ChatData>();
        mKeys = new ArrayList<String>();
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
                        alList.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
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
            if (!MyApp.preferences.getBoolean(eventDetail.getCatergory_id(), false)) {
                if(!addTuneFLAG){
                    addTuneFLAG = true;
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.admin_msg,null);
                    listView.addHeaderView(v);
                    listView.setAdapter(null);
                    TextView tvAdminMsg = (TextView) v.findViewById(R.id.tvAdminMsg1);
                    TextView btnYes = (TextView) v.findViewById(R.id.btnAdminMsgYes);
                    TextView btnNo = (TextView) v.findViewById(R.id.btnAdminMsgNo);

                    ImageView like = (ImageView) v.findViewById(R.id.like);
                    ImageView dislike = (ImageView) v.findViewById(R.id.dislike);

                    like.setImageResource(R.drawable.like);
                    dislike.setImageResource(R.drawable.dislike);

                    btnYes.setText("Yes I want to tune In");
                    btnNo.setText("No, I don't want to tune In");
                    //tvAdminMsg.setText("Congrats now you are part of "+ eventDetail.getSubscribed_user()+"+ in stadium following the match");

                    tvAdminMsg.setText(MyApp.alAdmMsg.get(0).get_admin_message().replace("<Event>",CateName));

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        MyApp.PreDefinedEventAnalytics("join_group",eventDetail.getCategory_name(),eventID);
                        editor = MyApp.preferences.edit();
                        editor.putBoolean(eventDetail.getCatergory_id(), true);
                        editor.commit();
                        /*  Update user, Subscribe this event */
                        getAdminSecondMessage();
                        }
                    });
                }
            }else if(!MyApp.preferences.getBoolean(EventChatActivity.eventID+"HouseParty", false) && !addHousePartyFLAG){
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
        SharedPreferences.Editor editor = MyApp.preferences.edit();
        editor.putBoolean(eventDetail.getCatergory_id(), true);
        editor.commit();
        if(!addHousePartyFLAG){
            addHousePartyFLAG = true;
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.admin_msg, null);
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
            //tvAdminMsg.setText("Start a House Party. There are most fun.");

            if(MyUtill.isTimeBetweenTwoTime(eventDetail.getEvent_start(),eventDetail.getEvent_exp()))
            {
                //LIVE
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
        ii.putExtra("Event", eventDetail.getEvent_title());
        ii.putExtra("EventTime", eventDetail.getEvent_start());
        ii.putExtra("message", "");
        startActivity(ii);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            View view = getActivity().getCurrentFocus();
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatActivity.eventID, "-1"));
             if(!(noSend >= 0 && noSend < 3)){
                 if(noSend == 0) {
                     linearCanMsg.setVisibility(View.VISIBLE);
                     MyUtill.hideKeyBoard(getActivity(),linearCanMsg);
                 }else {
                     linearCanMsg.setVisibility(View.GONE);
                 }
            }else{
                linearCanMsg.setVisibility(View.VISIBLE);
                 MyUtill.hideKeyBoard(getActivity(),linearCanMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
        if(subscribedGroup.contains(eventDetail.getCatergory_id())) {
            listView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        System.out.println("onPause");

    }


    @Override
    public void onClick(View v) {
        try{
            int id = v.getId();
            View view = getActivity().getCurrentFocus();
            if (id == R.id.imgEmoji) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                etMsg.setText("");
                viewLay.setVisibility(View.VISIBLE);
                linearCanMsg.setVisibility(View.VISIBLE);
                //Toast.makeText(getActivity(), "Emoji will be shown soon", Toast.LENGTH_SHORT).show();
                /*if (viewLay.getVisibility() == View.VISIBLE) {
                    viewLay.setVisibility(View.GONE);
                } else {
                    viewLay.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Emoji will be shown soon", Toast.LENGTH_SHORT).show();
                }*/

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
                        Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                        startActivityForResult(ii, 111);
                    }
                }else{
                    if (view != null) {
                      //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
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
                    linearCanMsg.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(getActivity(), "You have already send free messages.", Toast.LENGTH_SHORT).show();

                    Toast.makeText(getActivity(), "Let's put a Name to the message, Please Register",Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode == 102){
                getAdminSecondMessage();
            }
        }
    }

    @Override
    public void onRefresh() {

        StadiumMsgLimit = StadiumMsgLimit+5;

        swipeRefreshLayout.setRefreshing(false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(ChatStadiumFragment.this).commit();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //StadiumMsgLimit+=2;
        String msg = MyApp.alCanMsg.get(position).getCanned_message();
        sendMsg(msg,"canned"); //cannned message click

        etMsg.setText("");
    }

    private void sendMsg(String msg,String messageType){
       String deviceID = MyApp.getDeviveID(getActivity());
       String sender = MyApp.preferences.getString(MyApp.USER_NAME, "");
        if(sender.contains("Guest") || TextUtils.isEmpty(sender)) {
            if (TextUtils.isEmpty(sender)) {
                Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                startActivityForResult(ii, 111);
            } else {
            try {
                if (!MyApp.preferences.getBoolean("HousePartyMessage" + EventChatActivity.eventID, false)) {
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putBoolean("HousePartyMessage" + EventChatActivity.eventID, true);
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
                SharedPreferences.Editor editor = MyApp.preferences.edit();
                editor.putString("SendTime: " + EventChatActivity.eventID, String.valueOf(noSend));
                editor.putBoolean(EventChatActivity.eventID, true);
                editor.commit();
                ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),messageType);
                alanRef.push().setValue(alan);
                if (messageType.equals("normal")) {
                    MyApp.CustomEventAnalytics("chat_sent", EventChatActivity.SuperCateName, eventDetail.getCategory_name());
                }
                else if (messageType.equals("canned")){
                    MyApp.CustomEventAnalytics("canned_sent", EventChatActivity.SuperCateName, eventDetail.getCategory_name());
                }
                if (msg.contains("#featured")||msg.contains("#Featured")||msg.contains("#FEATURED")){
                    MyUtill.addMsgtoFeatured(getActivity(),msg);
                }
            } else {
                //Toast.makeText(getActivity(), "For send more messages you have to register", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "You have already sent Free Msgs” with “Let's put a Name to the message, Please Register", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                startActivityForResult(ii, 111);
            }
         }
        }else{
            ChatData alan = new ChatData(sender, msg, deviceID, getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),messageType);
            alanRef.push().setValue(alan);
            try {MyApp.CustomEventAnalytics("chat_sent ", EventChatActivity.SuperCateName, eventDetail.getCategory_name());
            }catch (Exception ex){}
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
        //System.out.println("~~~~~~~~~~"+ eventDetail.getEvent_id()+" :"+StadiumMsgLimit);
        editor.commit();
        //PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(eventDetail.getEvent_id(),""+StadiumMsgLimit).commit();

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

}

