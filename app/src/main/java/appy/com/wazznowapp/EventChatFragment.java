package appy.com.wazznowapp;

import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.app.model.ActionItem;
import com.app.model.ConnectDetector;
import com.app.model.EventDetail;
import com.app.model.QuickAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 8/2/2016.
 */
public class EventChatFragment extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ConnectDetector connectDetector;
    static String CateName;
    static String SuperCateName;
    public static String eventID = "";
    static EventDetail eventDetail;
    ListView left_drawer;
    private static final int ID_UP = 1;
    private static final int ID_DOWN = 2;
    QuickAction quickAction;


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
            e.printStackTrace();
            //eventDetail =new EventDetail();
            return;
        }


        if (MyApp.firebaseFlag && connectDetector.getConnection()) {
            init();
        } else {
            Toast.makeText(this, "Please check internet connection. Server Error.", Toast.LENGTH_SHORT).show();
            finish();
        }
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        adapter.addFragment(new FeatureFragment(), "");
        adapter.addFragment(new HousePartyFragment(), "HOUSE PARTY");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        try {
            if (ChatStadiumFragment.linearCanMsg.getVisibility() == View.VISIBLE) {
                View view = this.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                ChatStadiumFragment.linearCanMsg.setVisibility(View.GONE);
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
        }
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
            ActionItemAddText("<br>Mumbai Indians"+i+" Have Won the <br>toss and they elected to bat first.<br><font color='grey'>6:5"+i+" p.m</font><br>");
        }

        //ActionItem nextItem = new ActionItem(ID_DOWN, "\nMumbai Indians Have Won the toss\nand they elected to bat first.", null);
        //ActionItem prevItem = new ActionItem(ID_UP, "\nPankaj Singh to Dhawan, no run,\nangled across and left alone\nwith angled across\nan led across as the test match", null);
        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked

        //prevItem.setSticky(true);
        //nextItem.setSticky(true);

        //add action items into QuickAction
        //quickAction.addActionItem(nextItem);
        //quickAction.addActionItem(prevItem);

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
