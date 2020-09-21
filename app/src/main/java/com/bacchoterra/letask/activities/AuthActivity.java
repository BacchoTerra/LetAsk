package com.bacchoterra.letask.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.bacchoterra.letask.helper.MyHelper;
import com.bacchoterra.letask.model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    //Layout components
    private SignInButton btnGoogleSignIn;
    private Button btnEmailSignIn;
    private TextView txtVisitorSignIn;

    //Firebase components
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    //Google auth components
    private GoogleSignInClient mSignInClient;

    //ActivityResult identifier
    public static final int GOOGLE_INTENT = 100;

    //Extras (Bundle key for user with google info already set)
    public static final String BUNDLE_GOOGLE_USER_INFO_KEY = "bundle_user_info";

    //Google auth credential
    public static AuthCredential authCredential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();
    }

    private void init() {
        rootRef = FirebaseConfig.getFBDatabase();
        mAuth = FirebaseConfig.getFBAuth();

        initViews();
        initGoogleSignIn();

    }

    private void initViews() {
        btnGoogleSignIn = findViewById(R.id.auth_activity_btnGoogleSignIn);
        btnEmailSignIn = findViewById(R.id.auth_activity_btnEmailSignIn);
        txtVisitorSignIn = findViewById(R.id.auth_activity_txtVisitorSignIn);

        btnGoogleSignIn.setOnClickListener(this);
        btnEmailSignIn.setOnClickListener(this);
        txtVisitorSignIn.setOnClickListener(this);

    }

    private void initGoogleSignIn() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    private void handleGoogleSigInResult(Task<GoogleSignInAccount> completed) {

        try {
            GoogleSignInAccount acc = completed.getResult(ApiException.class);
            assert acc != null;
            handleGoogleUserExistence(acc);
        } catch (ApiException e) {
            Snackbar.make(btnEmailSignIn, "Error", Snackbar.LENGTH_SHORT).show();
        }

    }


    private void handleGoogleUserExistence(final GoogleSignInAccount acc){



        final String email = acc.getEmail();

        assert email != null;

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if (task.isSuccessful()){

                    if (task.getResult().getSignInMethods().contains(GoogleAuthProvider.PROVIDER_ID)){

                        MyHelper.showProgressDialog(AuthActivity.this);

                        authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);

                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    startActivity(new Intent
                                            (AuthActivity.this,MainActivity.class));
                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                    MyHelper.dismissProgressDialog();
                                }
                            }
                        });
                    }else if (task.getResult().getSignInMethods().contains(EmailAuthProvider.PROVIDER_ID)){

                        MyHelper.showSnackbarLong(R.string.this_account_has_email_auth,btnEmailSignIn);
                        mSignInClient.signOut();

                    } else {
                        authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);

                        Usuario usuario = new Usuario();

                        usuario.setName(acc.getDisplayName());
                        usuario.setEmail(acc.getEmail());
                        usuario.setId(Base64Custom.toBase64(acc.getEmail()));

                        Intent intent = new Intent(AuthActivity.this, GoogleRegistrationActivity.class);

                        intent.putExtra(BUNDLE_GOOGLE_USER_INFO_KEY,usuario);

                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }

                }else {
                    assert task.getException() != null;
                    Toast.makeText(AuthActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        MyHelper.dismissProgressDialog();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.auth_activity_btnGoogleSignIn:

                if (MyHelper.netConn(this)) {

                    Intent intent = mSignInClient.getSignInIntent();
                    startActivityForResult(intent, GOOGLE_INTENT);

                } else {
                    MyHelper.showSnackbarLong(R.string.no_internet_connection, txtVisitorSignIn);
                }
                break;

            case R.id.auth_activity_btnEmailSignIn:
                startActivity(new Intent(AuthActivity.this, EmailAuthActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GOOGLE_INTENT) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSigInResult(task);
        }


    }
}