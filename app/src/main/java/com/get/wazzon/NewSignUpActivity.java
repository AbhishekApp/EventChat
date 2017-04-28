package com.get.wazzon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.InvalidNameException;
import com.app.model.UserProfile;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Auth;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.SocialProfile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.MyApp.FIREBASE_BASE_URL;
import static com.get.wazzon.SignUpActivity.btnSign;

/**
 * Created by admin on 8/2/2016.
 */
public class NewSignUpActivity extends AppCompatActivity implements View.OnClickListener,Auth.OnAuthListener {
    ActionBar actionBar;
    //Button btnSignup;
    public ProgressBar progressBar;
    UserProfile userProfile;
    SharedPreferences.Editor editor;
    Firebase myFirebaseSignup;
    //final static String firebaseURL = "https://wazznow-cd155.firebaseio.com";
    LinearLayout linearNahGuestUser;
    private List<String> permissionNeeds;
    private String userId;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    CallbackManager callbackManager;
    TextView textView;
    LoginButton btnFb;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]
    private static final String TAG = "FacebookLogin";
    UserLoginSignupAction userSignup;
    public static boolean closeSignup=false;



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
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        getIntent().getBooleanExtra("WonSignup", false);


        mAuthListener = new FirebaseAuth.AuthStateListener()  {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                updateUI(user);
                userSignup = new UserLoginSignupAction();
                try {
                    //String[] nameSplit = "Manish".split(" ");
                    String[] nameSplit = user.getDisplayName().split(" ");
                    if (nameSplit.length > 0) {
                        String first = nameSplit[0];
                        String last = nameSplit[1];
                        if (!TextUtils.isEmpty(user.getEmail()))
                            userSignup.userSignup(NewSignUpActivity.this, first/*username*/, last/*last name*/, ""/*phone number*/, user.getEmail()/*email id*/, MyApp.getDeviveID(NewSignUpActivity.this)/*password*/);
                        else
                            userSignup.userSignup(NewSignUpActivity.this, first/*username*/, last/*last name*/, ""/*phone number*/, "wazzon.app@gmail.com"/*email id*/, MyApp.getDeviveID(NewSignUpActivity.this)/*password*/);
                    }else{
                        if (!TextUtils.isEmpty(user.getEmail()))
                            userSignup.userSignup(NewSignUpActivity.this, user.getDisplayName()/*username*/, ""/*last name*/, ""/*phone number*/, user.getEmail()/*email id*/, MyApp.getDeviveID(NewSignUpActivity.this)/*password*/);
                        else
                            userSignup.userSignup(NewSignUpActivity.this, user.getDisplayName()/*username*/, ""/*last name*/, ""/*phone number*/, "wazzon.app@gmail.com"/*email id*/, MyApp.getDeviveID(NewSignUpActivity.this)/*password*/);
                    }
                }
                catch (ArrayIndexOutOfBoundsException  e) {
                    // no last name case
                    new InvalidNameException("Missing space in: " + user);
                    userSignup.userSignup(NewSignUpActivity.this, user.getDisplayName()/*username*/, ""/*last name*/, ""/*phone number*/, user.getEmail()/*email id*/, MyApp.getDeviveID(NewSignUpActivity.this)/*password*/);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // [START_EXCLUDE]
            // [END_EXCLUDE]
            }
        };
        init();
        myFirebaseSignup = new Firebase(FIREBASE_BASE_URL);
        myFirebaseSignup.child("UserDetail");
        userProfile = new UserProfile();
    }



    class UserLoginSignupAction {
        SharedPreferences.Editor editor;
        String firebaseUserURL = FIREBASE_BASE_URL;

        public void userSignup(final Activity con, final String uName, final String uLastName, final String uPhone, final String email, final String password){
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.addAuthStateListener(mAuthListener);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(con, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                    Log.d("Signup", "createUserWithEmail:onComplete:" + task.isSuccessful());
                    progressBar.setVisibility(View.GONE);

                    if (!task.isSuccessful() && task.getException().toString().contains("The email address is already in use by another account")) {
                        //isSignupSuccessful = true;

                        editor = MyApp.preferences.edit();
                        editor.putString("isSignupSuccessful", "true");
                        editor.commit();

                        //Toast.makeText(con, "account overwitten", Toast.LENGTH_SHORT).show();
                        userUpdateOnServer(uName, uLastName, uPhone, email, password);
                        SignUpActivity.makeClickable();
                        setResult(102);
                        finish();


                    }else if(task.isSuccessful()){
                        //isSignupSuccessful = true;

                        editor = MyApp.preferences.edit();
                        editor.putString("isSignupSuccessful", "true");
                        editor.commit();
                        try {
                            MyApp.PreDefinedEventAnalytics("sign_up", eventDetail.getEvent_title(), eventID);
                        }catch (Exception ex){ex.printStackTrace();}
                        //Toast.makeText(con, "new signup", Toast.LENGTH_SHORT).show();
                        userUpdateOnServer(uName, uLastName, uPhone, email, password);
                        setResult(102);
                        finish();
                    }
                    }
                });
            }else{
                if(TextUtils.isEmpty(email)&& TextUtils.isEmpty(uName)){
                    Toast.makeText(con, "Please fill all the required field", Toast.LENGTH_SHORT).show();
                }else{
                    //if(TextUtils.isEmpty(email)){
                        Toast.makeText(con, "We need your Email to sign you in. Please try again!", Toast.LENGTH_SHORT).show();
                    //}
                }
            }
        }
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d("Signup", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d("Signup", "onAuthStateChanged:signed_out");
            }
            }
        };


        public void userUpdateOnServer(String uName, String uLastName, String uPhone, String email, String password){
            editor = MyApp.preferences.edit();
            editor.putString(MyApp.USER_NAME, uName+" "+uLastName);
            editor.putString(MyApp.USER_LAST_NAME, uLastName);
            editor.putString(MyApp.USER_PHONE, uPhone);
            editor.putString(MyApp.USER_EMAIL, email);
            editor.putString(MyApp.USER_TYPE, "user");
            editor.putString(MyApp.USER_PASSWORD, password);
            editor.commit();

            Firebase firebase = new Firebase(firebaseUserURL);
            Map<String, String> alanisawesomeMap = new HashMap<String, String>();
            String userGroup = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, "");
            String commentator_privilege = MyApp.preferences.getString("commentator_privilege","");
            String UserJoinedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP,"");
            alanisawesomeMap.put("name", uName);
            alanisawesomeMap.put("lastName", uLastName);
            alanisawesomeMap.put("passKey", password);
            alanisawesomeMap.put("authorType", "user");
            alanisawesomeMap.put("phone", uPhone);
            alanisawesomeMap.put("email", email);
            alanisawesomeMap.put("userType", "user");
            alanisawesomeMap.put("house_party_invitations",""+userGroup);
            alanisawesomeMap.put("commentator_privilege",""+commentator_privilege);
            alanisawesomeMap.put("user_enabled","true");
            alanisawesomeMap.put("joined_group",UserJoinedGroup);
            //by default this flag will be false when admin approve then that user will be treated as commentator and can post commnets in the specific group's event
            final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
            //System.out.println("USER List new length : " );
            //System.out.println("USER List new deviceID : " );
            users.put("0", alanisawesomeMap);
            firebase.child("users/" + password).setValue(users);
            MyApp.PreDefinedEventAnalytics("sign_up",email,eventID);
        }
    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign Up");

        linearNahGuestUser = (LinearLayout) findViewById(R.id.linearNahGuestUser);
        btnFb = (LoginButton) findViewById(R.id.btnFb);

        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(NewSignUpActivity.this, "hi", Toast.LENGTH_SHORT).show();

                MyApp.PreDefinedEventAnalytics("signUp_fb", "" , "");

            }
        });

        btnFb.setReadPermissions("email", "public_profile");
        btnFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                progressBar.setVisibility(View.VISIBLE);

                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });

        btnSign = (Button) findViewById(R.id.btnmySignup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        textView = (TextView) findViewById(R.id.textView);
        btnSign.setOnClickListener(this);
        //btnFb.setOnClickListener(this);
        linearNahGuestUser.setOnClickListener(this);

        try {
            MyApp.CustomEventAnalytics("signup_activity_loaded", eventDetail.getEvent_id());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }



    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnmySignup) {
            MyApp.PreDefinedEventAnalytics("signUp_email", "" , "");
            startActivity(new Intent(NewSignUpActivity.this,SignUpActivity.class));
        } /*else if (id == R.id.btnFb) {
            //progressBar.setVisibility(View.VISIBLE);
           // fbConnect();
        }*/
        else if (id == R.id.linearNahGuestUser) {
            ChatStadiumFragment.nahClicked =true;
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


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //textView.setText("\n"+getString(R.string.facebook_status_fmt, user.getDisplayName())+"\n\n"+getString(R.string.firebase_status_fmt, user.getEmail()));

        } else {
            textView.setText("");
            //mDetailTextView.setText(null);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        MenuItem signup = menu.findItem(R.id.menu_signup);


        if (MyApp.preferences.getString("isSignupSuccessful", "false").equals("true")) {
            signup.setVisible(false);
        }else{
            signup.setVisible(true);
        }
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
        }/*else if(id == R.id.menu_more){
            Toast.makeText(this,"More is coming soon", Toast.LENGTH_SHORT).show();
        }*/
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


    @Override
    protected void onResume() {
        super.onResume();
        if(closeSignup){
            finish();
        }
    }



    private void displayMessage(Profile profile) {
        if (profile != null) {
            Uri profile_url = profile.getProfilePictureUri(200, 200);
            //textView.setText(profile.getId()+"\n"+profile.getName()+"\n"+profile.getProfilePictureUri(200, 200)+"\n"+profile.getLinkUri());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(NewSignUpActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]
}


