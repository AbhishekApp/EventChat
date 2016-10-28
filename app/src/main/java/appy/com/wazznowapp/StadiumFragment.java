package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.UserProfile;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.mylist.adapters.CannedAdapter;
import com.mylist.adapters.StadiumChatListAdapter;

/**
 * Created by admin on 8/2/2016.
 */
public class StadiumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    StadiumChatListAdapter adapter;
    EditText etMsg;
    LinearLayout linearCanMsg;
    GridView viewLay;

    Firebase myFirebaseRef;
    Firebase alanRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    CannedAdapter cannedAdapter;


    final static String firebaseURL = MyApp.FIREBASE_BASE_URL;

    boolean cannedFlag = false;
    SharedPreferences.Editor editor;
    boolean flagAdminMsg;
    Handler handler;

    String userName="";
    int msgLimit = 30;
    InputMethodManager imm;
    String subscribedGroup;

    public StadiumFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        myFirebaseRef = new Firebase(firebaseURL);
        alanRef = myFirebaseRef.child(EventChatFragment.SuperCateName+"/ "+EventChatFragment.CateName+"/ "+EventChatFragment.eventID).child("StadiumChat");
        userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
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
                adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout, "ABHI");
                ChatData alan = new ChatData("Admin", "Congrates now you are part of 2.2k in stadium following the match", MyApp.preferences.getString("Android_ID", null));
                alanRef.push().setValue(alan);
                editor = MyApp.preferences.edit();
                editor.putBoolean(EventChatFragment.eventID, true);
                editor.commit();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            adapter = new StadiumChatListAdapter(alanRef.limit(30), getActivity(), R.layout.chat_layout, "ABHI");
            listView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }catch (Exception e){}

        if(!TextUtils.isEmpty(userName) && !userName.equalsIgnoreCase("Guest User")){
            flagAdminMsg = MyApp.preferences.getBoolean(EventChatFragment.eventID, false);
            subscribedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
            if (subscribedGroup.contains(EventChatFragment.eventID)) {}
            if(!flagAdminMsg){

                handler = new Handler();
                handler.postDelayed(runn, 20 * 1000);
            }
        }

    }

    Runnable runn = new Runnable() {
        @Override
        public void run() {
            ChatData alan = new ChatData("Admin", "Start a house party, there are most fun.",  MyApp.preferences.getString("Android_ID", null));
            alanRef.push().setValue(alan);
        }
    };



    @Override
    public void onClick(View v) {
        try{
        int id = v.getId();
        View view = getActivity().getCurrentFocus();
        if (id == R.id.imgEmoji) {
            if (view != null) {
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (linearCanMsg.getVisibility() == View.VISIBLE) {
                linearCanMsg.setVisibility(View.GONE);
            } else {
                linearCanMsg.setVisibility(View.VISIBLE);
       //         Toast.makeText(getActivity(),"Emoji will be shown soon", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.etChatMsg) {
            linearCanMsg.setVisibility(View.GONE);
        } else if (id == R.id.imgSendChat) {

            if(!TextUtils.isEmpty(userName) || cannedFlag) {
                if(cannedFlag){

                    if (view != null) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    if (linearCanMsg.getVisibility() == View.VISIBLE) {
                        linearCanMsg.setVisibility(View.GONE);
                    } else {
                        linearCanMsg.setVisibility(View.VISIBLE);
                       // Toast.makeText(getActivity(),"Guest User can send only Canned Messages", Toast.LENGTH_SHORT).show();
                    }
                }else if(!TextUtils.isEmpty(userName) && !userName.equalsIgnoreCase("Guest User")) {
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
                    }
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
                linearCanMsg.setVisibility(View.VISIBLE);
//                Intent ii = new Intent(getActivity(), SignUpActivity.class);
//                startActivity(ii);
//                startActivityForResult(ii, 111);
            }
        }
        }catch (Exception ex){

        }
    }


    @Override
    public void onRefresh() {
        msgLimit+=10;
        alanRef.limit(msgLimit);
//        alanRef.
        adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout, "ABHI");
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
        String sender = MyApp.preferences.getString(MyApp.USER_NAME, "Guest");
        if(sender.equalsIgnoreCase("Guest") || TextUtils.isEmpty(sender)){
            int noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: "+EventChatFragment.eventID, "0"));
            if(noSend < 3){
                noSend++;
                SharedPreferences.Editor editor = MyApp.preferences.edit();
                editor.putString("SendTime: "+EventChatFragment.eventID, String.valueOf(noSend));
                editor.commit();
                ChatData alan = new ChatData(sender, msg, deviceID);
                alanRef.push().setValue(alan);
                UserProfile profile = new UserProfile();
                profile.updateUserGroup(getActivity(), EventChatFragment.eventID);
                onRefresh();
            }else{
                Toast.makeText(getActivity(), "For send more messages you have to register", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivity(ii);
//                return;
            }

        }else{

        }

    }


}

