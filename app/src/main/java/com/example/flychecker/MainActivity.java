package com.example.flychecker;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private ListView listView;
    private WeatherAdapter adapter;

    //weather for athens
    private static final String url = "https://api.open-meteo.com/v1/forecast?latitude=37.9792&longitude=23.7166&timezone=Europe/Athens&hourly=temperature_2m,precipitation,cloudcover,windspeed_10m";

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

        listView = findViewById(R.id.list);

        adapter = new WeatherAdapter(getApplicationContext(), mWeatherList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.d(TAG, "onCreate: " + url);
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
                        weather.setTime(timeJsonArray.getString(i));
                        weather.setPrecipitation(Integer.parseInt(precipitationJsonArray.getString(i)));
                        weather.setCloudcover(Integer.parseInt(cloudcoverJsonArray.getString(i)));
                        weather.setWindspeed_10m(Float.parseFloat(windspeedJsonArray.getString(i)));
                        weather.setTemperature_2m(temperatureJsonArray.getString(i));
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