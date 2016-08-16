package appy.com.wazznowapp;

import android.app.Activity;
import android.content.Intent;
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

import com.app.model.UserProfile;

/**
 * Created by admin on 8/2/2016.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    ActionBar actionBar;
    Button btnSign;
    UserProfile userProfile;
    EditText etName, etLastName, etPhone, etEmail, etPassword;
    TextView tvNahGuestUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        init();
        userProfile = new UserProfile();
    }
    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        etName = (EditText) findViewById(R.id.input_name);
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
            if(!TextUtils.isEmpty(uName)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Login");
                returnIntent.putExtra("user", uName);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }else{
                Toast.makeText(this, "Please Fill Name", Toast.LENGTH_LONG).show();
            }
        }
        else if(id == R.id.tvNahGuestUser){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","Guest User");
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

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
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
