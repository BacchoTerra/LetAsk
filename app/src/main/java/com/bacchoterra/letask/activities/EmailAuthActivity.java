package com.bacchoterra.letask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.bacchoterra.letask.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EmailAuthActivity extends AppCompatActivity implements View.OnClickListener {

    //Layout components
    private Toolbar toolbar;
    private TextInputLayout inputLayoutEmail;
    private TextInputEditText editEmail;
    private Button btnContinue;

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

    }

    private void initViews() {

        toolbar = findViewById(R.id.email_activity_toolbar);
        inputLayoutEmail = findViewById(R.id.email_Activity_inputLayoutEmail);
        editEmail = findViewById(R.id.email_Activity_editEmail);
        btnContinue = findViewById(R.id.email_Activity_btnConfirmEmail);


    }

    private void initToolbar(){

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        }

    }

    private void initTextWatcher(){

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

                if (current.contains("@") && current.contains(".com") && current.length() >=6){

                    btnContinue.setEnabled(true);

                }else {
                    btnContinue.setEnabled(false);
                }

            }
        });



    }


    @Override
    public void onClick(View view) {

    }
}