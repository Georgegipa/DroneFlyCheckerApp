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

public class Locator {
    private static final String TAG = Locator.class.getSimpleName();
    public static final int PERMISSION_FINE_LOCATION = 99;
    private double latitude;
    private double longitude;
    private String city;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Activity activity;

    public Locator(Activity activity)
    {
        this.activity = activity;
        if(isLocationEnabled())
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity);
        else//user has disabled location services
            fusedLocationProviderClient = null;
        Log.d(TAG, "Locator: constructor");
        updateGPS();
    }

    //save the location and city name to the shared preferences
    private void saveLocation()
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences("location", Context.MODE_PRIVATE).edit();
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.putString("city", city);
        editor.apply();
    }

    //retrieve the location and city name from the shared preferences
    private void retrieveLocation()
    {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("location", Context.MODE_PRIVATE);
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        city = sharedPreferences.getString("city", "");
    }

    //helper function that parses gps location to lat and long and city name
    private void getLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //use geocoder to get the city name
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                city = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            city = "";
            Log.d(TAG, "IOException: " + e.getMessage());
        }
        saveLocation();
    }

    public void updateGPS() {
        if(fusedLocationProviderClient == null)
        {
            retrieveLocation();
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        //permission granted now get the location
                        getLocation(location);
                    }
                }
            });
        }
        else {
            //permission not granted
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public boolean prevLocationExists()
    {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("location", Context.MODE_PRIVATE);
        //check if shared preferences contains the location
        return sharedPreferences.contains("latitude") && sharedPreferences.contains("longitude");
    }

    public boolean prevLocationLoaded()
    {
        return fusedLocationProviderClient == null;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }
}
