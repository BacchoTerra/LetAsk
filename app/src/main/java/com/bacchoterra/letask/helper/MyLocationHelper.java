package com.bacchoterra.letask.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyLocationHelper {

    private Context context;

    //Google play_services location
    private Address address;
    private FusedLocationProviderClient flpc;
    private LocationCallback lCallback;
    private LocationRequest lRequest;


    public MyLocationHelper(Context context) {
        this.context = context;
    }
}
