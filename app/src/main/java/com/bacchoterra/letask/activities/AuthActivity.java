package com.bacchoterra.letask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    //Layout components
    private SignInButton btnGoogleSignIn;
    private Button btnEmailSignIn;
    private TextView txtVisitorSignIn;

    //Firebase components
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();
    }

    private void init(){

        mAuth = FirebaseConfig.getFBAuth();
        initViews();

    }

    private void initViews() {
        btnGoogleSignIn = findViewById(R.id.auth_activity_btnGoogleSignIn);
        btnEmailSignIn = findViewById(R.id.auth_activity_btnEmailSignIn);
        txtVisitorSignIn = findViewById(R.id.auth_activity_txtVisitorSignIn);

    }
}