package com.bacchoterra.letask.authfragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.activities.GoogleRegistrationActivity;
import com.bacchoterra.letask.model.Usuario;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    //Context
    private Context context;

    //Layout components
    private View view;
    private TextInputEditText editName;
    private TextInputLayout inputLayoutPassword;
    private TextInputEditText editPassword;
    private FloatingActionButton fabLocation;
    private FloatingActionButton fabBirthDate;
    private TextView txtBirthDate;

    //Calendar components;
    private Calendar calendar;
    private SimpleDateFormat sdf;

    //Model
    private Usuario argumentedUsuario;


    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

        editName = view.findViewById(R.id.frag_register_email_editName);
        inputLayoutPassword = view.findViewById(R.id.frag_register_email_inputLayoutPassword);
        editPassword = view.findViewById(R.id.frag_register_email_editPassword);
        fabLocation = view.findViewById(R.id.frag_register_email_fabLocation);
        fabBirthDate = view.findViewById(R.id.frag_register_email_fabBirthDate);
        txtBirthDate = view.findViewById(R.id.frag_register_email_txtBirthDate);

        fabLocation.setOnClickListener(this);
        fabBirthDate.setOnClickListener(this);

    }

    private void initMaterialDatePicker() {

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();

        MaterialDatePicker<Long> picker = builder.build();

        picker.show(getChildFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {

                calendar.setTimeInMillis(selection);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

                txtBirthDate.setText(sdf.format(calendar.getTime()));
                fabBirthDate.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));


            }
        });


    }

    private void checkFragHost(){

        assert getTag() != null;
        if (getTag().equals(GoogleRegistrationActivity.GOOGLE_FRAG_TAG)){

            if (getArguments() != null){
                argumentedUsuario = (Usuario) getArguments().getSerializable(GoogleRegistrationActivity.BUNDLE_KEY);
            }

            inputLayoutPassword.setVisibility(View.GONE);
            editName.setText(argumentedUsuario.getName());

        }


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.frag_register_email_fabBirthDate:
                initMaterialDatePicker();
        }
    }
}
