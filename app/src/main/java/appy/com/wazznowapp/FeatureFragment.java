package appy.com.wazznowapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

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
    String userName = "";
    int msgLimit = 3;
    //InputMethodManager imm;
    ArrayList<ChatData> alList;
    ArrayList<String> mKeys;
    FeaturedChatAdapter chatAdapter;
    TextView headerText;
    String longDeepLink = "https://ry5a4.app.goo.gl/?link=$" +
            "&apn=appy.com.wazznowapp" +
            "&afl=$" +
            "&st=WazzNow+Title" +
            "&sd=House+Party+Chat+Invitation" +
            "&si=http://media.appypie.com/appypie-slider-video/images/logo_new.png" +
            "&utm_source=";
    ProgressBar pd;
    String shortLinkURL = "";
    String msg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        connectDetector = new ConnectDetector(getActivity());
        if (connectDetector.getConnection()) {
            myFirebaseRef = new Firebase(firebaseURL);
            alanRef = myFirebaseRef.child(EventChatFragment.SuperCateName + "/ " + EventChatFragment.CateName + "/ " + EventChatFragment.eventID).child("FeatureChat");
            userName = MyApp.preferences.getString(MyApp.USER_NAME, null);
            alanRef.keepSynced(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.featured_chat, container, false);
        init(view, savedInstanceState);
        return view;
    }

    private void init(View v, Bundle savedInstanceState) {

        pd = (ProgressBar) v.findViewById(R.id.pd);
        linearLayout = (LinearLayout) v.findViewById(R.id.linearTopChat);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) v.findViewById(R.id.listMain);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        LayoutInflater inflater = getLayoutInflater(savedInstanceState);
        View headerV = inflater.inflate(R.layout.header, listView, false);
        headerText = (TextView) headerV.findViewById(R.id.headerText);

        listView.addHeaderView(headerV, null, false);

        swipeRefreshLayout.setOnRefreshListener(this);
        alList = new ArrayList<ChatData>();
        mKeys = new ArrayList<String>();
        chatAdapter = new FeaturedChatAdapter(getActivity(), alList);

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
                Toast.makeText(getActivity(), "Failed to load comments.", Toast.LENGTH_SHORT).show();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        msgLimit += 5;
        chatAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        msgLimit += 2;

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

    public String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    class FeaturedChatAdapter extends BaseAdapter {
        Context con;
        ArrayList<ChatData> alList;
        TextView tvUser;
        TextView tvMsg;
        //TextView btnYes, btnNo;
        LinearLayout linear;
        LinearLayout.LayoutParams relativeParam;
        ImageView imgIcon;
        ImageView share;

        //  int limit;
        public FeaturedChatAdapter(Context context, ArrayList<ChatData> al) {
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
            return alList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(con.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.featured_layout, null);
                imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
                tvUser = (TextView) view.findViewById(R.id.tvChatUser);
                tvMsg = (TextView) view.findViewById(R.id.tvChat);
                linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);

                //btnYes = (TextView) view.findViewById(R.id.btnYesTuneOrInvite);
                //btnNo = (TextView) view.findViewById(R.id.btnNoThanks);
            } else {
                imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
                tvUser = (TextView) view.findViewById(R.id.tvChatUser);
                tvMsg = (TextView) view.findViewById(R.id.tvChat);
                linear = (LinearLayout) view.findViewById(R.id.linearMsgChat);

                //btnYes = (TextView) view.findViewById(R.id.btnYesTuneOrInvite);
                //btnNo = (TextView) view.findViewById(R.id.btnNoThanks);
            }

            share = (ImageView) view.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "whatsapp", Toast.LENGTH_SHORT).show();
                    new newShortAsync().execute();
                }
            });


          //  if (position < alList.size() && msgLimit <= alList.size()) {
            try {
                ChatData model = alList.get(alList.size() - msgLimit + position);
                populateView(view, model);
            }
            catch(Exception e){
                // for now eat exceptions
            }
           // } else {
            //    System.out.println("error");
            //}
            return view;
        }

        protected void populateView(final View v, ChatData model) {
            try {
                headerText.setText(new SimpleDateFormat("dd-MMM hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(model.getTimestamp())));
                tvMsg.setText(model.getTitle().replace("#featured", "").replace("#Featured", "").replace("#FEATURED", ""));
                if (tvMsg.getText().toString().trim().length()>1)
                share.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvUser.setText(model.getAuthor());
            relativeParam = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            String sender = model.getAuthor();
            String fromUser = model.getToUser();
            String userName = MyApp.preferences.getString(MyApp.USER_NAME, "");
            boolean isEqual = sender.equalsIgnoreCase(userName);
            tvMsg.setGravity(Gravity.LEFT);
            tvMsg.setPadding(5, 5, 10, 15);
            tvUser.setGravity(Gravity.LEFT);
            tvUser.setVisibility(View.VISIBLE);
            tvUser.setPadding(35, 5, 10, 5);
            linear.setGravity(Gravity.LEFT);
            linear.setLayoutParams(relativeParam);
            linear.setVisibility(View.VISIBLE);
            linear.setPadding(35, 5, 80, 5);
            if (model.getAuthor().equalsIgnoreCase("Admin")) {
                imgIcon.setVisibility(View.VISIBLE);
            } else {
                imgIcon.setVisibility(View.GONE);
                tvMsg.setBackgroundColor(Color.TRANSPARENT);
            }
            if (model.getAuthor().equalsIgnoreCase("Guest User")) {
                //tvUser.setVisibility(View.GONE);
            }
        }
    }


    public class newShortAsync extends AsyncTask<Void, Void, String> {

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
            String res = null;
            String json = "{\"longUrl\": \"" + longDeepLink.replace("$", getResources().getString(R.string.apk_link)) + "\"}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key=" + getResources().getString(R.string.google_shortlink_api_key));
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

                int status = con.getResponseCode();
                InputStream inputStream;
                if (status == HttpURLConnection.HTTP_OK)
                    inputStream = con.getInputStream();
                else
                    inputStream = con.getErrorStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                res = buffer.toString();
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
            String response = s;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String id = jsonObject.getString("id");
                shortLinkURL = id;
                Intent sendIntent = new Intent(getActivity(), ShareEventActivity.class);
                if (!TextUtils.isEmpty(userName)) {
                    if (!userName.contains("user")) {
                        msg = "Hi, This is " + userName + ". Watch the " + id + " with me right here on WazzNow.";
                    } else {
                        msg = "Hi,  Watch the " + id + " with me right here on WazzNow.";
                    }
                } else {
                    msg = "Hi,  Watch the " + id + " with me right here on WazzNow.";
                }
                //Uri uri = buildDeepLink("http://d2wuvg8krwnvon.cloudfront.net/customapps/WazzNow.apk", 2, true);
                //  String dLink = longDeepLink.replace("SenderID", eventID);
                //sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra("share", msg);
                /*sendIntent.setType("text/plain");*/
                //sendIntent.setPackage("com.whatsapp");
                try {
                    startActivity(sendIntent);
                    //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                }
                pd.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}


