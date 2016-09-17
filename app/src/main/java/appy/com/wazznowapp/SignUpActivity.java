package appy.com.wazznowapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
    EditText etName, etLastName, etPhone, etEmail;
    TextView tvNahGuestUser;
    SharedPreferences.Editor editor;
    public static String USER_NAME = "UserName";
    public static String USER_LAST_NAME = "UserLastName";
    public static String USER_PHONE = "UserPhone";
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
        myFirebaseSignup.child("UserDetail");
       // myFirebaseSignup.child("UserList");
        userProfile = new UserProfile();
    }
    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign Up");
        etName = (EditText) findViewById(R.id.input_name);
        etLastName = (EditText) findViewById(R.id.input_lastname);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPhone = (EditText) findViewById(R.id.input_phone);

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
            String uLastName = etLastName.getText().toString();
            String uEmail = etEmail.getText().toString();
            String uPass = MyApp.getDeviveID(this);
            String uPhone = etPhone.getText().toString();
            boolean flag = validate(uName, uEmail, uPass, uPhone);
            if(flag) {
                userSignup = new UserLoginSignupAction();
                userSignup.userSignup(SignUpActivity.this, uName, uLastName, uPhone, uEmail, uPass);
               // finish();
            }
        }
        else if(id == R.id.tvNahGuestUser){
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

    private boolean validate(String name, String email, String pass, String phone){
        boolean valid = false;
        try{
            if(TextUtils.isEmpty(name)){
                Toast.makeText(this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if(name.length() < 4){
                Toast.makeText(this, "Name field should not less than 4 character", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this, "Phone cannot be blank", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if(phone.length() == 9){
                Toast.makeText(this, "Phone number should be of 10 numbers", Toast.LENGTH_SHORT).show();
                return valid;
            }
             if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
                return valid;
            }
            if(!email.contains("@") || !email.contains(".")){
                Toast.makeText(this, "Please enter valid Email id", Toast.LENGTH_SHORT).show();
                return valid;
            }
             if(TextUtils.isEmpty(pass)){
                Toast.makeText(this, "Service not connecting", Toast.LENGTH_SHORT).show();
                return valid;
            }

        }catch (Exception ex){
            Log.e("SignUpActivity","Signup Activity ERROR : "+ex.toString());
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return valid;
        }
        return true;
    }

}
