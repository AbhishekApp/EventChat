package appy.com.wazznowapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 1/20/2017.
 */
public class FeatureFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ConnectDetector connectDetector;
    ListView listView;
    LinearLayout linearLayout;
    Firebase myFirebaseRef;
    Firebase alanRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    static boolean addHousePartyFLAG = false;
    static boolean addTuneFLAG = false;
    final static String firebaseURL = MyApp.FIREBASE_BASE_URL;
    SharedPreferences.Editor editor;
    String userName="";
    int msgLimit = 3;
    InputMethodManager imm;
    ArrayList<ChatData> alList;
    ArrayList<String> mKeys;
    ChatAdapter chatAdapter;
    TextView headerText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        connectDetector = new ConnectDetector(getActivity());
        if(connectDetector.getConnection()) {
            myFirebaseRef = new Firebase(firebaseURL);
            alanRef = myFirebaseRef.child(EventChatFragment.SuperCateName + "/ " + EventChatFragment.CateName + "/ " + EventChatFragment.eventID).child("FeatureChat");
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            alanRef.keepSynced(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.featured_chat, container, false);
        init(view,savedInstanceState );
        return view;
    }

    private void init(View v,Bundle savedInstanceState) {
        linearLayout = (LinearLayout) v.findViewById(R.id.linearTopChat);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) v.findViewById(R.id.listMain);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        LayoutInflater inflater = getLayoutInflater(savedInstanceState);
        View headerV = inflater.inflate(R.layout.header, listView, false);
        headerText = (TextView)headerV.findViewById(R.id.headerText);

        listView.addHeaderView(headerV, null, false);

        swipeRefreshLayout.setOnRefreshListener(this);
        alList = new ArrayList<ChatData>();
        mKeys = new ArrayList<String>();
        chatAdapter = new ChatAdapter(getActivity(), alList);

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
    }



    @Override
    public void onResume() {
        super.onResume();
        try {
            listView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }
        catch (Exception ex){}
    }


    @Override
    public void onPause() {
        super.onPause();
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

    class ChatAdapter extends BaseAdapter {
        Context con;
        ArrayList<ChatData> alList;
        TextView tvUser;
        TextView tvMsg;
        //TextView btnYes, btnNo;
        LinearLayout linear, linearBtn;
        LinearLayout.LayoutParams relativeParam;
        ImageView imgIcon;
        //  int limit;
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
                view = inflater.inflate(R.layout.featured_layout, null);
                imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
                tvUser = (TextView) view.findViewById(R.id.tvChatUser);
                tvMsg = (TextView) view.findViewById(R.id.tvChat);
                linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
                linearBtn = (LinearLayout) view.findViewById(R.id.linearBtn);
                //btnYes = (TextView) view.findViewById(R.id.btnYesTuneOrInvite);
                //btnNo = (TextView) view.findViewById(R.id.btnNoThanks);
            }else{
                imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
                tvUser = (TextView) view.findViewById(R.id.tvChatUser);
                tvMsg = (TextView) view.findViewById(R.id.tvChat);
                linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);
                linearBtn = (LinearLayout) view.findViewById(R.id.linearBtn);
                //btnYes = (TextView) view.findViewById(R.id.btnYesTuneOrInvite);
                //btnNo = (TextView) view.findViewById(R.id.btnNoThanks);
            }
            if(position < alList.size() && msgLimit < alList.size()) {
                ChatData model = alList.get(alList.size()-msgLimit+position);
                populateView(view, model);
            }
            return view;
        }

        protected void populateView(final View v, ChatData model) {
            try {
                headerText.setText(new SimpleDateFormat("dd-MMM hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(model.getTimestamp())) );
                tvMsg.setText(/*new SimpleDateFormat("dd-MMM hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(model.getTimestamp())) + "\n" +*/ model.getTitle().replace("#", ""));
            }
            catch (Exception e){
                e.printStackTrace();
            }
            tvUser.setText(model.getAuthor());
            linearBtn.setVisibility(View.GONE);
            relativeParam = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            String sender = model.getAuthor();
            String fromUser = model.getToUser();
            String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
            boolean isEqual = sender.equalsIgnoreCase(userName);
            tvMsg.setGravity(Gravity.LEFT);
            tvMsg.setPadding(35,5,10,15);
            tvUser.setGravity(Gravity.LEFT);
            tvUser.setVisibility(View.VISIBLE);
            tvUser.setPadding(35,5,10,5);
            linear.setGravity(Gravity.LEFT);
            linear.setLayoutParams(relativeParam);
            //linear.setBackgroundResource(R.drawable.chat_incomin_background);
            linear.setBackgroundResource(R.drawable.chat_incomin_background);
            linear.setPadding(35,5,80,5);
            linearBtn.setVisibility(View.GONE);
            if(model.getAuthor().equalsIgnoreCase("Admin")) {
                imgIcon.setVisibility(View.VISIBLE);

            }else{
                imgIcon.setVisibility(View.GONE);
                tvMsg.setBackgroundColor(Color.TRANSPARENT);
            }
            if(model.getAuthor().equalsIgnoreCase("Guest User")) {
                //tvUser.setVisibility(View.GONE);
            }
        }
    }

}


