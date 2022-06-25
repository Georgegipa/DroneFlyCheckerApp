package com.example.flychecker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Helpers.*;


/**
 * This class is used to get the location of the user.
 * It gets gps coordinates using 3 different methods:
 * 1. FusedLocationProviderClient
 * 2. Using last known location from SharedPreferences
 * 3. Using LocationManager to get the location
 *
 * If FusedLocationProviderClient has not been invoked from any other app then it fails.
 * If cached location exists then it is used, else it gets the location from LocationManager.
 */
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
    private void retrieveLocation() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("location", Context.MODE_PRIVATE);
        //latitude and longitude cant be more than 90 and 200 respectively
        //so if the location isn't found the false location will be checked
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "200"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "200"));
    }

    public static boolean locationExistsInCache(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
        return sharedPreferences.contains("latitude") && sharedPreferences.contains("longitude");
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
        //convert string to Locale
        Geocoder geocoder = new Geocoder(act,new Locale(PreferencesHelpers.getLanguage(act)));
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality() + ", " + countryCodeToEmoji(addresses.get(0).getCountryCode());
            }
        } catch (IOException e) {
            Log.d(TAG, "IOException: " + e.getMessage());
        }
        return "";
    }

    private static String countryCodeToEmoji(String countryCode) {
        int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
    }

    public void updateGPS(OnSuccessListener<Location> locationListener) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    //gps is available
                    getLocation(location);
                    usingCachedLocation = false;
                }
                else {
                    //fused location is unavailable so use the cached location
                    usingCachedLocation = true;
                    retrieveLocation();
                }
                locationListener.onSuccess(location);
            });
        }
    }

    public void getCurrentLocation(LocationListener ll)
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, location -> {
                if (location != null) {
                    getLocation(location);
                    ll.onLocationChanged(location);
                }
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
