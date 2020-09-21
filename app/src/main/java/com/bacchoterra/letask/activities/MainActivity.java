package com.bacchoterra.letask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt = findViewById(R.id.rola);


        final FirebaseUser user = UsuarioFirebase.getCurrentUser();


        Toast.makeText(getApplicationContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, SharedPrefsUtil.getUserCountry(this), Toast.LENGTH_SHORT).show();


        txt.setOnClickListener(new View.OnClickListener() {
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

    }
}