package com.bacchoterra.letask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.SharedPrefsUtil;
import com.bacchoterra.letask.helper.UsuarioFirebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    //Layout components
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private BottomNavigationViewEx bottomNavigationViewEx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        /*txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

                mSignInClient.signOut();
                FirebaseAuth d = FirebaseConfig.getFBAuth();

                d.signOut();
                finish();


            }
        });


         */

    }

    private void init() {
        initViews();
        initToolbarAndDrawer();
        initBottomNavigation();

    }

    private void initViews() {

        toolbar = findViewById(R.id.activity_main_toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawerLayout);
        navView = findViewById(R.id.activity_main_navView);
        bottomNavigationViewEx = findViewById(R.id.activity_main_bottomNavView);

    }

    private void initToolbarAndDrawer() {


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();



    }

    private void initBottomNavigation(){

        bottomNavigationViewEx.setIconSize(28);


    }
}