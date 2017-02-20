package appy.com.wazznowapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.UserProfile;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Auth;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.SocialProfile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static appy.com.wazznowapp.EventChatActivity.eventDetail;
import static appy.com.wazznowapp.SignUpActivity.btnSign;


/**
 * Created by admin on 8/2/2016.
 */
public class NewSignUpActivity extends AppCompatActivity implements View.OnClickListener,Auth.OnAuthListener {
    ActionBar actionBar;
    Button btnFb,btnSignup;
    public ProgressBar progressBar;
    UserProfile userProfile;
    SharedPreferences.Editor editor;
    Firebase myFirebaseSignup;
    final static String firebaseURL = "https://wazznow-cd155.firebaseio.com";
    TextView tvNahGuestUser;
    private List<String> permissionNeeds;
    private String userId;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    CallbackManager callbackManager;
    TextView textView;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);
        }

        @Override
        public void onCancel() {
            Log.e("error","error");
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        setContentView(R.layout.new_signup);
        callbackManager = CallbackManager.Factory.create();

        init();
        myFirebaseSignup = new Firebase(firebaseURL);
        myFirebaseSignup.child("UserDetail");
        userProfile = new UserProfile();
    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign Up");

        tvNahGuestUser = (TextView) findViewById(R.id.tvNahGuestUser);
        btnFb = (Button) findViewById(R.id.btnFb);
        btnSign = (Button) findViewById(R.id.btnmySignup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        textView = (TextView) findViewById(R.id.textView);
        btnSign.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        tvNahGuestUser.setOnClickListener(this);

        try {
            MyApp.CustomEventAnalytics("signup_activity_loaded", EventChatActivity.SuperCateName, eventDetail.getCategory_name());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnmySignup) {
            startActivity(new Intent(NewSignUpActivity.this,SignUpActivity.class));

        } else if (id == R.id.btnFb) {
            progressBar.setVisibility(View.VISIBLE);
            fbConnect();
        }
        else if (id == R.id.tvNahGuestUser) {
            editor = MyApp.preferences.edit();
            editor.putString(MyApp.USER_NAME, "Guest User");
            editor.commit();
            setResult(101);
            finish();
        }
    }

    public static void makeClickable() {
        btnSign.setClickable(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_signup){
            //Intent ii = new Intent(this, NewSignUpActivity.class);
           // startActivity(ii);
        }else if(id == R.id.menu_info){
            Intent ii = new Intent(this, InfoActivity.class);
            startActivity(ii);
        }else if(id == R.id.menu_more){
            Toast.makeText(this,"More is coming soon", Toast.LENGTH_SHORT).show();
        }
        else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLoginSuccess(SocialProfile profile) {
        saveAuthenticatedUser(profile);
    }

    @Override
    public void onLoginError(String message) {
        Log.e("error",message);
    }

    @Override
    public void onLoginCancel() {
        Log.i("fbLoginStatus", "Cancelled");
    }

    @Override
    public void onRevoke() {
        Log.i("fbLoginStatus", "Cancelled");
    }


    private void saveAuthenticatedUser(SocialProfile profile) {
        String image_url = profile.getImage();
        String SocialName = profile.getName();
        String SocialId = profile.getUid();
        String imageurl = profile.getImage();
        Toast.makeText(NewSignUpActivity.this, "saveAuthenticatedUser Method", Toast.LENGTH_SHORT).show();
    }



    public void fbConnect() {
        permissionNeeds = Arrays.asList("email","public_profile");
        LoginManager.getInstance().logInWithReadPermissions(this,permissionNeeds);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("fbLoginStatus", "" + loginResult);
                        userId = loginResult.getAccessToken().getUserId();
                        // sending a Graph request
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.e("GraphResponse", "-------------" + response.toString());
                                        textView.setText(response.toString());
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,gender,birthday,email,location");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.i("fbLoginStatus", "Cancelled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.i("fbLoginStatus", "Error "+error);
                    }
                });
    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            Uri profile_url = profile.getProfilePictureUri(200, 200);
            //textView.setText(profile.getId()+"\n"+profile.getName()+"\n"+profile.getProfilePictureUri(200, 200)+"\n"+profile.getLinkUri());
        }
    }
}


