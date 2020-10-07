package com.bacchoterra.letask.authfragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.activities.AuthActivity;
import com.bacchoterra.letask.activities.EmailAuthActivity;
import com.bacchoterra.letask.activities.GoogleRegistrationActivity;
import com.bacchoterra.letask.activities.MainActivity;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.bacchoterra.letask.helper.MyHelper;
import com.bacchoterra.letask.helper.SharedPrefsUtil;
import com.bacchoterra.letask.firebase.UsuarioFirebase;
import com.bacchoterra.letask.model.Usuario;
import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    //Context
    private Context context;
    private Activity activity;

    //Layout components
    private View view;
    private TextInputLayout inputLayoutName;
    private TextInputEditText editName;
    private TextInputLayout inputLayoutPassword;
    private TextInputEditText editPassword;
    private AppCompatAutoCompleteTextView actvCountry;
    private Button btnSignIn;

    //Model for google login
    private Usuario googleUsuario;

    //model for email login
    private Usuario emailUsuario;
    private String userEmail; // this string come from EmailAuthActivity as a argument for this fragment.


    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    //Extras
    private List<String> countryList = new ArrayList<>();


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
        mAuth = FirebaseConfig.getFBAuth();
        rootRef = FirebaseConfig.getFBDatabase();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.activity = getActivity();

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

    private void initAutoCompleteCountry() {

        World.init(context);

        final List<Country> countries = World.getAllCountries();

        for (Country c : countries) {

            String countryName = c.getName();
            countryList.add(countryName);

        }

        ArrayAdapter<String> actvCountries = new ArrayAdapter<>(context, android.R.layout.simple_expandable_list_item_1, countryList);

        actvCountry.setAdapter(actvCountries);


    }

    private boolean isNameOnlyLetters(String name) {

        char[] letters = name.toCharArray();

        for (char c : letters) {
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }

        return true;

        /*Pattern p = Pattern.compile("[a-zA-z ]{5,50}");

        Matcher m = p.matcher(name);


        return m.matches();

         */

    }

    private void createAccount() {


        assert editName.getText() != null;

        final String name = editName.getText().toString();
        final String country = actvCountry.getText().toString();


        assert getTag() != null;

        if (getTag().equals(GoogleRegistrationActivity.GOOGLE_FRAG_TAG)) {

            if (name.length() >= 5 && isNameOnlyLetters(name)) {

                if (!country.isEmpty() && countryList.contains(country)) {

                    MyHelper.showProgressDialog(activity);

                    mAuth.signInWithCredential(AuthActivity.authCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {


                                        googleUsuario.setName(name);
                                        googleUsuario.setCountry(country);


                                        saveUserInDatabase(googleUsuario);

                                    } else {
                                        MyHelper.showSnackbarLong(R.string.error_creatin_account, view);
                                    }
                                }
                            });

                } else if (country.isEmpty()) {
                    MyHelper.showSnackbarLong(R.string.please_select_your_country, view);
                } else if (!countryList.contains(country)) {
                    MyHelper.showSnackbarLong(R.string.select_a_valid_country, view);
                }


            } else if (name.length() < 5) {
                MyHelper.showSnackbarLong(R.string.name_should_countain_5_min, view);

            } else if (!isNameOnlyLetters(name)) {
                MyHelper.showSnackbarLong(R.string.name_can_only_contain_letters, view);

            }


        }

        if (getTag().equals(EmailAuthActivity.EMAIL_REGISTRATION_FRAG_TAG)) {


            assert editPassword.getText() != null;
            String password = editPassword.getText().toString();

            if (name.length() >= 5 && isNameOnlyLetters(name)) {

                if (!(password.length() < 6) && !password.contains(" ")) {

                    if (!country.isEmpty() && countryList.contains(country)) {

                        MyHelper.showProgressDialog(activity);

                        mAuth.createUserWithEmailAndPassword(userEmail, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            emailUsuario = new Usuario();
                                            emailUsuario.setName(name);
                                            emailUsuario.setEmail(userEmail.toLowerCase());
                                            emailUsuario.setCountry(country);
                                            emailUsuario.setId(Base64Custom.toBase64(userEmail.toLowerCase()));

                                            UsuarioFirebase.updateUserName(emailUsuario.getName());

                                        }

                                    }
                                });


                    } else if (country.isEmpty()) {
                        MyHelper.showSnackbarLong(R.string.please_select_your_country, view);
                    } else if (!countryList.contains(country)) {
                        MyHelper.showSnackbarLong(R.string.select_a_valid_country, view);
                    }

                } else if (password.length() < 6) {

                    MyHelper.showSnackbarLong(R.string.password_must_contain_6_chars, view);

                } else {

                    MyHelper.showSnackbarLong(R.string.invalid_password, view);
                }

            } else if (name.length() < 5) {
                MyHelper.showSnackbarLong(R.string.name_should_countain_5_min, view);

            } else if (!isNameOnlyLetters(name)) {
                MyHelper.showSnackbarLong(R.string.name_can_only_contain_letters, view);

            }


        }


    }

    private void saveUserInDatabase(final Usuario user) {

        rootRef.child(FirebaseConfig.USERS_NOD).child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    SharedPrefsUtil.saveUserCountry(context, user.getCountry(), user.getEmail());
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    context.startActivity(intent);

                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else {
                    deleteUserFromFBAuth();
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }

                MyHelper.dismissProgressDialog();
            }
        });


    }


    private void deleteUserFromFBAuth() {
        assert mAuth.getCurrentUser() != null;
        mAuth.getCurrentUser().delete();
    }

    private void checkFragHost() {

        assert getTag() != null;
        if (getTag().equals(GoogleRegistrationActivity.GOOGLE_FRAG_TAG)) {

            if (getArguments() != null) {
                googleUsuario = (Usuario) getArguments().getSerializable(GoogleRegistrationActivity.BUNDLE_USER_INFO_KEY);
            }

            inputLayoutPassword.setVisibility(View.GONE);
            editName.setText(googleUsuario.getName());
        } else {

            if (getArguments() != null) {
                userEmail = getArguments().getString(EmailAuthActivity.BUNDLE_KEY);
            }

        }


    }

    @Override
    public void onClick(View view) {


        if (view == btnSignIn) {

            if (MyHelper.netConn(context)) {
                createAccount();
            } else {
                MyHelper.showSnackbarLong(R.string.no_internet_connection, view);
            }


        }

    }

}
