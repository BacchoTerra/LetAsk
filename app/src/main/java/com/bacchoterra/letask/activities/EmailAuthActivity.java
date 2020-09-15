package com.bacchoterra.letask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.authfragments.CreateAccountFragment;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

public class EmailAuthActivity extends AppCompatActivity implements View.OnClickListener {

    //Layout components
    private ViewGroup rootLayout;
    private Toolbar toolbar;
    private TextView txtHeadline;
    private TextInputLayout inputLayoutEmail;
    private TextInputEditText editEmail;
    private Button btnContinue;
    private ProgressBar progressBar;

    //Firebase components
    private FirebaseAuth mAuth;

    //Fragment initialization tags
    public static final String EMAIL_FRAG_TAG = "email_registration";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);
        init();
    }

    private void init() {

        initViews();
        initToolbar();
        initTextWatcher();

        mAuth = FirebaseConfig.getFBAuth();

    }

    private void initViews() {

        rootLayout = findViewById(R.id.email_activity_rootLayout);
        toolbar = findViewById(R.id.email_activity_toolbar);
        txtHeadline = findViewById(R.id.email_Activity_txtHeadline);
        inputLayoutEmail = findViewById(R.id.email_Activity_inputLayoutEmail);
        editEmail = findViewById(R.id.email_Activity_editEmail);
        btnContinue = findViewById(R.id.email_Activity_btnConfirmEmail);
        progressBar = findViewById(R.id.email_activity_progressBar);

        btnContinue.setOnClickListener(this);


    }

    private void initToolbar() {

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        }

    }

    private void initTextWatcher() {

        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String current = editable.toString();

                if (current.contains("@") && current.contains(".com") && current.length() >= 6) {

                    btnContinue.setEnabled(true);

                } else {
                    btnContinue.setEnabled(false);
                }

            }
        });

    }

    private void checkEmailAuthCredential(String email) {

        progressBar.setVisibility(View.VISIBLE);
        changeVisibilityWitFade(btnContinue);
        mAuth = FirebaseConfig.getFBAuth();


        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if (task.isSuccessful()) {

                    SignInMethodQueryResult signInMethodQueryResult = task.getResult();
                    assert signInMethodQueryResult != null;
                    List<String> list = signInMethodQueryResult.getSignInMethods();

                    assert list != null;
                    if (list.isEmpty()) {
                        editEmail.setEnabled(false);

                        initFragment(new CreateAccountFragment(),R.anim.slide_in_down);


                    } else if (list.contains(GoogleAuthProvider.PROVIDER_ID)) {

                        //TODO: Snackbar mostrando que esse email tem um auth do google

                    } else if (list.contains(EmailAuthProvider.PROVIDER_ID)) {

                        //TODO:ir para o Fragmento de colocar a senha.

                    }

                } else {

                    Toast.makeText(EmailAuthActivity.this, "error", Toast.LENGTH_SHORT).show();
                    changeVisibilityWitFade(btnContinue);

                }

                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void initFragment(Fragment fragment,int animation){

        getSupportFragmentManager().beginTransaction().setCustomAnimations(animation,0).replace(R.id.email_activity_fragContainer,fragment, EMAIL_FRAG_TAG).commit();


    }

    private void changeVisibilityWitFade(View target){

        TransitionManager.beginDelayedTransition(rootLayout,new Fade());
        target.setVisibility(target.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE);

    }


    @Override
    public void onClick(View view) {

        if (view == btnContinue) {

            String email = editEmail.getText().toString();

            checkEmailAuthCredential(email);



        }

    }
}