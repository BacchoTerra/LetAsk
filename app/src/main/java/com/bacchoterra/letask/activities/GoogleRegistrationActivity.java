package com.bacchoterra.letask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.authfragments.CreateAccountFragment;
import com.bacchoterra.letask.model.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthCredential;

public class GoogleRegistrationActivity extends AppCompatActivity {

    //Layout components
    private Toolbar toolbar;
    private FrameLayout container;

    //Model
    public Usuario usuario;

    //Bundle from AuthActivity
    private Bundle bundle;

    //Fragment arguments;
    public static final String BUNDLE_KEY = "bundle_google_key";
    public static final String GOOGLE_FRAG_TAG = "google_registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_registration);
        init();

    }

    private void init() {
        initViews();
        initToolbar();
        getBundleFromActivity();
        initFragment();

    }

    private void initViews() {
        toolbar = findViewById(R.id.google_reg_activity_toolbar);
        container = findViewById(R.id.google_reg_Activity_fragContainer);

    }

    private void initToolbar() {


        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

    }

    private void getBundleFromActivity() {

        bundle = getIntent().getExtras();

        if (bundle != null) {

            usuario = (Usuario) bundle.get(AuthActivity.BUNDLE_USER_INFO);

        }

    }

    private void initFragment()     {

        CreateAccountFragment mFrag = new CreateAccountFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY, usuario);

        mFrag.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.google_reg_Activity_fragContainer, mFrag, GOOGLE_FRAG_TAG).commit();


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }
}