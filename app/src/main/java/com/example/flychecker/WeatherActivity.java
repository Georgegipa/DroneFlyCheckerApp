package com.example.flychecker;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra("weather")) {
            RawWeatherData weather = intent.getParcelableExtra("weather");
            setContentView(R.layout.activity_weather);
            AnalyzeWeatherData analyzeWeatherData = new AnalyzeWeatherData(weather);
            Status status = analyzeWeatherData.checkAll();
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Helpers.convertUnixToDate(this,weather.getTime()));
            //change color to action bar
//            getSupportActionBar().setBackgroundDrawable(
//                    new ColorDrawable(getResources().getColor(Helpers.statusToColor(status))));
            //setup overall card view
            ImageView overallIv = findViewById(R.id.iv_overall);
            TextView overallTv = findViewById(R.id.tv_overall);
            TextView overallSummaryTv = findViewById(R.id.tv_overall_summary);
            overallIv.setImageResource(Helpers.statusToIcon(status));
            overallTv.setText(Helpers.statusToString(status));
            overallSummaryTv.setText("hello there");
            //setup temperature card view
            ImageView temperatureIv = findViewById(R.id.iv_temperature);
            TextView temperatureTv = findViewById(R.id.tv_temperature);
            temperatureTv.setText(getString(R.string.Temperature)+":"+Helpers.getTemperatureUnit(this,weather.getTemperature()));
            temperatureIv.setColorFilter(getResources().getColor(Helpers.statusToColor(analyzeWeatherData.checkTemperature())));
            //setup humidity card view
            ImageView humidityIv = findViewById(R.id.iv_humidity);
            TextView humidityTv = findViewById(R.id.tv_humidity);
            humidityTv.setText(getString(R.string.Humidity)+":"+weather.getHumidity()+"%");
            humidityIv.setColorFilter(getResources().getColor(Helpers.statusToColor(analyzeWeatherData.checkHumidity())));
            //setup precipitation card view
            ImageView precipitationIv = findViewById(R.id.iv_precipitation);
            TextView precipitationTv = findViewById(R.id.tv_precipitation);
            precipitationTv.setText(getString(R.string.Precipitation)+":"+weather.getPrecipitation()+"mm");
            precipitationIv.setColorFilter(getResources().getColor(Helpers.statusToColor(analyzeWeatherData.checkPrecipitation())));

            //setup wind card view
            ImageView windIv = findViewById(R.id.iv_wind);
            TextView wind10mTv = findViewById(R.id.tv_wind_10m);
            TextView wind80mTv = findViewById(R.id.tv_wind_80m);
            TextView wind120mTv = findViewById(R.id.tv_wind_120m);
            wind10mTv.setText(getString(R.string.Wind)+"10m: "+Helpers.getWindSpeedUnit(this,weather.getWindSpeed10m()));
            wind80mTv.setText(getString(R.string.Wind)+" 80m: "+Helpers.getWindSpeedUnit(this,weather.getWindSpeed80m()));
            wind120mTv.setText(getString(R.string.Wind)+" 120m: "+Helpers.getWindSpeedUnit(this,weather.getWindSpeed120m()));
            Status[] windStatus = analyzeWeatherData.checkWindSpeeds();
            status = Status.SAFE;
            //loop through all wind speeds and get the highest status
            for(Status s: windStatus) {
                if(s.ordinal() > status.ordinal()) {
                    status = s;
                }
            }
            windIv.setColorFilter(getResources().getColor(Helpers.statusToColor(status)));
            //set drawable to textview
            wind10mTv.setCompoundDrawablesWithIntrinsicBounds(Helpers.statusToIcon(windStatus[0]), 0, 0, 0);
            wind80mTv.setCompoundDrawablesWithIntrinsicBounds(Helpers.statusToIcon(windStatus[1]), 0, 0, 0);
            wind120mTv.setCompoundDrawablesWithIntrinsicBounds(Helpers.statusToIcon(windStatus[2]), 0, 0, 0);
            //setup gusts card view
            ImageView gustsIv = findViewById(R.id.iv_gusts);
            TextView gustsTv = findViewById(R.id.tv_gusts);
            gustsTv.setText(getString(R.string.Gusts)+":"+Helpers.getWindSpeedUnit(this,weather.getGust()));
            gustsIv.setColorFilter(getResources().getColor(Helpers.statusToColor(analyzeWeatherData.checkGust())));
            //setup cloudcover card view
            ImageView cloudcoverIv = findViewById(R.id.iv_cloudcover);
            TextView cloudcoverTv = findViewById(R.id.tv_cloudcover);
            cloudcoverTv.setText(getString(R.string.CloudCover)+":"+weather.getCloudcover()+"%");
            cloudcoverIv.setColorFilter(getResources().getColor(Helpers.statusToColor(analyzeWeatherData.checkCloudCover())));
        }

    }

}
