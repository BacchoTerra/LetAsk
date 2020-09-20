package com.bacchoterra.letask.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

public abstract class MyHelper {

    //Alert dialog
    private static AlertDialog alertDialog;

    //Firebase components
    private static FirebaseAuth mAuth;
    public static final int HAS_GOOGLE_AUTH = 1;
    public static final int HAS_EMAIL_AUTH = 2;
    public static final int NULL_AUTH = 3;

    private static int type;


    public static Boolean netConn(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


        return connected;
    }

    public static void showSnackbarLong(int resId, View view) {

        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();

    }


    public static void showProgressDialog(Activity activity) {

        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_progress, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyCornerDialog);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }

    public static void dismissProgressDialog() {

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

    }

    public static int checkEmailAuthCredential(String email) {


        mAuth = FirebaseConfig.getFBAuth();


        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if (task.isSuccessful()) {

                    SignInMethodQueryResult signInMethodQueryResult = task.getResult();
                    List<String> list = signInMethodQueryResult.getSignInMethods();

                    assert list != null;
                    if (list.isEmpty()) {

                        type = NULL_AUTH;

                    } else if (list.contains(GoogleAuthProvider.PROVIDER_ID)) {

                        type = HAS_GOOGLE_AUTH;

                    } else if (list.contains(EmailAuthProvider.PROVIDER_ID)) {

                        type = HAS_EMAIL_AUTH;

                    }

                } else {

                    //Consider 0 as a error.
                    type = 0;

                }

            }
        });

        return type;

    }


}
