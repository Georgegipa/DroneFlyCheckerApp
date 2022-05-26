package com.example.flychecker;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ProgressDialog pDialog;

    private List<Weather> mWeatherList = new ArrayList();

    private RecyclerView mRecyclerView;
    private WeatherAdapter adapter;

    //weather for athens
    private static String url = "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,precipitation,cloudcover,windspeed_10m&timeformat=unixtime&latitude=37.9792&longitude=23.7166&timezone=Europe/Athens";

    private void setURL(String timezoneID)
    {
        url = "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,precipitation,cloudcover,windspeed_10m&timeformat=unixtime&latitude=37.9792&longitude=23.7166&timezone="+timezoneID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get timezone from the device
        String timezone = java.util.TimeZone.getDefault().getID();
        setURL(timezone);
        Log.d(TAG, "Timezone: "+timezone);



        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.d(TAG, "onCreate: " + url);
        Context currentContext = this;
        //display the whole response in the log

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
                        weather.setTime(Helpers.convertUnixToDate(Integer.parseInt(timeJsonArray.getString(i)),false));
                        weather.setPrecipitation(Integer.parseInt(precipitationJsonArray.getString(i)));
                        weather.setCloudcover(Integer.parseInt(cloudcoverJsonArray.getString(i)));
                        weather.setWindspeed_10m(Float.parseFloat(windspeedJsonArray.getString(i)));
                        weather.setTemperature_2m(Float.parseFloat(temperatureJsonArray.getString(i)));
                        mWeatherList.add(weather);
                    }
                    //print a list of the weather objects
//                    for (Weather weather : mWeatherList) {
//
//                        Log.d(TAG, weather.getTime() + " " + weather.getPrecipitation() + " " + weather.getCloudcover() + " " + weather.getWindspeed_10m() + " " + weather.getTemperature_2m());
//                    }
                    //create a toasts to show that the data is loaded
                    Toast.makeText(getApplicationContext(), "Data loaded", Toast.LENGTH_SHORT).show();
                    hidePDialog();
                    adapter = new WeatherAdapter(currentContext,mWeatherList);
                    mRecyclerView.setAdapter(adapter);


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }



    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}