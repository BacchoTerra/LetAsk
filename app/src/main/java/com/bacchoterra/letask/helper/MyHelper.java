package com.bacchoterra.letask.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public abstract class MyHelper {


    public static Boolean netConn (Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


        return connected;
    }

    public static void showSnackbarLong(int resId, View view){

        Snackbar.make(view,resId,Snackbar.LENGTH_LONG).show();

    }


}
