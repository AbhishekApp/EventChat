package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.firebase.client.Firebase;
import com.mylist.adapters.StadiumChatListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by admin on 8/2/2016.
 */
public class HousePartyFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    StadiumChatListAdapter adapter;
    EditText etMsg;
    GridView viewLay;
    String mPhotoUrl;
    Firebase myFirebaseRef;
    Firebase alanRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    FragmentActivity activity;
    final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
//    final static String firebaseURL = "https://wazznow-cd155.firebaseio.com/EventList/1/Event_Category/2/HouseParty";
    //private ValueEventListener mConnectedListener;
    //private ValueEventListener mDataRetrieveListener;
    boolean cannedFlag = false;
    String userName="";
    int msgLimit = 10;
    InputMethodManager imm;
    LinearLayout linearLayout,linearlayChat;
    static boolean addHousePartyFLAG = false;
    SharedPreferences.Editor editor;

    public HousePartyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        myFirebaseRef = new Firebase(firebaseURL);
        alanRef = myFirebaseRef.child(EventChatFragment.SuperCateName + "/ " + EventChatFragment.CateName + "/ " + EventChatFragment.eventID).child("HousepartyChat");
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
        send = (ImageView) v.findViewById(R.id.imgSendChat);
        etMsg = (EditText) v.findViewById(R.id.etChatMsg);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        viewLay = (GridView) v.findViewById(R.id.viewLay);
        //     al = new ArrayList<String>();

        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);

        editor = MyApp.preferences.edit();
        adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if(!MyApp.preferences.getBoolean(EventChatFragment.eventID+"HouseParty", false) && !addHousePartyFLAG){
                //linearLayout.removeAllViews();
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
                tvAdminMsg.setText("Invite for House Party. There are most fun.");
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
        //editor.putBoolean(EventChatFragment.eventID + "HouseParty", true);
        //editor.commit();
        Intent ii = new Intent(getActivity(), InviteFriendActivity.class);
        ii.putExtra("EventName", EventChatFragment.eventDetail.getCatergory_id());
        ii.putExtra("EventID", EventChatFragment.eventDetail.getEvent_id());
        startActivity(ii);
    }


    @Override
    public void onResume() {
        super.onResume();
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

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        View view = getActivity().getCurrentFocus();
        if (id == R.id.imgEmoji) {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (viewLay.getVisibility() == View.VISIBLE) {
                viewLay.setVisibility(View.GONE);
            } else {
                viewLay.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Emoji will be shown soon", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.etChatMsg) {
            viewLay.setVisibility(View.GONE);
        } else if (id == R.id.imgSendChat) {
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            if(!TextUtils.isEmpty(userName) || cannedFlag) {
                if(cannedFlag){
                    if (view != null) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (viewLay.getVisibility() == View.VISIBLE) {
                        viewLay.setVisibility(View.GONE);
                    } else {
                        viewLay.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"Guest User can send only Canned Messages", Toast.LENGTH_SHORT).show();
                    }
                }else if(!TextUtils.isEmpty(userName)){
                    String msg = etMsg.getText().toString();
                    if (!TextUtils.isEmpty(msg)) {
                        //al.add(msg);
                        try {
                            if (!MyApp.preferences.getBoolean("HousePartyMessage" + EventChatFragment.eventID, false)) {
                                //linearlayChat.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                                etMsg.setText("");
                                ChatData alan = new ChatData(userName, msg, MyApp.getDeviveID(getActivity()), getCurrentTimeStamp(),MyApp.preferences.getString(MyApp.USER_TYPE, ""));
                                alanRef.push().setValue(alan);
                            }else{
                                //linearlayChat.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),"You are not eligible to this group.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Log.e("StadiumFragment", "sendMsg ERROR: " + ex.toString());
                        }
                    }else{
                        Toast.makeText(getActivity(),"Blank message not send", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivity(ii);
                //startActivityForResult(ii, 111);
            }
        }
    }

    @Override
    public void onRefresh() {
        msgLimit+=10;
        alanRef.limit(msgLimit);
        //alanRef.
        adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout);
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
}
