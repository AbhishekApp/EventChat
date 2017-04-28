package com.get.wazzon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by manish on 8/2/2016.
 */
public class EventChatActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ConnectDetector connectDetector;
    public static String CateName;
    public static String SuperCateName;
    public static String eventID = "";
    public static String CatID = "";
    public static EventDetail eventDetail;
    ListView left_drawer;
    private static final int ID_UP = 1;
    private static final int ID_DOWN = 2;
    QuickAction quickAction;
    private Firebase mDatabaseRefrenceSync,mDatabaseRefrenceSync1;
    ArrayList<ChatData> alList = new ArrayList<ChatData>();
    ChildEventListener childEventListener,childEventListener1;
    ArrayList<String> mKeys = new ArrayList<>();
    boolean moreThanDay=false;
    private String NotificationMessageToShow="";
    String toDisplay="";
    static boolean localFlag = true;

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
                CatID = eventDetail.getCatergory_id();
                NotificationMessageToShow = getIntent().getStringExtra("NotificationMessageToShow");

                MyApp.PreDefinedEventAnalytics("view_item_list",eventDetail.getCategory_name(),eventID);

            }
        } catch (Exception e) {
             e.printStackTrace(); // for now eat exceptions
            //eventDetail =new EventDetail();
            return;
        }

        if (MyApp.firebaseFlag && connectDetector.getConnection()) {
            init();
            /**************************************************Fetching Commentator from Stadium***********************************************************/
            alList.clear();

            mDatabaseRefrenceSync = new Firebase(MyApp.FIREBASE_BASE_URL).child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name() + "/ " + eventDetail.getEvent_title() + "/ " + EventChatActivity.eventID).child("StadiumChat");
            //DatabaseReference myRef = database.getReference("users");
            mDatabaseRefrenceSync.keepSynced(true);
            childEventListener = new ChildEventListener(){
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    //Insert into the correct location, based on previousChildName
                    ChatData model = dataSnapshot.getValue(ChatData.class);
                    if(model.getAuthorType().equals("com")){
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
                    //Log.d("ChatStadiumFragment", "onChildRemoved:" + commentKey.toString());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("ChatStadiumFragment", "onChildMoved:" + dataSnapshot.getKey());
                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();
                    //Log.d("ChatStadiumFragment", "onChildMoved:" + movedComment.toString());
                    //Log.d("ChatStadiumFragment", "onChildMoved:" + commentKey.toString());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    //Log.w("ChatStadiumFragment", "postComments:onCancelled", firebaseError.toException());
                    Toast.makeText(EventChatActivity.this, "Failed to load comments.",Toast.LENGTH_SHORT).show();
                }
            };
            mDatabaseRefrenceSync.addChildEventListener(childEventListener);

            /**************************************************Fetching Commentator from House Party ***********************************************************/

            mDatabaseRefrenceSync1 = new Firebase(MyApp.FIREBASE_BASE_URL).child(EventChatActivity.SuperCateName + "/ " + eventDetail.getCategory_name() + "/ " + eventDetail.getEvent_title() + "/ " + EventChatActivity.eventID).child("HousepartyChat");
            //DatabaseReference myRef = database.getReference("users");
            mDatabaseRefrenceSync1.keepSynced(true);
            childEventListener1 = new ChildEventListener(){
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    //Insert into the correct location, based on previousChildName
                    ChatData model = dataSnapshot.getValue(ChatData.class);
                    if(model.getAuthorType().equals("com")){
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
                    }
                    //System.out.println(alList.toString());
                    Collections.sort(alList, new CustomComparator());
                    //System.out.println(alList.toString());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    //Log.d("ChatStadiumFragment", "onChildChanged:" + dataSnapshot.getKey());
                    //Log.d("ChatStadiumFragment", "onChildChanged previousChildName :" + previousChildName);
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    String key = dataSnapshot.getKey();
                    ChatData newModel = dataSnapshot.getValue(ChatData.class);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //Log.d("ChatStadiumFragment", "onChildRemoved:" + dataSnapshot.getKey());
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();
                    //Log.d("ChatStadiumFragment", "onChildRemoved:" + commentKey.toString());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    //Log.d("ChatStadiumFragment", "onChildMoved:" + dataSnapshot.getKey());
                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();
                    //Log.d("ChatStadiumFragment", "onChildMoved:" + movedComment.toString());
                    //Log.d("ChatStadiumFragment", "onChildMoved:" + commentKey.toString());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    //Log.w("ChatStadiumFragment", "postComments:onCancelled", firebaseError.toException());
                    Toast.makeText(EventChatActivity.this, "Failed to load comments.",Toast.LENGTH_SHORT).show();
                }
            };
            mDatabaseRefrenceSync1.addChildEventListener(childEventListener1);
        } else {
            Toast.makeText(this, "Please check internet connection. Server Error.", Toast.LENGTH_SHORT).show();
        }
    }

    public class CustomComparator implements Comparator<ChatData> {
        @Override
        public int compare(ChatData o1, ChatData o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }


    private void init() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(CateName);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                System.out.println("on changing the page make respected tab selected");
                //actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                System.out.println("onPageScrolled tab selected");
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                System.out.println("onPageScrollStateChanged tab selected");
            }
        });

        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        AlertDialog.Builder alert = new AlertDialog.Builder(EventChatActivity.this)
                .setTitle("WazzNow")
                .setMessage(NotificationMessageToShow)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(R.drawable.ic_launcher);

        try {
            if (NotificationMessageToShow != null) {
                if (NotificationMessageToShow.length() > 0) {
                    alert.show();
                    NotificationMessageToShow="";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        MenuItem signup = menu.findItem(R.id.menu_signup);
        if (MyApp.preferences.getString("isSignupSuccessful", "false").equals("true")) {
            signup.setVisible(false);
        }else{
            signup.setVisible(true);
        }
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
            Intent ii = new Intent(this, NewSignUpActivity.class);
            startActivity(ii);
        } else if (id == R.id.menu_info) {
            Intent ii = new Intent(this, InfoActivity.class);
            startActivity(ii);
        } /*else if (id == R.id.menu_more) {
            Toast.makeText(this, "More is coming soon", Toast.LENGTH_SHORT).show();
            //MyUtill.alertDialogShowUpdate(EventChatActivity.this);
        }*/ else if (id == R.id.menu_noti) {
            //Toast.makeText(this, "Notification is coming soon", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatStadiumFragment(), "STADIUM");
        adapter.addFragment(new FeatureFragment(), "FEATURED");
        adapter.addFragment(new HousePartyFragment(), "HOUSE PARTY");
        adapter.addFragment(new WonHistory(), "WON's");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    protected void ActionItemAddText(String str){
        quickAction.addActionItem(new ActionItem(ID_DOWN, str, null));
    }

    public String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();
        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }


    protected void ActionItemAddLine(){
        quickAction.addActionItem(new ActionItem(ID_DOWN, "", getResources().getDrawable(R.drawable.line_new)));
    }

    public void showPopup(View view) {
        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
        //orientation
        quickAction = new QuickAction(this, QuickAction.VERTICAL);
        quickAction.addActionItem(new ActionItem(ID_DOWN, "", getResources().getDrawable(R.drawable.ic_launcher)));

        toDisplay = MyApp.alAdmMsg.get(5).get_admin_message();

        for(int i = 0; i < alList.size() ; i++){
            ActionItemAddLine();
            String time="";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date sDate = sdf.parse(alList.get(i).getTimestamp());
                long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
                if (new Date().after(sDate)) {
                    moreThanDay = Math.abs(new Date().getTime() - sDate.getTime()) > MILLIS_PER_DAY;
                        time = new SimpleDateFormat("hh:mm a").format(new Date(sDate.getTime())) ;
                        ActionItemAddText("<br>"+/*toTitleCase(*/alList.get(i).getTitle()/*)*/+"<br><br><font color='grey'>"+time.toLowerCase()+"</font><br>");
                }
                else{
                    //back date
                    System.out.println("back date");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        ActionItemAddLine();
        quickAction.addActionItem(new ActionItem(ID_DOWN, "<br>"+eventDetail.getEvent_title()+" "+toDisplay+"<br>", null));

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);
                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == ID_DOWN) {
                    quickAction.onDismiss();
                    //dismissDialog(actionId);
                    //Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                }  else {
                    //Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
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