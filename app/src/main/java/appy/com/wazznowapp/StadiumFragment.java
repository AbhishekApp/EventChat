package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.app.model.UserProfile;
import com.firebase.client.Firebase;
import com.mylist.adapters.CannedAdapter;
import com.mylist.adapters.StadiumChatListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 8/2/2016.
 */
public class StadiumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    ConnectDetector connectDetector;
    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    StadiumChatListAdapter adapter;
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

    boolean cannedFlag = false;
    SharedPreferences.Editor editor;
    boolean flagAdminMsg;


    String userName="";
    int msgLimit = 5;
    InputMethodManager imm;
    String subscribedGroup;

    public StadiumFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        connectDetector = new ConnectDetector(getActivity());
        if(connectDetector.getConnection()) {
            myFirebaseRef = new Firebase(firebaseURL);
            alanRef = myFirebaseRef.child(EventChatFragment.SuperCateName + "/ " + EventChatFragment.CateName + "/ " + EventChatFragment.eventID).child("StadiumChat");
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);

        int noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatFragment.eventID, "0"));
        if(!(noSend > 0 && noSend < 3)){
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }


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

        linearCanMsg.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
        viewLay.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (!MyApp.preferences.getBoolean(EventChatFragment.eventID, false)) {
              adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);

                if(!addTuneFLAG){
                    addTuneFLAG = true;
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.admin_msg,null);
                    linearLayout.addView(v);
                    TextView tvAdminMsg = (TextView) v.findViewById(R.id.tvAdminMsg1);
                    TextView btnYes = (TextView) v.findViewById(R.id.btnAdminMsgYes);
                    TextView btnNo = (TextView) v.findViewById(R.id.btnAdminMsgNo);

                    tvAdminMsg.setText("Congrats now you are part of "+EventChatFragment.eventDetail.getSubscribed_user()+"+ in stadium following the match");

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
                            editor.putBoolean(EventChatFragment.eventID, true);
                            editor.commit();

                            /*  Update user, Subscribe this event */
                            UserProfile profile = new UserProfile();
                            profile.updateUserGroup(getActivity(), EventChatFragment.eventID);
                            updateEventList();
                            /*  Update user, Subscribe this event */

                            listView.setAdapter(adapter);

                            adapter.notifyDataSetChanged();
                            if(!addHousePartyFLAG){
                                addHousePartyFLAG = true;
                         //     linearLayout.removeAllViews();
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                                View vi = inflater.inflate(R.layout.admin_msg, null);
                                linearLayout.addView(vi);
                                TextView tvAdminMsg = (TextView) vi.findViewById(R.id.tvAdminMsg1);
                                TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
                                TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);
                                btnYes.setText("Invite Friends");
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
                        }
                    });
                }
            }else if(!MyApp.preferences.getBoolean(EventChatFragment.eventID+"HouseParty", false) && !addHousePartyFLAG){

            //    linearLayout.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.admin_msg,null);
                linearLayout.removeAllViews();
                linearLayout.addView(vi);
                TextView tvAdminMsg = (TextView) vi.findViewById(R.id.tvAdminMsg1);
                TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
                TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);
                btnYes.setText("Invite Friends");
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
        startActivity(ii);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            adapter = new StadiumChatListAdapter(alanRef.limit(5), getActivity(), R.layout.chat_layout);
            int noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatFragment.eventID, "0"));
            if(noSend > 0){
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if(noSend < 3){
                    linearCanMsg.setVisibility(View.VISIBLE);
                }
            }
         //
        }catch (Exception e){}
        userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
        if(!TextUtils.isEmpty(userName) && !userName.equalsIgnoreCase("Guest User")){
            flagAdminMsg = MyApp.preferences.getBoolean(EventChatFragment.eventID, false);
            subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
            if (subscribedGroup.contains(EventChatFragment.eventID)) {

            }

        }

    }


    @Override
    public void onClick(View v) {
        try{
        int id = v.getId();
        View view = getActivity().getCurrentFocus();
        if (id == R.id.imgEmoji) {

        } else if (id == R.id.etChatMsg) {
            linearCanMsg.setVisibility(View.GONE);
        } else if (id == R.id.imgSendChat) {

            if(!TextUtils.isEmpty(userName)) {
              if(!TextUtils.isEmpty(userName) && !userName.contains("Guest")) {
                    String msg = etMsg.getText().toString();
                    subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
                    if (subscribedGroup.contains(EventChatFragment.eventID)) {
                        if (!TextUtils.isEmpty(msg)) {
                            //     al.add(msg);
                            adapter.notifyDataSetChanged();
                            etMsg.setText("");
                            sendMsg(msg);

                        } else {
                            Toast.makeText(getActivity(), "Blank message not send", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "You are not tuned in this Event Group", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent ii = new Intent(getActivity(), SignUpActivity.class);
                    startActivityForResult(ii, 111);
                }
              /*  if(userName.equalsIgnoreCase("Guest User")){
                    Toast.makeText(getActivity(), "Canned messages coming soon for guest users", Toast.LENGTH_SHORT).show();
                }*/

            }else{
             //   Toast.makeText(getActivity(), "Unregister user can send only canned message", Toast.LENGTH_SHORT).show();
                if (view != null) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
           //     linearCanMsg.setVisibility(View.VISIBLE);
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
               // startActivity(ii);
                startActivityForResult(ii, 111);
            }
        }
        }catch (Exception ex){

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111){
            if(resultCode == 101){
                int noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatFragment.eventID, "0"));
                if(noSend < 3) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (linearCanMsg.getVisibility() == View.VISIBLE) {
                        linearCanMsg.setVisibility(View.GONE);
                    } else {
                        linearCanMsg.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(getActivity(), "You have already send free messages.", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    @Override
    public void onRefresh() {
        msgLimit+=10;
        alanRef.limit(msgLimit);
//        alanRef.
        adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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


            int noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatFragment.eventID, "0"));
            try {
                if (!MyApp.preferences.getBoolean("HousePartyMessage" + EventChatFragment.eventID, false)) {
                    SharedPreferences.Editor editor = MyApp.preferences.edit();
                    editor.putBoolean("HousePartyMessage" + EventChatFragment.eventID, true);
                    editor.commit();
                    //   sendHousePartyMsg();
                }
            } catch (Exception ex) {
                Log.e("StadiumFragment", "sendMsg ERROR: " + ex.toString());
            }
            if (noSend < 3) {
                if (noSend == 0) {
                    listView.setAdapter(adapter);
                    updateEventList();
                }
                noSend++;
                SharedPreferences.Editor editor = MyApp.preferences.edit();
                editor.putString("SendTime: " + EventChatFragment.eventID, String.valueOf(noSend));
                editor.putBoolean(EventChatFragment.eventID, true);

                editor.commit();
                ChatData alan = new ChatData(sender, msg, deviceID);
                alanRef.push().setValue(alan);
                UserProfile profile = new UserProfile();
                profile.updateUserGroup(getActivity(), EventChatFragment.eventID);
                // onRefresh();

            } else {
                Toast.makeText(getActivity(), "For send more messages you have to register", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivityForResult(ii, 111);
//                return;
            }
         }

        }else{
            ChatData alan = new ChatData(sender, msg, deviceID);
            alanRef.push().setValue(alan);
        }

    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        addTuneFLAG = false;
        addHousePartyFLAG = false;
    }
}

