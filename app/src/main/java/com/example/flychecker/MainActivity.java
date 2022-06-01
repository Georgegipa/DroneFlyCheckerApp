package com.example.flychecker;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<RawWeatherData> rawWeatherDataList = new ArrayList();

    private RecyclerView mRecyclerView;
    private WeatherAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private double latitude, longitude;
    private String cityName;
    private TextView currentLocationTv;

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
        setPrevOptions();
        getLocation();
        setContentView(R.layout.activity_main2);
        setTitle(getString(R.string.safe_for_takeoff));
        currentLocationTv = findViewById(R.id.tv_current_location);
        currentLocationTv.setText(currentLocationTv.getText() + cityName);
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(this, rawWeatherDataList,
                new WeatherAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RawWeatherData item) {
                        //TODO: navigate to detail activity
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
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //generate the api url based on the location
    @NonNull
    private String setURL(String timezoneID) {
        String apiURL = "https://api.open-meteo.com/v1/forecast";
        String hourlyVariables = "hourly=temperature_2m,relativehumidity_2m,shortwave_radiation,precipitation,cloudcover,weathercode,windspeed_10m,windspeed_80m,windspeed_120m,windspeed_180m,windgusts_10m";
        String timezone = "timezone=" + timezoneID;
        String poslat = "latitude=" + "37.9792";
        String poslon = "longitude=" + "23.7166";
        String timeformat = "timeformat=unixtime";
        String windunit = "windspeed_unit=ms";
        String dailyVariables = "daily=sunrise,sunset";
        String url = apiURL + "?" + hourlyVariables + "&" + timezone + "&" + poslat + "&" + poslon + "&" + dailyVariables + "&" + timeformat + "&" + windunit;
        Log.d(TAG, "url: " + url);
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
            //display a toast message if there is no internet connection
            //TODO: display a better message when there is no internet connection
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO:configure cache
        //TODO:configure timeouts
        String url = setURL(java.util.TimeZone.getDefault().getID());
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
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
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
            rawWeatherData.setTime(Integer.parseInt(timeJsonArray.getString(i)));
            rawWeatherData.setPrecipitation(Double.parseDouble(precipitationJsonArray.getString(i)));
            rawWeatherData.setTemperature(Double.parseDouble(temperatureJsonArray.getString(i)));
            rawWeatherData.setWindSpeed10m(Double.parseDouble(windspeed_10mJsonArray.getString(i)));
            rawWeatherData.setWindSpeed80m(Double.parseDouble(windspeed_80mJsonArray.getString(i)));
            rawWeatherData.setWindSpeed120m(Double.parseDouble(windspeed_120mJsonArray.getString(i)));
            rawWeatherData.setPrecipitation(Double.parseDouble(precipitationJsonArray.getString(i)));
            rawWeatherData.setWeathercode(Integer.parseInt(weathercodeJsonArray.getString(i)));
            rawWeatherData.setCloudcover(Double.parseDouble(cloudcoverJsonArray.getString(i)));
            rawWeatherData.setGust(Double.parseDouble(windgustsJsonArray.getString(i)));
            rawWeatherData.setHumidity(Double.parseDouble(humidityJsonArray.getString(i)));
            rawWeatherDataList.add(rawWeatherData);
        }
    }

    private void setPrevOptions() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = sharedPref.getString("language", "");
        Helpers.setLocale(this, lang);
        Helpers.setPrevTheme(this);
    }

    private void getLocation() {
        //request location permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        //TODO:check if location is enabled
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO: bother the user to enable location

        } else {
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location location;
            if (network_enabled) {

                location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }
            //use geocoder to get the city name
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //display a toast message with all of the data
            Toast.makeText(this, "Longitude: " + longitude + "\nLatitude: " + latitude + "\nCity: " + cityName, Toast.LENGTH_LONG).show();
        }


    }

}