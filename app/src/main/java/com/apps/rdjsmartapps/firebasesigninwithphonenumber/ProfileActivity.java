package com.apps.rdjsmartapps.firebasesigninwithphonenumber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth firebase;
    TextView welcomeText;
    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebase = FirebaseAuth.getInstance();
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        logOut = (Button) findViewById(R.id.logOut);

        if(firebase.getCurrentUser() != null){
            FirebaseUser user = firebase.getCurrentUser();
            welcomeText.setText("Welcome "+user.getPhoneNumber().toString());
        }

        logOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        if(view == logOut){
            Toast.makeText(getApplicationContext(), "Signing out!", Toast.LENGTH_SHORT).show();
            firebase.signOut();
            finish();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        }
    }
}
