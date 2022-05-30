package com.example.flychecker;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Weather> mWeatherList = new ArrayList();

    private RecyclerView mRecyclerView;
    private WeatherAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    //weather for athens
    private static String url;

    public MainActivity() {
    }

    private void setURL(String timezoneID) {
        String baseURL = "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,precipitation,cloudcover,windspeed_10m&timeformat=unixtime";
        url = baseURL + "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,precipitation,cloudcover,windspeed_10m&timeformat=unixtime&latitude=37.9792&longitude=23.7166&timezone=" + timezoneID;
    }

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
        setContentView(R.layout.activity_main);
        retrieveLang();
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        adapter = new WeatherAdapter(this, mWeatherList);
        mRecyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
        getData();
    }

    //Check if there is internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void refreshItems() {
        getData();
        //swipeRefreshLayout.setRefreshing(false);
    }


    private void getData() {
        //get timezone from the device
        if(!isNetworkAvailable()) {
            //display a toast message if there is no internet connection
            //TODO: display a better message when there is no internet connection
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO:configure cache
        //TODO:configure timeouts
        String timezone = java.util.TimeZone.getDefault().getID();
        setURL(timezone);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest weatherReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());
                try {
                    JSONObject jsonObject = response.getJSONObject("hourly");
                    Log.d(TAG, jsonObject.toString());
                    JSONArray timeJsonArray = jsonObject.getJSONArray("time");
                    JSONArray precipitationJsonArray = jsonObject.getJSONArray("precipitation");
                    JSONArray cloudcoverJsonArray = jsonObject.getJSONArray("cloudcover");
                    JSONArray windspeedJsonArray = jsonObject.getJSONArray("windspeed_10m");
                    JSONArray temperatureJsonArray = jsonObject.getJSONArray("temperature_2m");
                    for (int i = 0; i < timeJsonArray.length(); i++) {
                        Weather weather = new Weather();
                        //weather.setTime(Helpers.convertUnixToDate(Integer.parseInt(timeJsonArray.getString(i)),false));
                        weather.setTime(Helpers.convertUnixToDate(Integer.parseInt(timeJsonArray.getString(i)), false));
                        weather.setPrecipitation(Float.parseFloat(precipitationJsonArray.getString(i)));
                        weather.setCloudcover(Float.parseFloat(cloudcoverJsonArray.getString(i)));
                        weather.setWindspeed_10m(Float.parseFloat(windspeedJsonArray.getString(i)));
                        weather.setTemperature_2m(Float.parseFloat(temperatureJsonArray.getString(i)));
                        mWeatherList.add(weather);
                    }
                    //create a toasts to show that the data is loaded
                    Toast.makeText(getApplicationContext(), "Data Refreshed!", Toast.LENGTH_SHORT).show();
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

    private void retrieveLang()
    {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = sharedPref.getString("language","");
        Helpers.setLocale(this,lang);
    }

}