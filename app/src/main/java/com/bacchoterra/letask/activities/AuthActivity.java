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
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

    //Extras
    public static final String REGISTER_INTENT = "register_intent";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();
    }

    private void init() {

        mAuth = FirebaseConfig.getFBAuth();
        rootRef = FirebaseConfig.getFBDatabase();

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
            signInWithGoogleCredential(acc);
            MyHelper.showProgressDialog(this);
        } catch (ApiException e) {
            Snackbar.make(btnEmailSignIn, "Error", Snackbar.LENGTH_SHORT).show();
            signInWithGoogleCredential(null);
        }

    }

    private void signInWithGoogleCredential(GoogleSignInAccount acc) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    handleFirebaseSignIn(user);


                }
            }
        });

    }

    private void handleFirebaseSignIn(FirebaseUser user) {

        String email = user.getEmail();

        assert email != null;
        rootRef.child(FirebaseConfig.USERS_NOD)
                .child(Base64Custom.toBase64(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            Toast.makeText(AuthActivity.this, "exists", Toast.LENGTH_SHORT).show();
                            //TODO: Ir para a tela principal.
                        } else {

                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(AuthActivity.this);

                            if (account != null) {

                                Usuario usuario = new Usuario();

                                usuario.setName(account.getDisplayName());
                                usuario.setEmail(account.getEmail());
                                assert account.getEmail() !=null;
                                usuario.setId(Base64Custom.toBase64(account.getEmail()));

                                Intent intent = new Intent(AuthActivity.this,GoogleRegistrationActivity.class);
                                intent.putExtra(REGISTER_INTENT,usuario);

                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                startActivity(new Intent(AuthActivity.this,EmailAuthActivity.class));

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