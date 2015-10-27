package com.sayor.org.cutmypie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sayor.org.cutmypie.R;


public class LoginActivity extends AppCompatActivity {

    Button btLogin;
    EditText etUsername, etPassword, etEmail;
    ParseUser user;
    ProgressBar progressBar;
    ToggleButton btToggle;
    String sUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // creating parseuser for signup
        user = new ParseUser();

        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etEmail = (EditText)findViewById(R.id.etEmail);
        btLogin = (Button)findViewById(R.id.btLogin);;
        btToggle = (ToggleButton) findViewById(R.id.btToggle);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);

        onBackPressed();
    }

    public void onToggle(View v){
        if(btToggle.isChecked()){
            etEmail.setVisibility(View.INVISIBLE);
            btLogin.setText("Login");
        }else{
            etEmail.setVisibility(View.VISIBLE);
            btLogin.setText("Signup");
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    public void onValidate(View v){
        progressBar.setVisibility(ProgressBar.VISIBLE);
        String login = btLogin.getText().toString();
        // if (true) login, else signup
        if(login.equals("Login")) {
            user.logInInBackground(etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        sUserId = ParseUser.getCurrentUser().getObjectId();
                        Intent i =new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "error in login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            user.setUsername(etUsername.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.setEmail(etEmail.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        sUserId = ParseUser.getCurrentUser().getObjectId();
                        Intent i =new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(LoginActivity.this, "error in signup", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
