package com.example.flychecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import Helpers.*;
import Models.LocationObj;
import Models.RawWeatherData;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<RawWeatherData> rawWeatherDataList = new ArrayList();

    private RecyclerView mRecyclerView;
    private WeatherAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView currentLocationTv, notificationBarTv, dataSourceTv, lastRefreshTv, coordinatesTv;
    private double latitude, longitude;
    private CardView topCv;
    private String city;
    private LinearLayout topLl;
    private String currentLanguage;
    private volatile boolean usingCachedLocation;
    private long lastRefreshTime;

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

        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {//change to settings activity
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

        //add gps button
        MenuItem gpsItem = menu.findItem(R.id.action_gps);
        Locator locator = new Locator(this);
        gpsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Activity activity = MainActivity.this;
                if (locator.isLocationEnabled()) {
                    swipeRefreshLayout.setRefreshing(true);
                    locator.getCurrentLocation(new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            city = Locator.locationToCityName(activity, latitude, longitude);
                            Log.d(TAG, "onLocationChanged: " + city);
                            getData();
                        }
                    });
                } else {
                    Snackbar.make(topLl, R.string.gps_permission_denied_message, Snackbar.LENGTH_LONG).show();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.setLocale(this);
        setupGUI();
        //get the current language
        currentLanguage = PreferencesHelpers.getLanguage(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("location")) {
            LocationObj loc = intent.getParcelableExtra("location");
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            city = Locator.locationToCityName(this, latitude, longitude);
            currentLocationTv.setText(city);
            usingCachedLocation = loc.isUsingCachedLocation();
            if (usingCachedLocation)//using cached location
                Snackbar.make(findViewById(R.id.main_view), R.string.location_from_cache, Snackbar.LENGTH_LONG).show();

        }
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh adapter data to change language inside the recycler view
        adapter.refreshData(rawWeatherDataList);
        //refresh toolbar language
        invalidateOptionsMenu();
        updateCollapsable();
        //check if language has changed
        Log.d(TAG, "Current language: " + PreferencesHelpers.getLanguage(this)+" Last language: "+currentLanguage);
        if (!currentLanguage.equals(PreferencesHelpers.getLanguage(this))) {
            Log.d(TAG, "Language has changed");
            city = Locator.locationToCityName(this, latitude, longitude);
            Log.d(TAG, "City: " + city);
            currentLanguage = PreferencesHelpers.getLanguage(this);
            currentLocationTv.setText(city);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //collapse the top card view
        topLl.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //gui functions
    private void setupGUI() {
        setContentView(R.layout.activity_main);
        //setTitle(getString(R.string.safe_for_takeoff));
        currentLocationTv = findViewById(R.id.tv_current_location);
        mRecyclerView = findViewById(R.id.rv_list);
        topCv = findViewById(R.id.cv_top);
        topLl = findViewById(R.id.ll_top);
        coordinatesTv = findViewById(R.id.tv_coordinates);
        dataSourceTv = findViewById(R.id.tv_data_source);
        lastRefreshTv = findViewById(R.id.tv_last_refresh);
        lastRefreshTv.setText(getString(R.string.last_update) + "-");
        notificationBarTv = findViewById(R.id.tv_notification_bar);
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

        //show linearlayout when pressed
        topCv.setOnClickListener(v -> {
            if (topLl.getVisibility() == View.GONE) {
                topLl.setVisibility(View.VISIBLE);
            } else {
                topLl.setVisibility(View.GONE);
            }
        });
    }

    private void updateCollapsable() {
        if (lastRefreshTime != 0)
            lastRefreshTv.setText(getString(R.string.last_update) + Helpers.convertUnixToTime(this, lastRefreshTime));
        if (usingCachedLocation) {
            //set drawable
            dataSourceTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_memory_24, 0, 0, 0);
            dataSourceTv.setText(R.string.using_cached_location);
        } else {
            dataSourceTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_gps_fixed_24, 0, 0, 0);
            dataSourceTv.setText(R.string.using_cur_location);
        }

        coordinatesTv.setText(new StringBuilder().append(getString(R.string.coordinates)).append(String.format(" %.4f , %.4f", latitude, longitude)).toString());
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
                    .setAction(R.string.retry, v -> getData()).show();
            return;
        }
        String url = generateURL(java.util.TimeZone.getDefault().getID(), latitude, longitude);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest weatherReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("hourly");
                    parseJSONHourly(jsonObject);
                    //create a toasts to show that the data is loaded
                    notificationBar(getString(R.string.data_refreshed), getColor(R.color.green), 3500);//similar to LONG_DELAY
                    swipeRefreshLayout.setRefreshing(false);
                    lastRefreshTime = System.currentTimeMillis();
                    updateCollapsable();
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
                        .setAction(R.string.retry, v -> getData()).show();
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
            rawWeatherData.setWeathercode((int)Double.parseDouble(weathercodeJsonArray.getString(i)));
            rawWeatherData.setCloudcover((int)Double.parseDouble(cloudcoverJsonArray.getString(i)));
            rawWeatherData.setGust(Double.parseDouble(windgustsJsonArray.getString(i)));
            rawWeatherData.setHumidity(Double.parseDouble(humidityJsonArray.getString(i)));
            rawWeatherDataList.add(rawWeatherData);
        }
    }

    //show the notification bar for 5 seconds and then hide it
    private void notificationBar(String text, int color, int delay) {
        notificationBarTv.setVisibility(View.VISIBLE);
        notificationBarTv.setBackgroundColor(color);
        notificationBarTv.setText(text);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationBarTv.setVisibility(View.GONE);
            }
        }, delay);
    }
}