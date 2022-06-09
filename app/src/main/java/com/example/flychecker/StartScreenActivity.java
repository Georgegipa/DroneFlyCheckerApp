package com.example.flychecker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Objects;

import Helpers.*;
import Models.LocationObj;

//this class shows the splash screen , it also manages the permissions and some of the shared preferences
public class StartScreenActivity extends AppCompatActivity {
    private static final int build_version = android.os.Build.VERSION.SDK_INT;
    private static final int PERMISSION_FINE_LOCATION = 99;
    private Locator locator;
    private int times = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        //hide the action bar
        Helpers.setPrevTheme(this);
        Helpers.setLocale(this);
        locator = new Locator(this);
        //check if the user has granted the permission to access the location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //if not, request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        } else {
            updateLoc();
        }
    }

    private void updateLoc() {
        Log.d("SplashScreenActivity", "updateLoc: " + times);
        if (locator.isLocationEnabled() || Locator.locationExistsInCache(this)) {
            //wait for location
            if (PreferencesHelpers.getGPSpref(this) && locator.isLocationEnabled()) {//user prefers to use GPS instead of cache
                locator.getCurrentLocation(location -> {
                    intentToMainActivity(new LocationObj(location.getLatitude(), location.getLongitude(), false));
                });
            } else//used fused location provider or cache (if fused location provider is not available)
                locator.updateGPS(location -> {
                    //load data to class to avoid null values
                    double latitude = locator.getLatitude();
                    double longitude = locator.getLongitude();
                    Log.d("TAG", "onSuccess: " + latitude + " " + longitude + " " + locator.prevLocationLoaded());
                    if (latitude == 200 && longitude == 200)
                        intentToMainActivity(null);
                    else {
                        intentToMainActivity(new LocationObj(latitude, longitude, locator.prevLocationLoaded()));
                    }
                });
        } else {//user has not granted the permission to access the location
            //however, gps is not enabled & no old location exists in shared preferences
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Enabled");
            builder.setMessage("Press navigate to enable location services and restart app with location enabled" +
                    "\n\n" + "Location is required only for the first time to get the current location");
            builder.setPositiveButton("navigate", (dialog, which) -> {
                //intent to location settings
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            builder.setNegativeButton("cancel", (dialog, which) -> {
                //intent to MainActivity
                finish();
            });
            builder.show();
        }
    }

    @Override
    //this method is called when the user accepts or denies a permission
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //the user has selected the location permission
        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                updateLoc();
            } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                // the user has permanently denied the permission
                displayManualPermit();
            } else {
                // the user has denied the permission
                getPermission();
            }
        }
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request permission
            times++;
            //if user has denied the permission twice in android 11 and above then
            // it's the same as if the user has permanently denied the permission
            if ((build_version >= android.os.Build.VERSION_CODES.Q) && times == 2) {
                displayManualPermit();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void displayManualPermit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have denied the permission to access the location. Please allow the permission to continue using the app."
                + "Settings screen.\n\nSelect Permissions -> Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }

    private void intentToMainActivity(LocationObj locationObj) {

        if (locationObj != null) {
            Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
            intent.putExtra("location", locationObj);
            startActivity(intent);
        } else {
            //if the gps is not enabled exit the app
            if (!locator.isLocationEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Location is not enabled. Please enable location services and restart app with location enabled");
                builder.setPositiveButton("ok", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
            }
            /**if the none of the apps that use fused location have been used recently
             the app will fail to get the location
            In order to avoid this behavior use location manager to get the current location**/
            locator.getCurrentLocation(location -> {
                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                intent.putExtra("location", new LocationObj(location.getLatitude(), location.getLongitude(), false));
                startActivity(intent);
            });
        }
    }
}
