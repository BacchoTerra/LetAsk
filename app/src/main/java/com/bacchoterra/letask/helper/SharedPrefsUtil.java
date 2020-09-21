package com.bacchoterra.letask.helper;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class SharedPrefsUtil {

    //Components for saving and retrieving user country;
    public static final String USER_COUNTRY_ARCH = "user_country_arch";
    public static final String USER_COUNTRY_KEY = "user_country_key";



    public static void saveUserCountry(Context context,String country){

        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_COUNTRY_ARCH,0);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER_COUNTRY_KEY,country);

        editor.apply();
    }

    public static String getUserCountry (Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_COUNTRY_ARCH,0);

        if (sharedPreferences.contains(USER_COUNTRY_KEY)){

         return sharedPreferences.getString(USER_COUNTRY_KEY,null);

        }

        return null;

    }

}
