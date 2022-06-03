package com.example.flychecker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<RawWeatherData> rawWeatherDataList = new ArrayList();

    private RecyclerView mRecyclerView;
    private WeatherAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView currentLocationTv;
    private double latitude, longitude;
    private CardView topCv;
    private String city;
    private Locator locator;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menu);

        //add about menu item
        MenuItem about = menu.findItem(R.id.action_about);
        //change to about activity
        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            }
        });

        //add settings menu item
        MenuItem settings = menu.findItem(R.id.action_settings);
        //change to settings activity
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
        });

        //add refresh button
        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getData();
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.setLocale(this);
        Helpers.setPrevTheme(this);
        setupGUI();
        locator = new Locator(this);
        if(!locator.isLocationEnabled() && !locator.prevLocationExists()) {
            exitAlert("GPS disabled", "Please enable GPS to use this app.");
        }
        else {
            locator.updateGPS(new OnSuccessListener<Location>() {
                //wait for location
                @Override
                public void onSuccess(Location location) {
                    getData();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh adapter data to change language inside the recycler view
        adapter.refreshData(rawWeatherDataList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    //this method is called when the user accepts or denies a permission
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //the user has selected the location permission
        if (requestCode == Locator.PERMISSION_FINE_LOCATION ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                recreate();//restart activity and trigger onCreate
            } else {
                //permission denied
                exitAlert(getString(R.string.gps_permission_denied), getString(R.string.gps_permission_denied_message));
            }
        }
    }

    //gui functions
    private void setupGUI()
    {
        setContentView(R.layout.activity_main2);
        setTitle(getString(R.string.safe_for_takeoff));
        currentLocationTv = findViewById(R.id.tv_current_location);
        mRecyclerView = findViewById(R.id.rv_list);
        topCv = findViewById(R.id.cv_top);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(this, rawWeatherDataList,
                new WeatherAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RawWeatherData item) {
                        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                        intent.putExtra("weather", item);
                        startActivity(intent);
                    }
                });
        mRecyclerView.setAdapter(adapter);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getData();
            }
        });
    }

    private void updateUI()
    {
        latitude = locator.getLatitude();
        longitude = locator.getLongitude();
        city = locator.getCity();
        Log.d(TAG, "updateUI: " + latitude + " " + longitude + " " + city);
        //geocoder failed to find city
        if (TextUtils.isEmpty(city)) {//check if city is empty or null
            Snackbar.make(findViewById(R.id.main_view), "Unable to find locations city name", Snackbar.LENGTH_LONG).show();
            topCv.setVisibility(View.GONE);//hide the top card view if the city name is not found
        }
        else {
            topCv.setVisibility(View.VISIBLE);
            currentLocationTv.setText(city);
        }
        if(locator.prevLocationLoaded())
        {
            Snackbar.make(findViewById(R.id.main_view), "Unable to retrieve latest gps location loading latest known position", Snackbar.LENGTH_LONG).show();
        }
    }

    private void exitAlert(String title,String message)
    {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    //generate the api url based on the location
    @NonNull
    private String generateURL(String timezoneID, double latitude, double longitude) {
        String apiURL = "https://api.open-meteo.com/v1/forecast";
        String hourlyVariables = "hourly=temperature_2m,relativehumidity_2m,precipitation,cloudcover,weathercode,windspeed_10m,windspeed_80m,windspeed_120m,windgusts_10m";
        String timezone = "timezone=" + timezoneID;
        String poslat = "latitude=" + latitude;
        String poslon = "longitude=" + longitude;
        String timeformat = "timeformat=unixtime";
        String windunit = "windspeed_unit=ms";
        String dailyVariables = "daily=sunrise,sunset";
        String url = apiURL + "?" + hourlyVariables + "&" + timezone + "&" + poslat + "&" + poslon + "&" + dailyVariables + "&" + timeformat + "&" + windunit;
        Log.d(TAG, "open-meteo url: " + url);
        return url;
    }

    //Check if there is internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //make the api call and load the data
    private void getData() {
        //get timezone from the device
        if (!isNetworkAvailable()) {
            //display a snackbar if there is no internet connection with a retry button
            Snackbar.make(findViewById(R.id.main_view), getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getData();
                        }
                    }).show();
            return;
        }
        //TODO:configure cache
        //TODO:configure timeouts
        updateUI();
        String url = generateURL(java.util.TimeZone.getDefault().getID(), latitude, longitude);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest weatherReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());
                try {
                    JSONObject jsonObject = response.getJSONObject("hourly");
                    parseJSONHourly(jsonObject);
                    //create a toasts to show that the data is loaded
                    Toast.makeText(getApplicationContext(), getString(R.string.data_refreshed), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.refreshData(rawWeatherDataList);//update the adapter
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
                //show a snackbar with a retry button if the request fails
                Snackbar.make(findViewById(R.id.main_view), getString(R.string.request_failed), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getData();
                            }
                        }).show();
            }
        });
        AppController.getInstance().addToRequestQueue(weatherReq);
    }

    private void parseJSONHourly(JSONObject jsonObject) throws JSONException {
        rawWeatherDataList.clear();
        JSONArray timeJsonArray = jsonObject.getJSONArray("time");
        JSONArray weathercodeJsonArray = jsonObject.getJSONArray("weathercode");
        JSONArray precipitationJsonArray = jsonObject.getJSONArray("precipitation");
        JSONArray cloudcoverJsonArray = jsonObject.getJSONArray("cloudcover");
        JSONArray windspeed_10mJsonArray = jsonObject.getJSONArray("windspeed_10m");
        JSONArray windspeed_80mJsonArray = jsonObject.getJSONArray("windspeed_80m");
        JSONArray windspeed_120mJsonArray = jsonObject.getJSONArray("windspeed_120m");
        JSONArray windgustsJsonArray = jsonObject.getJSONArray("windgusts_10m");
        JSONArray temperatureJsonArray = jsonObject.getJSONArray("temperature_2m");
        JSONArray humidityJsonArray = jsonObject.getJSONArray("relativehumidity_2m");

        for (int i = 0; i < timeJsonArray.length(); i++) {
            //parse data
            RawWeatherData rawWeatherData = new RawWeatherData();
            int unixTime = Integer.parseInt(timeJsonArray.getString(i));
            //keep only the last hour and future hours
            if (!Helpers.isFuture(unixTime) && !Helpers.isLastHour(unixTime))
                continue;
            rawWeatherData.setTime(unixTime);
            rawWeatherData.setPrecipitation(Double.parseDouble(precipitationJsonArray.getString(i)));
            rawWeatherData.setTemperature(Double.parseDouble(temperatureJsonArray.getString(i)));
            rawWeatherData.setWindSpeed10m(Double.parseDouble(windspeed_10mJsonArray.getString(i)));
            rawWeatherData.setWindSpeed80m(Double.parseDouble(windspeed_80mJsonArray.getString(i)));
            rawWeatherData.setWindSpeed120m(Double.parseDouble(windspeed_120mJsonArray.getString(i)));
            rawWeatherData.setPrecipitation(Double.parseDouble(precipitationJsonArray.getString(i)));
            rawWeatherData.setWeathercode(Integer.parseInt(weathercodeJsonArray.getString(i)));
            rawWeatherData.setCloudcover(Integer.parseInt(cloudcoverJsonArray.getString(i)));
            rawWeatherData.setGust(Double.parseDouble(windgustsJsonArray.getString(i)));
            rawWeatherData.setHumidity(Double.parseDouble(humidityJsonArray.getString(i)));
            rawWeatherDataList.add(rawWeatherData);
        }
    }

}