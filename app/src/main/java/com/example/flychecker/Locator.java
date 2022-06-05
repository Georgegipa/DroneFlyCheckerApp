package com.example.flychecker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Helpers.PreferencesHelpers;

public class Locator {
    private static final String TAG = Locator.class.getSimpleName();
    private double latitude;
    private double longitude;
    private boolean usingCachedLocation;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Activity activity;

    public Locator(Activity activity) {
        this.activity = activity;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity);
    }

    //save the location and city name to the shared preferences
    private void saveLocation() {
        SharedPreferences.Editor editor = activity.getSharedPreferences("location", Context.MODE_PRIVATE).edit();
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }

    //retrieve the location and city name from the shared preferences
    public void retrieveLocation() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("location", Context.MODE_PRIVATE);
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
    }

    public static boolean locationExistsInCache(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
        return sharedPreferences.contains("latitude") && sharedPreferences.contains("longitude") && sharedPreferences.contains("city");
    }

    //helper function that parses gps location to lat and long and city name
    private void getLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        saveLocation();
    }

    public static String locationToCityName(Activity act,double latitude,double longitude)
    {
        //use geocoder to get the city name
        Geocoder geocoder = new Geocoder(act, Locale.getDefault());
        Log.d("Locale", Locale.getDefault().toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            Log.d(TAG, "IOException: " + e.getMessage());
        }
        return "";
    }

    public void updateGPS(OnSuccessListener<Location> locationListener) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    //permission granted now get the location
                    //getLocation(location);
                    getLocation(location);
                    usingCachedLocation = false;
                } else {
                    //permission granted but no location yet
                    usingCachedLocation = true;
                    retrieveLocation();
                }
                locationListener.onSuccess(location);
            });
        }
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public boolean prevLocationLoaded() {
        return usingCachedLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
