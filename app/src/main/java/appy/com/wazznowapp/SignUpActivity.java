package appy.com.wazznowapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.model.UserLoginSignupAction;
import com.app.model.UserProfile;
import com.firebase.client.Firebase;

/**
 * Created by admin on 8/2/2016.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    ActionBar actionBar;
    Button btnSign;
    UserProfile userProfile;
    EditText etName, etLastName, etPhone, etEmail, etPassword;
    TextView tvNahGuestUser;
    SharedPreferences.Editor editor;
    public static String USER_NAME = "UserName";
    public static String USER_EMAIL = "UserEmail";
    public static String USER_PASSWORD = "UserPassword";
    UserLoginSignupAction userSignup;

    Firebase myFirebaseSignup;
    final static String firebaseURL = "https://wazznow-cd155.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        init();
        myFirebaseSignup = new Firebase(firebaseURL);
       // myFirebaseSignup.child("UserList");
        userProfile = new UserProfile();
    }
    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign Up");
        etName = (EditText) findViewById(R.id.input_name);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPassword = (EditText) findViewById(R.id.input_password);
        tvNahGuestUser = (TextView) findViewById(R.id.tvNahGuestUser);
        btnSign = (Button) findViewById(R.id.btnSignup);

        btnSign.setOnClickListener(this);
        tvNahGuestUser.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btnSignup){
            String uName = etName.getText().toString();
            String uEmail = etEmail.getText().toString();
            String uPass = etPassword.getText().toString();
            if(!TextUtils.isEmpty(uEmail)) {
                userSignup = new UserLoginSignupAction();
                userSignup.userSignup(SignUpActivity.this, uName, uEmail, uPass);

                editor = MyApp.preferences.edit();
                editor.putString(USER_NAME, uName);
                editor.commit();
               // finish();
            }else{
                Toast.makeText(this, "Please Fill Name", Toast.LENGTH_LONG).show();
            }
        }
        else if(id == R.id.tvNahGuestUser){
            MyApp.USER_LOGIN = true;
            editor = MyApp.preferences.edit();
            editor.putString(USER_NAME, "Guest User");
            editor.commit();
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
