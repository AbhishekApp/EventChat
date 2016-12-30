package appy.com.wazznowapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.app.model.ConnectDetector;
import com.app.model.EventDetail;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by admin on 8/2/2016.
 */
public class EventChatFragment extends AppCompatActivity  {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ConnectDetector connectDetector;
    static String CateName;
    static String SuperCateName;
    public static String eventID;
    static EventDetail eventDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_chat_fragment);
        connectDetector = new ConnectDetector(this);
        eventDetail = (EventDetail) getIntent().getSerializableExtra("EventDetail");
        SuperCateName = eventDetail.getSuper_category_name();
        CateName = eventDetail.getEvent_title();
        eventID = eventDetail.getEvent_id();

        if(MyApp.firebaseFlag && connectDetector.getConnection()) {
            init();
        }else{
            Toast.makeText(this, "Please check internet connection. Server Error.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void init(){
     /*   toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(CateName);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.menu_signup){
            Intent ii = new Intent(this, SignUpActivity.class);
            startActivity(ii);
        }else if(id == R.id.menu_info){
            Intent ii = new Intent(this, InfoActivity.class);
            startActivity(ii);
        }else if(id == R.id.menu_more){
            Toast.makeText(this,"More is coming soon", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.menu_noti){
            Toast.makeText(this,"Notification is coming soon", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StadiumFragment(), "Stadium");
        adapter.addFragment(new HousePartyFragment(), "House Party");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        try{
            if(StadiumFragment.linearCanMsg.getVisibility() == View.VISIBLE){
                View view = this.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                StadiumFragment.linearCanMsg.setVisibility(View.GONE);
            }else {
                super.onBackPressed();
            }
        }catch (Exception ex){}
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
