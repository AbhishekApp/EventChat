package com.get.wazzon;

import android.app.Activity;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.UserProfile;
import com.firebase.client.Firebase;
import com.firebase.simplelogin.FirebaseSimpleLoginError;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginCompletionHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.MyApp.FIREBASE_BASE_URL;
import static com.get.wazzon.NewSignUpActivity.closeSignup;
import static com.get.wazzon.R.id.progressBar2;
import static com.get.wazzon.R.id.up;

/**
 * Created by manish on 8/2/2016.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    static Button btnSign;
    public ProgressBar progressBar;
    UserProfile userProfile;
    EditText etName, etLastName, etPhone, etEmail;
    LinearLayout linearNahGuestUser;
    SharedPreferences.Editor editor;
    UserLoginSignupAction userSignup;
    Firebase myFirebaseSignup;
    boolean wonFlag = false;
    TextView tvHead;
    String emailBody;
    //final static String firebaseURL = "https://wazznow-cd155.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        try{
            wonFlag = getIntent().getBooleanExtra("WonSignup", false);
        }catch (Exception ex){}
        init();
        myFirebaseSignup = new Firebase(FIREBASE_BASE_URL);
        myFirebaseSignup.child("UserDetail");
        userProfile = new UserProfile();
    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign Up");
        linearNahGuestUser = (LinearLayout) findViewById(R.id.linearNahGuestUser);
        tvHead = (TextView) findViewById(R.id.tvHeadSign);
        if(wonFlag){
            tvHead.setText("Please Provide the Below Info for Redemption");
            linearNahGuestUser.setVisibility(View.GONE);
        }
        else
        {
            tvHead.setText(getResources().getString(R.string.signupheading));
        }
        etName = (EditText) findViewById(R.id.input_name);
        etLastName = (EditText) findViewById(R.id.input_lastname);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPhone = (EditText) findViewById(R.id.input_phone);


        btnSign = (Button) findViewById(R.id.btnSignup);
        progressBar = (ProgressBar) findViewById(progressBar2);
        btnSign.setOnClickListener(this);
        linearNahGuestUser.setOnClickListener(this);
        try {
            MyApp.CustomEventAnalytics("signup_activity_loaded", eventDetail.getEvent_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSignup) {
            progressBar.setVisibility(View.VISIBLE);
            btnSign.setClickable(false);
            String uName = etName.getText().toString();
            String uLastName = etLastName.getText().toString();
            String uEmail = etEmail.getText().toString();
            String uPass = MyApp.getDeviveID(this);
            String uPhone = etPhone.getText().toString();
            boolean flag = validate(uName, uEmail, uPass, uPhone);
            if (flag) {
                if(wonFlag){
                    emailBody = "Name : "+uName+" \n Last Name : "+uLastName+" \n Email : "+uEmail+" \n Phone no. : "+uPhone+" \n Total WON : "+MyApp.preferences.getInt("EarnedWon", 0);
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.setType("message/rfc822");
//                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"wazzon.app@gmail.com"});
                    i.setData(Uri.parse("mailto:wazzon.app@gmail.com"));
                    i.putExtra(Intent.EXTRA_SUBJECT, "For Redemption WazzOn "+MyApp.preferences.getInt("EarnedWon", 0)+" WON");
                    i.putExtra(Intent.EXTRA_TEXT   , emailBody);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(SignUpActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }else {
                    userSignup = new UserLoginSignupAction();
                    userSignup.userSignup(SignUpActivity.this, uName, uLastName, uPhone, uEmail, uPass);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Credentials can't be validated", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.linearNahGuestUser) {
            ChatStadiumFragment.nahClicked = true;
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (closeSignup) {
            finish();
        }
    }

    private boolean validate(String name, String email, String pass, String phone) {
        boolean valid = false;
        try {
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if (name.length() < 4) {
                Toast.makeText(this, "Name field should not less than 4 character", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Phone cannot be blank", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if (phone.length() <= 9) {
                Toast.makeText(this, "Phone number should be of 10 numbers", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length() < 6) {
                Toast.makeText(this, "Please enter valid Email id", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if (TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Service not connecting", Toast.LENGTH_SHORT).show();
                return valid;
            }

        } catch (Exception ex) {
            Log.e("SignUpActivity", "Signup Activity ERROR : " + ex.toString());
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return valid;
        }
        return true;
    }

    class UserLoginSignupAction {
        SharedPreferences.Editor editor;
        String firebaseUserURL = FIREBASE_BASE_URL;

        public void userSignup(final Activity con, final String uName, final String uLastName, final String uPhone, final String email, final String password) {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
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

                            MyApp.PreDefinedEventAnalytics("sign_up", "signup", email);
                            //Toast.makeText(con, "Success", Toast.LENGTH_SHORT).show();
                            userUpdateOnServer(uName, uLastName, uPhone, email, password);
                            SignUpActivity.makeClickable();
                            setResult(102);
                            closeSignup = true;
                            finish();

                        } else if (task.isSuccessful()) {
                            //isSignupSuccessful = true;

                            editor = MyApp.preferences.edit();
                            editor.putString("isSignupSuccessful", "true");
                            editor.commit();

                            MyApp.PreDefinedEventAnalytics("sign_up", "signup", email);
                            // Toast.makeText(con, "Success", Toast.LENGTH_SHORT).show();
                            userUpdateOnServer(uName, uLastName, uPhone, email, password);
                            setResult(102);
                            closeSignup = true;
                            finish();

                        }
                    }


                });
            } else {
                Toast.makeText(con, "Please fill all the required field", Toast.LENGTH_SHORT).show();
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

        public void userLogin(Firebase myRef, final Activity con, String email, String password) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(mAuthListener);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(con, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            Log.d("Login", "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("Login", "signInWithEmail:failed", task.getException());
                                Toast.makeText(con, "Login Fail", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(con, "User logged in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        public void userChangePassword(Firebase myRef, final Context con, String email, String password, String newPassword) {
            SimpleLogin authClient = new SimpleLogin(myRef, con);
            authClient.changePassword(email, password, newPassword, new SimpleLoginCompletionHandler() {
                public void completed(FirebaseSimpleLoginError error, boolean success) {
                    if (error != null) {
                        // There was an error processing this request
                        Toast.makeText(con, "Password not changed", Toast.LENGTH_SHORT).show();
                    } else if (success) {
                        // Password changed successfully
                        Toast.makeText(con, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void userLogout() {
            FirebaseAuth.getInstance().signOut();
        }

        public void userUpdateOnServer(String uName, String uLastName, String uPhone, String email, String password) {
            editor = MyApp.preferences.edit();
            editor.putString(MyApp.USER_NAME, uName + " " + uLastName);
            editor.putString(MyApp.USER_LAST_NAME, uLastName);
            editor.putString(MyApp.USER_PHONE, uPhone);
            editor.putString(MyApp.USER_EMAIL, email);
            editor.putString(MyApp.USER_TYPE, "user");
            editor.putString(MyApp.USER_PASSWORD, password);
            editor.commit();

            Firebase firebase = new Firebase(firebaseUserURL);
            Map<String, String> alanisawesomeMap = new HashMap<String, String>();
            String userGroup = MyApp.preferences.getString(MyApp.HOUSE_PARTY_INVITATIONS, "");
            String UserJoinedGroup = MyApp.preferences.getString(MyApp.USER_JOINED_GROUP, "");
            String commentator_privilege = MyApp.preferences.getString("commentator_privilege", "");
            alanisawesomeMap.put("name", uName);
            alanisawesomeMap.put("lastName", uLastName);
            alanisawesomeMap.put("passKey", password);
            alanisawesomeMap.put("authorType", "user");
            alanisawesomeMap.put("phone", uPhone);
            alanisawesomeMap.put("email", email);
            alanisawesomeMap.put("userType", "user");
            alanisawesomeMap.put("house_party_invitations", "" + userGroup);
            alanisawesomeMap.put("commentator_privilege", "" + commentator_privilege);
            alanisawesomeMap.put("user_enabled", "true");
            alanisawesomeMap.put("joined_group", UserJoinedGroup);

            //by default this flag will be false when admin approve then that user will be treated as commentator and can post commnets in the specific group's event
            final Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
            //System.out.println("USER List new length : " );
            //System.out.println("USER List new deviceID : " );
            users.put("0", alanisawesomeMap);
            firebase.child("users/" + password).setValue(users);
            MyApp.PreDefinedEventAnalytics("sign_up", email, eventID);
        }
    }
}


