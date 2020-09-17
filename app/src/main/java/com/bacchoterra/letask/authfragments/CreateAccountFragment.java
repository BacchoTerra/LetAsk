package com.bacchoterra.letask.authfragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.activities.GoogleRegistrationActivity;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.bacchoterra.letask.helper.MyHelper;
import com.bacchoterra.letask.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    //Context
    private Context context;

    //Layout components
    private View view;
    private TextInputLayout inputLayoutName;
    private TextInputEditText editName;
    private TextInputLayout inputLayoutPassword;
    private TextInputEditText editPassword;
    private AppCompatAutoCompleteTextView actvCountry;
    private Button btnSignIn;

    //Model
    private Usuario argumentedUsuario;
    private Usuario usuario;

    private String userName;
    private String userEmail;
    private String userId;
    private String userCountry;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;


    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_account, container, false);
        init();

        String s = Locale.getDefault().getCountry();

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();


        return view;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    private void init() {
        initViews();
        checkFragHost();
        initAutoCompleteCountry();

    }

    private void initViews() {

        inputLayoutName = view.findViewById(R.id.frag_register_email_inputLayoutName);
        editName = view.findViewById(R.id.frag_register_email_editName);
        inputLayoutPassword = view.findViewById(R.id.frag_register_email_inputLayoutPassword);
        editPassword = view.findViewById(R.id.frag_register_email_editPassword);
        actvCountry = view.findViewById(R.id.frag_register_email_actvCountry);


        btnSignIn = view.findViewById(R.id.frag_register_email_btnSignIn);
        btnSignIn.setOnClickListener(this);


    }

    private void initAutoCompleteCountry(){

        String [] vector = {"Brazil", "USA","Russia","China","Barbados"};

        ArrayAdapter<String> countries = new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1,vector);

        actvCountry.setAdapter(countries);


    }


    private void checkFragHost() {

        assert getTag() != null;
        if (getTag().equals(GoogleRegistrationActivity.GOOGLE_FRAG_TAG)) {

            if (getArguments() != null) {
                argumentedUsuario = (Usuario) getArguments().getSerializable(GoogleRegistrationActivity.BUNDLE_KEY);
            }

            inputLayoutPassword.setVisibility(View.GONE);
            editName.setText(argumentedUsuario.getName());
        }


    }

    private void saveGoogleUser() {


        usuario = new Usuario();

        usuario.setName(userName);
        usuario.setEmail(userEmail);
        usuario.setId(Base64Custom.toBase64(userId));
        usuario.setCountry(userCountry);

        rootRef = FirebaseConfig.getFBDatabase();

        rootRef.child(FirebaseConfig.USERS_NOD)
                .child(userId)
                .setValue(usuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(context, "salvo", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.frag_register_email_btnSignIn:

                assert getTag() != null;
                if (getTag().equals(GoogleRegistrationActivity.GOOGLE_FRAG_TAG)) {

                    if (MyHelper.netConn(context)){
                        userName = editName.getText().toString();
                        userEmail = argumentedUsuario.getEmail();
                        userCountry = actvCountry.getText().toString();
                        userId = Base64Custom.toBase64(userEmail);

                        if (userName.length() >= 5) {

                            if (!userCountry.isEmpty()){
                                saveGoogleUser();

                            }else {
                                MyHelper.showSnackbarLong(R.string.please_select_your_country, view);
                            }


                        } else{
                            MyHelper.showSnackbarLong(R.string.invalid_user_name, view);
                        }


                    }else {
                        MyHelper.showSnackbarLong(R.string.no_internet_connection,view);
                    }

                }


        }
    }

    //TODO: ADCICIONAR PERMISSOES DE GPS E FEATURE DE PEGAR LOCALIZAÇAO NO CLICK DO FABLOCATION

}
