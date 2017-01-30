package appy.com.wazznowapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
public class ShareEventActivity extends Activity implements View.OnTouchListener{
    private GridView gridView;
    //private Button btnShare;
    private java.util.List<ResolveInfo> listApp;
    private final int WHAT_SHOW_SHARE_APP = 101;
    LinearLayout baseLayout;
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;
    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_SHOW_SHARE_APP:
                gridView.setAdapter(new MyAdapter());
                break;
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_event);
        gridView = (GridView) findViewById(R.id.gridView);
        //btnShare = (Button) findViewById(R.id.btn_share);

        listApp = showAllShareApp();
        if (listApp != null) {
            gridView.setAdapter(new MyAdapter());
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //btnShare.setEnabled(true);
                    share(listApp.get(position),""+getIntent().getStringExtra("share"));
                }
            });
        }

        /*btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            listApp = showAllShareApp();
            if (listApp != null) {
                gridView.setAdapter(new MyAdapter());
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        share(listApp.get(position));
                    }
                });
            }
            }
        });*/


        baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
        //gridView.setOnTouchListener(this);
    }

    private void share(ResolveInfo appInfo,String share) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, share);
        if (appInfo != null) {
            sendIntent.setComponent(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name));
        }
        sendIntent.setType("text/plain");
        // startActivity(Intent.createChooser(sendIntent, "Share"));
        startActivity(sendIntent);
        finish();
    }


    private java.util.List<ResolveInfo> showAllShareApp() {
        java.util.List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        intent.setType("text/plain");
        PackageManager pManager = getPackageManager();
        mApps = pManager.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    public boolean onTouch(View view, MotionEvent event) {
        // Get finger position on screen
        final int Y = (int) event.getRawY();
        // Switch on motion event type
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // save default base layout height
                defaultViewHeight = baseLayout.getHeight();
                // Init finger and view position
                previousFingerPosition = Y;
                baseLayoutPosition = (int) baseLayout.getY();
                break;

            case MotionEvent.ACTION_UP:
                // If user was doing a scroll up
                if(isScrollingUp){
                    // Reset baselayout position
                    baseLayout.setY(0);
                    // We are not in scrolling up mode anymore
                    isScrollingUp = false;
                }

                // If user was doing a scroll down
                if(isScrollingDown){
                    // Reset baselayout position
                    baseLayout.setY(0);
                    // Reset base layout size
                    baseLayout.getLayoutParams().height = defaultViewHeight;
                    baseLayout.requestLayout();
                    // We are not in scrolling down mode anymore
                    isScrollingDown = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isClosing){
                    int currentYPosition = (int) baseLayout.getY();
                    // If we scroll up
                    if(previousFingerPosition >Y){
                        // First time android rise an event for "up" move
                        if(!isScrollingUp){
                            isScrollingUp = true;
                        }
                        // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
                        if(baseLayout.getHeight()<defaultViewHeight){
                            baseLayout.getLayoutParams().height = baseLayout.getHeight() - (Y - previousFingerPosition);
                            baseLayout.requestLayout();
                        }
                        else {
                            // Has user scroll enough to "auto close" popup ?
                            if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                closeUpAndDismissDialog(currentYPosition);
                                return true;
                            }
                        }
                        baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));
                    }
                    // If we scroll down
                    else{
                        // First time android rise an event for "down" move
                        if(!isScrollingDown){
                            isScrollingDown = true;
                        }
                        // Has user scroll enough to "auto close" popup ?
                        if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2)
                        {
                            closeDownAndDismissDialog(currentYPosition);
                            return true;
                        }
                        // Change base layout size and position (must change position because view anchor is top left corner)
                        baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));
                        baseLayout.getLayoutParams().height = baseLayout.getHeight() - (Y - previousFingerPosition);
                        baseLayout.requestLayout();
                    }
                    // Update position
                    previousFingerPosition = Y;
                }
                break;
        }
        return true;
    }


    public void closeUpAndDismissDialog(int currentPosition){
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, -baseLayout.getHeight());
        positionAnimator.setDuration(500);
        positionAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition){
        isClosing = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, screenHeight+baseLayout.getHeight());
        positionAnimator.setDuration(500);
        positionAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        positionAnimator.start();
    }


    class MyAdapter extends BaseAdapter {
        PackageManager pm;
        public MyAdapter(){
            pm=getPackageManager();
        }

        @Override
        public int getCount() {
            return listApp.size();
        }

        @Override
        public Object getItem(int position) {
            return listApp.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ShareEventActivity.this).inflate(R.layout.layout_share_app, parent, false);
                holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
                holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                //holder.tvPackageName = (TextView) convertView.findViewById(R.id.tv_app_package_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ResolveInfo appInfo = listApp.get(position);
            holder.ivLogo.setImageDrawable(appInfo.loadIcon(pm));
            holder.tvAppName.setText(appInfo.loadLabel(pm));
            //holder.tvPackageName.setText(/*appInfo.activityInfo.packageName*/"");

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivLogo;
        TextView tvAppName;
        //TextView tvPackageName;
    }
}
