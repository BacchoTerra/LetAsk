package com.bacchoterra.letask.authfragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.activities.EmailAuthActivity;
import com.bacchoterra.letask.activities.MainActivity;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.MyHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;


public class SignInFragment extends Fragment implements View.OnClickListener {

    //Context
    private Context context;
    private Activity activity;

    //Layout components
    private View view;
    private TextInputEditText editPassword;
    private TextView txtForgotPassword;
    private Button btnLogin;

    //stuff from from arguments
    private String userEmail;

    //Firebase
    private FirebaseAuth mAuth;


    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        init();

        return view;
    }

    private void init() {

        mAuth = FirebaseConfig.getFBAuth();

        initViews();
        getEmailFromActivity();


    }

    private void initViews() {
        editPassword = view.findViewById(R.id.frag_insert_password_editPassword);
        txtForgotPassword = view.findViewById(R.id.frag_insert_password_txtForgotPassword);
        btnLogin = view.findViewById(R.id.frag_insert_password_btnLogin);

        btnLogin.setOnClickListener(this);
    }

    private void getEmailFromActivity() {

        if (getArguments() != null) {
            userEmail = getArguments().getString(EmailAuthActivity.BUNDLE_KEY);
        }


    }

    private void signUserIn() {


        MyHelper.showProgressDialog(activity);
        assert editPassword.getText() != null;
        mAuth.signInWithEmailAndPassword(userEmail, editPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    activity.startActivity(new Intent(getActivity(), MainActivity.class));
                    activity.finish();

                } else {

                    String error = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = context.getString(R.string.incorrect_password);
                    } catch (Exception e) {
                        error = e.getMessage();
                    }

                    Snackbar.make(editPassword, error, Snackbar.LENGTH_LONG).show();

                }

                MyHelper.dismissProgressDialog();
            }
        });

    }

    @Override
    public void onClick(View view) {

        if (view == btnLogin) {

            if (MyHelper.netConn(context)) {

                signUserIn();

            }

        }

    }
}