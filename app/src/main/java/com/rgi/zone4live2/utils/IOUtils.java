package com.rgi.zone4live2.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.ActivityLogin;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.activites.SplashActivity;
import com.rgi.zone4live2.apps.Zone4LiveApp;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IOUtils {

    public static final String PREFS_NAME = "ZONE4";

    public static boolean isInternetPresent(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            @SuppressLint("MissingPermission") NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static void mySnackBarInternet(Context mContext, View view) {
        Snackbar.make(view, mContext.getString(R.string.msg_internet_connection), Snackbar.LENGTH_LONG)
                .setActionTextColor(mContext.getResources().getColor(R.color.black))
                .show();
    }

    public static void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (((Activity) context).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean is_marshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void showSnackBar(Activity context, String msg) {
        //CommonUtils.hideKeyboard(context);
        View parentLayout = context.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.darkgreen));
        snackbar.show();
    }

    public static void errorShowSnackBar(Activity context, String msg) {
        //CommonUtils.hideKeyboard(context);
        View parentLayout = context.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.red));
        snackbar.show();
    }

    public static void logout(MainActivity mainActivity) {
        SharedPreferences mPrefs = mainActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        Zone4LiveApp.cache.clearAllPreference(mainActivity);

        // After logout redirect user to Login Activity
        Intent i = new Intent(mainActivity, ActivityLogin.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        mainActivity.startActivity(i);
    }

    public static String currentdate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(todayDate);
    }

    public static String getAddress(FragmentActivity activity, double lat, double logn) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(activity, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, logn, 1);
            if (addresses.size() > 0) {
                /* Log.e("Location", "getAddress: " + addresses.get(0).getAddressLine(0) + "-" +
                        addresses.get(0).getLocality() + "-" + addresses.get(0).getAdminArea() + "-" + addresses.get(0).getCountryName()
                        + "-" + addresses.get(0).getPostalCode() + "-" + addresses.get(0).getFeatureName()); */
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getAddressNew(Context activity, double lat, double logn) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(activity, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, logn, 1);
            if (addresses.size() > 0) {
               /* Log.e("Location", "getAddress: " + addresses.get(0).getAddressLine(0) + "-" +
                        addresses.get(0).getLocality() + "-" + addresses.get(0).getAdminArea() + "-" + addresses.get(0).getCountryName()
                        + "-" + addresses.get(0).getPostalCode() + "-" + addresses.get(0).getFeatureName());*/
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getLocality(FragmentActivity activity, double lat, double logn) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(activity, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, logn, 1);
            if (addresses.size() > 0)
                return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void logout(SplashActivity splashActivity, String splash) {
        SharedPreferences mPrefs = splashActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        Zone4LiveApp.cache.clearAllPreference(splashActivity);
    }

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    public static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    public static boolean isInArea(double lat, double logn, double latitude, double longitude) {
        Log.e("TAG", "dailog: lat a "+ lat +" "+logn );
        Log.e("TAG", "dailog: lat b "+latitude+" "+longitude);
        LatLng latLngA = new LatLng(lat,logn);
        LatLng latLngB = new LatLng(latitude,longitude);

        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        double distance = locationA.distanceTo(locationB);
        if(distance > 200){
            return false;
        } else {
            return true;
        }
    }
}
