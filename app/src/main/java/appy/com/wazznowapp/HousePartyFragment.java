package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.MyUtill;
import com.firebase.client.Firebase;
import com.mylist.adapters.CannedAdapter;
import com.mylist.adapters.HouseChatListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static appy.com.wazznowapp.ChatStadiumFragment.nahClicked;
import static appy.com.wazznowapp.EventChatActivity.CatID;
import static appy.com.wazznowapp.EventChatActivity.eventDetail;
import static appy.com.wazznowapp.EventChatActivity.eventID;
import static appy.com.wazznowapp.MyApp.FireBaseHousePartyChatNode;
import static appy.com.wazznowapp.R.id.tvAdminMsg1;

/**
 * Created by admin on 8/2/2016.
 */
public class HousePartyFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ListView listView;
    ImageView imgEmoji,keyboard;
    ImageView send;
    HouseChatListAdapter adapter;
    EditText etMsg;
    GridView viewLay;
    //String mPhotoUrl;
    Firebase myFirebaseRef;
    Firebase alanRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    //FragmentActivity activity;
    final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
    boolean cannedFlag = false;
    String userName="";
    int msgLimit = 10;
    InputMethodManager imm;
    LinearLayout linearLayout,linearlayChat,linearCanMsg;
    static boolean addHousePartyFLAG = false;
    SharedPreferences.Editor editor;
    int mPageEndOffset = 0;
    int mPageLimit = 10;
    View vi;
    CannedAdapter cannedAdapter;
    public static ProgressBar pdh;
    int noSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        myFirebaseRef = new Firebase(firebaseURL);

        String SubDomain = "";
        List<String> items = new ArrayList<>();

        String userGroup = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, "");
        if(FireBaseHousePartyChatNode.length()>0 || userGroup.length()>0){
            if(FireBaseHousePartyChatNode.length()>0){
                 items = Arrays.asList(FireBaseHousePartyChatNode.split(","));
            }else if(userGroup.length()>0){
                 items = Arrays.asList(userGroup.split(","));
                 FireBaseHousePartyChatNode = userGroup;
            }

            for (int i = 0; i <items.size() ; i++) {
                String j = items.get(i);
                if(j.contains(CatID)){
                    SubDomain = j;
                }
            }
            if (SubDomain.length()>0){
                alanRef = myFirebaseRef.child(EventChatActivity.SuperCateName+"/ " +eventDetail.getCategory_name()+"/HousepartyChat").child(""+ SubDomain+"/0");
                alanRef.limitToFirst(mPageLimit).startAt(mPageEndOffset);

                //Toast.makeText(getActivity(), "Waiting for your Friends to Join In. Invite some more. More the merrier.", Toast.LENGTH_SHORT).show();

            }else{
                //Toast.makeText(getActivity(), "no SubDomain : "+SubDomain, Toast.LENGTH_SHORT).show();
            }
            }
        MyApp.CustomEventAnalytics("fragment_selected", "houseparty" , EventChatActivity.eventDetail.getCatergory_id());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        linearLayout = (LinearLayout) v.findViewById(R.id.linearTopChat);
        linearlayChat = (LinearLayout) v.findViewById(R.id.linearlayChat);
        listView = (ListView) v.findViewById(R.id.listMain);
        imgEmoji = (ImageView) v.findViewById(R.id.imgEmoji);
        keyboard = (ImageView) v.findViewById(R.id.keyboard);
        send = (ImageView) v.findViewById(R.id.imgSendChat);
        etMsg = (EditText) v.findViewById(R.id.etChatMsg);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        viewLay = (GridView) v.findViewById(R.id.viewLay);
        //     al = new ArrayList<String>();
        linearCanMsg = (LinearLayout) v.findViewById(R.id.linearCanMsg);
        pdh = (ProgressBar) v.findViewById(R.id.pd);

        cannedAdapter = new CannedAdapter(getActivity(), eventDetail.getCannedMessage());
        viewLay.setAdapter(cannedAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        keyboard.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
        viewLay.setOnItemClickListener(this);

        editor = MyApp.preferences.edit();
        try{adapter = new HouseChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
        }catch (Exception e){
            e.printStackTrace();

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            if(!MyApp.preferences.getBoolean(eventID+"HouseParty", false) && !addHousePartyFLAG){
                //linearLayout.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.admin_msg,null);


                TextView tvAdminMsg = (TextView) vi.findViewById(tvAdminMsg1);
                tvAdminMsg.setBackgroundResource(R.drawable.chat_head_);


                LinearLayout linearAdminBtn = (LinearLayout) vi.findViewById(R.id.linearAdminBtn);
                linearAdminBtn.setBackgroundResource(R.drawable.admin_bg);
               // linearLayout.removeAllViews();
                //linearLayout.addView(vi);
                listView.addHeaderView(vi);
                linearAdminBtn.setGravity(Gravity.CENTER);
                TextView btnYes = (TextView) vi.findViewById(R.id.btnAdminMsgYes);
                TextView btnNo = (TextView) vi.findViewById(R.id.btnAdminMsgNo);

                ImageView like = (ImageView) vi.findViewById(R.id.like);
                ImageView dislike = (ImageView) vi.findViewById(R.id.dislike);

                like.setImageResource(R.drawable.add);
                dislike.setImageResource(R.drawable.nothanks);

                btnYes.setText("Invite Friends");
                btnNo.setText("No, Thanks");
                //tvAdminMsg.setText("Invite for House Party. There are most fun.");
                tvAdminMsg.setText(MyApp.alAdmMsg.get(3).get_admin_message());

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


    private void housePartyStarted(){
        //editor = MyApp.preferences.edit();
        //editor.putBoolean(EventChatActivity.eventID + "HouseParty", true);
        //editor.commit();
        Intent ii = new Intent(getActivity(), InviteFriendActivity.class);
        ii.putExtra("EventName", eventDetail.getCatergory_id());
        ii.putExtra("EventID", eventDetail.getEvent_id());
        ii.putExtra("Event", eventDetail.getEvent_title());
        ii.putExtra("message", "");
        ii.putExtra("EventTime", eventDetail.getEvent_start());
        startActivity(ii);
    }


    @Override
    public void onResume() {
        super.onResume();

        if(nahClicked){
            etMsg.setText("");
            viewLay.setAdapter(cannedAdapter);
            linearCanMsg.setVisibility(View.VISIBLE);
            MyUtill.hideKeyBoard(getActivity(),linearCanMsg,true);

            imgEmoji.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);

            //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else{
            imgEmoji.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.GONE);

            try {
                //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                //View view = getActivity().getCurrentFocus();
                userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
                noSend = Integer.parseInt(MyApp.preferences.getString("SendTime: " + EventChatActivity.eventID, "-1"));
                if(!(noSend >= 0 && noSend < 3)){
                    if(noSend == 0) {
                        linearCanMsg.setVisibility(View.GONE);
                        MyUtill.hideKeyBoard(getActivity(),linearCanMsg,false);
                    }else {
                        linearCanMsg.setVisibility(View.GONE);
                    }
                }else{
                    linearCanMsg.setVisibility(View.GONE);
                    MyUtill.hideKeyBoard(getActivity(),linearCanMsg,false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        //View vi = inflater.inflate(R.layout.admin_msg,null);
        listView.removeHeaderView(vi);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        View view = getActivity().getCurrentFocus();
        if (id == R.id.keyboard) {
                /*Handler mHandler= new Handler();
                mHandler.post(
                        new Runnable() {
                            public void run() {*/
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            etMsg.setText("");
            viewLay.setVisibility(View.VISIBLE);
            linearCanMsg.setVisibility(View.VISIBLE);
            imgEmoji.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);

            //Toast.makeText(getActivity(), "Emoji will be shown soon", Toast.LENGTH_SHORT).show();
                /*if (viewLay.getVisibility() == View.VISIBLE) {
                    viewLay.setVisibility(View.GONE);
                } else {
                    viewLay.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Emoji will be shown soon", Toast.LENGTH_SHORT).show();
                }*/

        }
        /*if (id == R.id.imgEmoji) {

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            etMsg.setText("");
            viewLay.setVisibility(View.VISIBLE);
            linearCanMsg.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), "Emoji will be shown soon", Toast.LENGTH_SHORT).show();
        }*/ else if (id == R.id.etChatMsg) {
            viewLay.setVisibility(View.GONE);
            linearCanMsg.setVisibility(View.GONE);
        } else if (id == R.id.imgSendChat) {
            String userGroup = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, "");
            if(FireBaseHousePartyChatNode.length()>0 || userGroup.length()>0){
            if (!(userGroup.contains(CatID)|| FireBaseHousePartyChatNode.contains(CatID))){
                Toast.makeText(getActivity(), "Invite Some Friends First Captain!", Toast.LENGTH_SHORT).show();
            }else {
                {
                    userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
                    if (!TextUtils.isEmpty(userName) || cannedFlag) {
                        if (cannedFlag) {
                            if (view != null) {
                                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            if (viewLay.getVisibility() == View.VISIBLE) {
                                viewLay.setVisibility(View.GONE);
                            } else {
                                viewLay.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Guest User can send only Canned Messages", Toast.LENGTH_SHORT).show();
                            }
                        } else if (!TextUtils.isEmpty(userName)) {
                            String msg = etMsg.getText().toString();
                            if (!TextUtils.isEmpty(msg)) {
                                //al.add(msg);
                                try {
                                    if (!MyApp.preferences.getBoolean("HousePartyMessage" + CatID, false)) {
                                        //linearlayChat.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();
                                        etMsg.setText("");
                                        //ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(), MyApp.preferences.getString(MyApp.USER_TYPE, ""), "normal");

                                        String commentator_privilege = MyApp.preferences.getString("commentator_privilege", "");
                                        ChatData alan;
                                        if (commentator_privilege.contains(eventDetail.getCatergory_id())){
                                            alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(),"com","normal");
                                        }else{
                                            alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),"normal");
                                        }

                                        alanRef.push().setValue(alan);
                                        MyApp.CustomEventAnalytics("chat_sent", EventChatActivity.SuperCateName, " : House Party : " + eventDetail.getCategory_name());

                                        if (msg.contains("#featured") || msg.contains("#Featured") || msg.contains("#FEATURED")) {
                                            MyUtill.addMsgtoFeatured(getActivity(), msg);
                                        }
                                    } else {
                                        //linearlayChat.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "You are not eligible to this group.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {

                                    Log.e("StadiumFragment", "sendMsg ERROR: " + ex.toString());
                                    String SubDomain = "";
                                    List<String> items = new ArrayList<>();

                                    String userGroup1 = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, "");
                                    if(FireBaseHousePartyChatNode.length()>0 || userGroup1.length()>0){
                                        if(FireBaseHousePartyChatNode.length()>0){
                                            items = Arrays.asList(FireBaseHousePartyChatNode.split(","));
                                        }else if(userGroup1.length()>0){
                                            items = Arrays.asList(userGroup1.split(","));
                                            FireBaseHousePartyChatNode = userGroup1;
                                        }

                                        for (int i = 0; i <items.size() ; i++) {
                                            String j = items.get(i);
                                            if(j.contains(CatID)){
                                                SubDomain = j;
                                            }
                                        }
                                        if (SubDomain.length()>0){
                                            alanRef = myFirebaseRef.child(EventChatActivity.SuperCateName+"/ " +eventDetail.getCategory_name()+"/HousepartyChat").child(""+ SubDomain+"/0");
                                            alanRef.limitToFirst(mPageLimit).startAt(mPageEndOffset);
                                            //Toast.makeText(getActivity(), "Waiting for your Friends to Join In. Invite some more. More the merrier.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            //Toast.makeText(getActivity(), "no SubDomain : "+SubDomain, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    try{
                                        adapter = new HouseChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    //ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(), MyApp.preferences.getString(MyApp.USER_TYPE, ""), "normal");

                                    String commentator_privilege = MyApp.preferences.getString("commentator_privilege", "");
                                    ChatData alan;
                                    if (commentator_privilege.contains(eventDetail.getCatergory_id())){
                                        alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(),"com","normal");
                                    }else{
                                        alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),"normal");
                                    }

                                    alanRef.push().setValue(alan);
                                    etMsg.setText("");
                                    MyApp.CustomEventAnalytics("chat_sent", EventChatActivity.SuperCateName, " : House Party : " + eventDetail.getCategory_name());

                                    if (msg.contains("#featured") || msg.contains("#Featured") || msg.contains("#FEATURED")) {
                                        MyUtill.addMsgtoFeatured(getActivity(), msg);
                                    }
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank message not send", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Intent ii = new Intent(getActivity(), NewSignUpActivity.class);
                        startActivity(ii);
                        //startActivityForResult(ii, 111);
                    }
                }
              }
            }else{
                Toast.makeText(getActivity(), "Invite Some Friends First Captain!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        msgLimit+=10;
        alanRef.limit(msgLimit);
        //alanRef.
        adapter = new HouseChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //StadiumMsgLimit+=2;
        String msg = eventDetail.getCannedMessage().get(position);
        try {
            if (!MyApp.preferences.getBoolean("HousePartyMessage" + CatID, false)) {
                //linearlayChat.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                etMsg.setText("");
                ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""),"normal");
                alanRef.push().setValue(alan);
                MyApp.CustomEventAnalytics("chat_sent", EventChatActivity.SuperCateName , " : House Party : "+ eventDetail.getCategory_name());
                if (msg.contains("#featured")||msg.contains("#Featured")||msg.contains("#FEATURED")){
                    MyUtill.addMsgtoFeatured(getActivity(),msg);
                }
            }else{
                //linearlayChat.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"You are not eligible to this group.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.e("StadiumFragment", "sendMsg ERROR: " + ex.toString());

        }
        etMsg.setText("");
    }
}
