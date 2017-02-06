package appy.com.wazznowapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.app.model.ActionItem;
import com.app.model.ChatData;
import com.app.model.ConnectDetector;
import com.app.model.EventDetail;
import com.app.model.QuickAction;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import static appy.com.wazznowapp.MyApp.StadiumMsgLimit;

/**
 * Created by admin on 8/2/2016.
 */
public class EventChatActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ConnectDetector connectDetector;
    public static String CateName;
    public static String SuperCateName;
    public static String eventID = "";
    public static EventDetail eventDetail;
    ListView left_drawer;
    private static final int ID_UP = 1;
    private static final int ID_DOWN = 2;
    QuickAction quickAction;
    private Firebase mDatabaseRefrenceSync;
    ArrayList<ChatData> alList = new ArrayList<ChatData>();
    ChildEventListener childEventListener;
    ArrayList<String> mKeys = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_chat_fragment);
        connectDetector = new ConnectDetector(this);
        try {
            if (getIntent().hasExtra("EventDetail")) {
                eventDetail = (EventDetail) getIntent().getSerializableExtra("EventDetail");
                SuperCateName = eventDetail.getSuper_category_name();
                CateName = eventDetail.getEvent_title();
                eventID = eventDetail.getEvent_id();
            }
        } catch (Exception e) {
            //e.printStackTrace(); // for now eat exceptions
            //eventDetail =new EventDetail();
            return;
        }

        if (MyApp.firebaseFlag && connectDetector.getConnection()) {
            init();


            /*mDatabaseRefrenceSync.orderByChild("authorType").equalTo("com").addListenerForSingleValueEvent(new ValueEventListener(){


                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                }

                @Override
               public void onCancelled(FirebaseError firebaseError) {

               }
           });*/


            /*mDatabaseRefrenceSync.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                        Toast.makeText(getApplicationContext(), "XXXXXX", Toast.LENGTH_LONG).show();
                    }
                    for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                        fb.addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(DataSnapshot xdataSnapshot) {
                                Toast.makeText(getApplicationContext(), "YYYYYYY", Toast.LENGTH_LONG).show();
                            }
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
                public void onCancelled(FirebaseError firebaseError) {
                }
            });*/

            /*for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Schedule msg = postSnapshot.getValue(Schedule.class);
                System.out.println(msg.getHall().getId());
            }*/

            /*mDatabaseRefrenceSync.orderByChild("authorType").equalTo("com").addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.e("EventChatActivity:::::", "onChildAdded:" + dataSnapshot.getKey());
                    *//*ChatData model = dataSnapshot.getValue(ChatData.class);
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
                    }*//*
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            mDatabaseRefrenceSync = new Firebase(MyApp.FIREBASE_BASE_URL).child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name() + "/ " + eventDetail.getEvent_title() + "/ " + EventChatActivity.eventID).child("StadiumChat");
            //DatabaseReference myRef = database.getReference("users");
            mDatabaseRefrenceSync.keepSynced(true);

            childEventListener = new ChildEventListener(){
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    // Insert into the correct location, based on previousChildName
                    ChatData model = dataSnapshot.getValue(ChatData.class);

                    if(model.getAuthorType().equals("com")){
                        Log.e("EventChatFragment", "onChildAdded:" + model.getAuthorType());
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
                    }
                        System.out.println(alList.toString());
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("ChatStadiumFragment", "onChildChanged:" + dataSnapshot.getKey());
                    Log.d("ChatStadiumFragment", "onChildChanged previousChildName :" + previousChildName);
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    String key = dataSnapshot.getKey();
                    ChatData newModel = dataSnapshot.getValue(ChatData.class);


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
                    Toast.makeText(EventChatActivity.this, "Failed to load comments.",Toast.LENGTH_SHORT).show();
                }
            };

            mDatabaseRefrenceSync.addChildEventListener(childEventListener);



        } else {
            Toast.makeText(this, "Please check internet connection. Server Error.", Toast.LENGTH_SHORT).show();
            finish();
        }

        //init();
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(CateName);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View menuItemView = findViewById(R.id.menu_noti);
                menuItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //asdad

                        showPopup(v);

                        //Toast.makeText(this, "Notification is coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            View view = this.getCurrentFocus();
            ChatStadiumFragment.linearCanMsg.setVisibility(View.GONE);
            finish();
        } else if (id == R.id.menu_signup) {
            Intent ii = new Intent(this, SignUpActivity.class);
            startActivity(ii);
        } else if (id == R.id.menu_info) {
            Intent ii = new Intent(this, InfoActivity.class);
            startActivity(ii);
        } else if (id == R.id.menu_more) {
            Toast.makeText(this, "More is coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_noti) {
//            Toast.makeText(this, "Notification is coming soon", Toast.LENGTH_SHORT).show();
        }
        /*ImageButton locButton = (ImageButton) findViewById(R.id.menu_noti);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });*/
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatStadiumFragment(), "STADIUM");
        adapter.addFragment(new FeatureFragment(), "FEATURED");
        adapter.addFragment(new HousePartyFragment(), "HOUSE PARTY");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    protected void ActionItemAddText(String str){
        quickAction.addActionItem(new ActionItem(ID_DOWN, str, null));
    }

    protected void ActionItemAddLine(){
        quickAction.addActionItem(new ActionItem(ID_DOWN, "", getResources().getDrawable(R.drawable.line_new)));
    }

    public void showPopup(View view) {
        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
        //orientation
        quickAction = new QuickAction(this, QuickAction.VERTICAL);
        quickAction.addActionItem(new ActionItem(ID_DOWN, "", getResources().getDrawable(R.drawable.ic_launcher)));

        for(int i = 0; i < 5 ; i++){
            ActionItemAddLine();
            ActionItemAddText("<br>Mumbai Indians"+i+" Have Won the toss and they elected to bat first.<br><font color='grey'>6:5"+i+" p.m</font><br>");
        }


        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);
                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == ID_DOWN) {
                    quickAction.onDismiss();
                    //dismissDialog(actionId);
                    Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();

                }  else {
                    Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
        //by clicking the area outside the dialog.
        quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {
           //Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
            }
        });
        quickAction.show(view);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
