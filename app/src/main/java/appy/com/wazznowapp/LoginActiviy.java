package appy.com.wazznowapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.model.UserLoginSignupAction;
import com.firebase.client.Firebase;

/**
 * Created by admin on 9/8/2016.
 */
public class LoginActiviy extends AppCompatActivity implements View.OnClickListener{

    ActionBar actionBar;
    TextView tvSignup, tvGuest;
    EditText etEmail, etPass;
    Button btnLogin;
    UserLoginSignupAction login;
    Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        firebase = new Firebase(MyApp.FIREBASE_BASE_URL);

        init();
    }

    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Login");
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        tvGuest = (TextView) findViewById(R.id.tvNahGuestUser);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPass = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
        tvGuest.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnLogin){
            String email = etEmail.getText().toString();
            String pass = etPass.getText().toString();
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                login = new UserLoginSignupAction();
                login.userLogin(firebase, LoginActiviy.this, email, pass);
            }
        }
        else if(id == R.id.tvSignup){
            Intent ii = new Intent(this, SignUpActivity.class);
            startActivity(ii);
        }
        else if(id == R.id.tvNahGuestUser){

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
