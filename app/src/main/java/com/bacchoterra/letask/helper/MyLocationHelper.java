package com.bacchoterra.letask.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocationHelper {

    //Initialization components
    private Context context;


    //Google play_services location
    private Address address;
    private FusedLocationProviderClient flpc;
    private LocationCallback lCallback;
    private LocationRequest lRequest;
    private String returnedCountry;

    public MyLocationHelper(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public String getCurrentLocation() {

        flpc = LocationServices.getFusedLocationProviderClient(context);


        flpc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                    try {
                        List<Address> list = geocoder.getFromLocation(lat, lng, 1);
                        address = list.get(0);
                        returnedCountry = address.getCountryName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    getNewCurrentLocation();
                    flpc.requestLocationUpdates(lRequest, lCallback, null);
                }

            }
        });

        return returnedCountry;
    }

    private void getNewCurrentLocation() {

        lRequest = LocationRequest.create();
        lRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        lRequest.setInterval(6000);
        lCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            address = list.get(0);
                            returnedCountry = address.getCountryName();
                            flpc.removeLocationUpdates(lCallback);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

    }

}
