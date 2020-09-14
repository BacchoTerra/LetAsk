package com.bacchoterra.letask.authfragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class RegisterEmailFragment extends Fragment implements View.OnClickListener {

    //Context
    private Context context;

    //Layout components
    private View view;
    private TextInputEditText editName;
    private TextInputEditText editPassword;
    private FloatingActionButton fabLocation;
    private FloatingActionButton fabBirthDate;
    private TextView txtBirthDate;

    //Calendar components;
    private Calendar calendar;
    private SimpleDateFormat sdf;


    public RegisterEmailFragment() {
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
        view = inflater.inflate(R.layout.fragment_register_email, container, false);
        init();

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (context == null) {
            this.context = getActivity();
        }
        if (calendar == null) {
            this.calendar = Calendar.getInstance();
        }
        if (sdf == null) {
            this.sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        }

    }

    private void init() {
        initViews();

    }

    private void initViews() {

        editName = view.findViewById(R.id.frag_register_email_editName);
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
                calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + 1);

                txtBirthDate.setText(sdf.format(calendar.getTime()));


            }
        });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.frag_register_email_fabBirthDate:
                initMaterialDatePicker();
        }
    }
}
