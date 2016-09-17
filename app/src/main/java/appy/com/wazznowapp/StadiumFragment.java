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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.mylist.adapters.StadiumChatListAdapter;

/**
 * Created by admin on 8/2/2016.
 */
public class StadiumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener  {

    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    StadiumChatListAdapter adapter;
    EditText etMsg;
    View viewLay;

    String mPhotoUrl;
    Firebase myFirebaseRef;
    Firebase alanRef;
    private SwipeRefreshLayout swipeRefreshLayout;


    final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
//    final static String firebaseURL = "https://wazznow-cd155.firebaseio.com/EventList/0/Event_Category/2/Stadium";
    private ValueEventListener mConnectedListener;
    private ValueEventListener mDataRetrieveListener;
    boolean cannedFlag = false;
    SharedPreferences.Editor editor;
    boolean flagAdminMsg;
    Handler handler;

    String userName="";
    int msgLimit = 10;
    InputMethodManager imm;

    public StadiumFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        myFirebaseRef = new Firebase(firebaseURL);
        alanRef = myFirebaseRef.child(EventChatFragment.CateName).child("StadiumChat");

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
        viewLay = (View) v.findViewById(R.id.viewLay);
 //     al = new ArrayList<String>();
        swipeRefreshLayout.setOnRefreshListener(this);
        imgEmoji.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
      /*  mConnectedListener = alanRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                 //   Toast.makeText(getActivity(), "Connected to Firebase", Toast.LENGTH_SHORT).show();
                    System.out.println("Firebase connected");
                } else {
                    System.out.println("Firebase not connected");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
             //   Toast.makeText(getActivity(), "error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Server not connect ERROR : "+firebaseError.getMessage());
            }
        });
*/
        adapter = new StadiumChatListAdapter(alanRef.limit(msgLimit), getActivity(), R.layout.chat_layout, "ABHI");

    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){}
        userName = MyApp.preferences.getString(SignUpActivity.USER_NAME, null);
        if(!TextUtils.isEmpty(userName)){
            flagAdminMsg = MyApp.preferences.getBoolean(EventChatFragment.CateName, false);
            if(!flagAdminMsg){
                ChatData alan = new ChatData("Admin", "Congrates now you are part of 2.2k in stadium following the match");
                alanRef.push().setValue(alan);
                editor = MyApp.preferences.edit();
                editor.putBoolean(EventChatFragment.CateName, true);
                editor.commit();
                handler = new Handler();
                handler.postDelayed(runn, 15 * 10000);
            }
        }
    }

    Runnable runn = new Runnable() {
        @Override
        public void run() {
            ChatData alan = new ChatData("Admin", "Start a house party.");
            alanRef.push().setValue(alan);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
     //   alanRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        View view = getActivity().getCurrentFocus();
        if (id == R.id.imgEmoji) {
            if (view != null) {
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (viewLay.getVisibility() == View.VISIBLE) {
                viewLay.setVisibility(View.GONE);
            } else {
                viewLay.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(),"Emoji will be shown soon", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.etChatMsg) {
            viewLay.setVisibility(View.GONE);
        } else if (id == R.id.imgSendChat) {

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
                   //     al.add(msg);
                        adapter.notifyDataSetChanged();
                        etMsg.setText("");
                        ChatData alan = new ChatData(userName, msg);
                        alanRef.push().setValue(alan);

                    }else{
                        Toast.makeText(getActivity(),"Blank message not send", Toast.LENGTH_SHORT).show();
                    }
                }

            }else{
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivity(ii);
//                startActivityForResult(ii, 111);
            }
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
}

