package com.bacchoterra.letask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.helper.MyHelper;
import com.bacchoterra.letask.helper.SharedPrefsUtil;
import com.bacchoterra.letask.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    //LayoutComponents
    private ViewGroup rootLayout;
    private Button btnTryAgain;

    //Extras
    private static final int TIME = 2300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                handleUserAuthentication();

            }
        }, TIME);


    }

    private void initViews() {
        rootLayout = findViewById(R.id.activity_splash_rootLayout);
        btnTryAgain = findViewById(R.id.activity_splash_btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserAuthentication();
            }
        });
    }

    private void handleUserAuthentication() {

        if (MyHelper.netConn(this)) {


            FirebaseUser user = UsuarioFirebase.getCurrentUser();

            if (user != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }

            finish();

        } else {
            MyHelper.showSnackbarLong(R.string.no_internet_connection, rootLayout);
            btnTryAgain.setVisibility(View.VISIBLE);
        }

    }
}