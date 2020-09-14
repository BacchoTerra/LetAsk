package com.bacchoterra.letask.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.bacchoterra.letask.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.snackbar.Snackbar;

public abstract class MyHelper {

    //Alert dialog
    private static AlertDialog alertDialog;


    public static Boolean netConn (Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


        return connected;
    }

    public static void showSnackbarLong(int resId, View view){

        Snackbar.make(view,resId,Snackbar.LENGTH_LONG).show();

    }


    public static void showProgressDialog (Activity activity) {

        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_progress,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }

    public static void dismissProgressDialog(){

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }

    }


}
