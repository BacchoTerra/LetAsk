package com.bacchoterra.letask.authfragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private FloatingActionButton fabLocation;
    private FloatingActionButton fabBirthDate;
    private TextView txtBirthDate;
    private Button btnSignIn;

    //Calendar components;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private int currentYear;

    //Model
    private Usuario argumentedUsuario;
    private Usuario usuario;

    private String userName;
    private String userEmail;
    private String userId;
    private Long userBirthDate;

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


        return view;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        this.context = context;

        if (calendar == null) {
            this.calendar = Calendar.getInstance();
            this.currentYear = calendar.get(Calendar.YEAR);
        }
        if (sdf == null) {
            this.sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        }

    }

    private void init() {
        initViews();
        checkFragHost();

    }

    private void initViews() {

        inputLayoutName = view.findViewById(R.id.frag_register_email_inputLayoutName);
        editName = view.findViewById(R.id.frag_register_email_editName);
        inputLayoutPassword = view.findViewById(R.id.frag_register_email_inputLayoutPassword);
        editPassword = view.findViewById(R.id.frag_register_email_editPassword);
        fabLocation = view.findViewById(R.id.frag_register_email_fabLocation);
        fabBirthDate = view.findViewById(R.id.frag_register_email_fabBirthDate);
        txtBirthDate = view.findViewById(R.id.frag_register_email_txtBirthDate);
        btnSignIn = view.findViewById(R.id.frag_register_email_btnSignIn);

        fabLocation.setOnClickListener(this);
        fabBirthDate.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);


    }

    private void initMaterialDatePicker() {

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();

        MaterialDatePicker<Long> picker = builder.build();

        picker.show(getChildFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {

                calendar.setTimeInMillis(selection);


                if (calendar.get(Calendar.YEAR) > currentYear) {

                    txtBirthDate.setText(R.string.invalid_date);
                    fabBirthDate.setImageResource(R.drawable.ic_baseline_cake_24);
                    userBirthDate = null;

                } else {
                    txtBirthDate.setText(sdf.format(calendar.getTime()));
                    fabBirthDate.setImageResource(R.drawable.ic_round_green_check_24);
                    userBirthDate = selection;

                }


            }
        });


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


        userName = editName.getText().toString();
        userEmail = argumentedUsuario.getEmail();
        userId = Base64Custom.toBase64(argumentedUsuario.getEmail());


        usuario = new Usuario();

        usuario.setName(userName);
        usuario.setEmail(userEmail);
        usuario.setBirthDate(userBirthDate);
        usuario.setId(Base64Custom.toBase64(userId));

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
            case R.id.frag_register_email_fabBirthDate:
                initMaterialDatePicker();
                break;

            case R.id.frag_register_email_btnSignIn:

                assert getTag() != null;
                if (getTag().equals(GoogleRegistrationActivity.GOOGLE_FRAG_TAG)) {

                    if (MyHelper.netConn(context)){
                        userName = editName.getText().toString();
                        userEmail = argumentedUsuario.getEmail();
                        userId = Base64Custom.toBase64(userEmail);


                        if (!userName.isEmpty()) {
                            if (userBirthDate != null) {

                                saveGoogleUser();


                            } else {
                                MyHelper.showSnackbarLong(R.string.select_a_valid_birth_date, view);
                            }
                        } else {
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
