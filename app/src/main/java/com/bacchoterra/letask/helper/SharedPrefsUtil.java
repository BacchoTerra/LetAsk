package com.bacchoterra.letask.helper;

import android.content.Context;
import android.content.SharedPreferences;



public abstract class SharedPrefsUtil {

    //Components for saving and retrieving user country;
    public static final String USER_COUNTRY_ARCH = "user_country_arch";


    /**
     *
     * @param context the context (usually an Activity) that is saving the value.
     * @param country a string containing a country name, the actual value being saved.
     * @param email the email of current user, its used as a key for the preferences, which provide
     *              multi accounts saving users country in the same device.
     *
     * @see SharedPrefsUtil#getUserCountry(Context, String);
     */
    public static void saveUserCountry(Context context, String country, String email) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_COUNTRY_ARCH, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString(email, country);

        editor.apply();
    }


    /**
     *
     * @param context the context (usually an Activity) that is retrieving the value.
     * @param email the email of current user, as this is the key for accessing the value saved.
     * @return user's country saved when creating the account,or null if there is nothing saved
     * (e.g if the user changed or reset the device); in this case, the proper action would be
     * fetching the country from FirebaseDatabase and saving it.
     */
    public static String getUserCountry(final Context context, final String email) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_COUNTRY_ARCH, 0);



        if (sharedPreferences.contains(email)) {

            return sharedPreferences.getString(email, "rolassa");

        }else {
            return null;
        }

    }

}
