package com.example.flychecker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra("weather")) {
            RawWeatherData weather = intent.getParcelableExtra("weather");
            setContentView(R.layout.activity_weather);
            WeatherAnalyzer wd = new WeatherAnalyzer(this,weather);
            Status status = wd.checkAll();
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Helpers.convertUnixToDate(this,weather.getTime()));
            //setup weather summary card view
            ImageView overallIv = findViewById(R.id.iv_overall);
            TextView overallSummaryTv = findViewById(R.id.tv_overall_summary);
            overallIv.setImageResource(Helpers.statusToIcon(status));
            setIcon(overallSummaryTv,wd.checkWeatherCode());
            overallSummaryTv.setText(wd.getWeatherCodeSummary());
            //setup temperature card view
            ImageView temperatureIv = findViewById(R.id.iv_temperature);
            TextView temperatureTv = findViewById(R.id.tv_temperature);
            TextView temperatureSummaryTv = findViewById(R.id.tv_temperature_summary);
            temperatureTv.setText(getString(R.string.Temperature)+":"+ PreferencesHelpers.getTemperatureUnit(this,weather.getTemperature()));
            temperatureIv.setColorFilter(getResources().getColor(Helpers.statusToColor(wd.checkTemperature())));
            dynamicHide(temperatureSummaryTv,wd.getTemperatureSummary());
            //setup humidity card view
            ImageView humidityIv = findViewById(R.id.iv_humidity);
            TextView humidityTv = findViewById(R.id.tv_humidity);
            TextView humiditySummaryTv = findViewById(R.id.tv_humidity_summary);
            humidityTv.setText(getString(R.string.Humidity)+":"+weather.getHumidity()+"%");
            humidityIv.setColorFilter(getResources().getColor(Helpers.statusToColor(wd.checkHumidity())));
            dynamicHide(humiditySummaryTv,wd.getHumiditySummary());
            //setup precipitation card view
            ImageView precipitationIv = findViewById(R.id.iv_precipitation);
            TextView precipitationTv = findViewById(R.id.tv_precipitation);
            TextView precipitationSummaryTv = findViewById(R.id.tv_precipitation_summary);
            precipitationTv.setText(getString(R.string.Precipitation)+":"+weather.getPrecipitation()+"mm");
            precipitationIv.setColorFilter(getResources().getColor(Helpers.statusToColor(wd.checkPrecipitation())));
            precipitationSummaryTv.setText(wd.getPrecipitationSummary());
            dynamicHide(precipitationSummaryTv,wd.getPrecipitationSummary());
            //setup wind card view
            ImageView windIv = findViewById(R.id.iv_wind);
            TextView wind10mTv = findViewById(R.id.tv_wind_10m);
            TextView wind80mTv = findViewById(R.id.tv_wind_80m);
            TextView wind120mTv = findViewById(R.id.tv_wind_120m);
            TextView windSummaryTv = findViewById(R.id.tv_wind_summary);
            wind10mTv.setText(getString(R.string.Wind)+"10m: "+ PreferencesHelpers.getWindSpeedUnit(this,weather.getWindSpeed10m()));
            wind80mTv.setText(getString(R.string.Wind)+" 80m: "+ PreferencesHelpers.getWindSpeedUnit(this,weather.getWindSpeed80m()));
            wind120mTv.setText(getString(R.string.Wind)+" 120m: "+ PreferencesHelpers.getWindSpeedUnit(this,weather.getWindSpeed120m()));
            windSummaryTv.setText(wd.getWindSpeedsSummary());
            windIv.setColorFilter(getResources().getColor(Helpers.statusToColor(wd.checkAllWindSpeeds())));
            Status[] windStatus = wd.checkWindSpeeds();
            setIcon(wind10mTv,windStatus[0]);
            setIcon(wind80mTv,windStatus[1]);
            setIcon(wind120mTv,windStatus[2]);
            //TODO: add wind summary
            //setup gusts card view
            ImageView gustsIv = findViewById(R.id.iv_gusts);
            TextView gustsTv = findViewById(R.id.tv_gusts);
            TextView gustsSummaryTv = findViewById(R.id.tv_gusts_summary);
            gustsTv.setText(getString(R.string.Gusts)+":"+ PreferencesHelpers.getWindSpeedUnit(this,weather.getGust()));
            gustsIv.setColorFilter(getResources().getColor(Helpers.statusToColor(wd.checkGust())));
            dynamicHide(gustsSummaryTv,wd.getGustSummary());
            //setup cloudcover card view
            ImageView cloudcoverIv = findViewById(R.id.iv_cloudcover);
            TextView cloudcoverTv = findViewById(R.id.tv_cloudcover);
            TextView cloudcoverSummaryTv = findViewById(R.id.tv_cloudcover_summary);
            cloudcoverTv.setText(getString(R.string.CloudCover)+":"+weather.getCloudcover()+"%");
            cloudcoverIv.setColorFilter(getResources().getColor(Helpers.statusToColor(wd.checkCloudCover())));
            dynamicHide(cloudcoverSummaryTv,wd.getCloudCoverSummary());
        }
    }

    //dynamically hide textview if text is empty
    private void dynamicHide(TextView item, String summary) {
        if(summary.isEmpty()){
            item.setVisibility(View.GONE);
        }
        else {
            item.setVisibility(View.VISIBLE);
            item.setText(summary);
        }
    }

    //adds an icon in front of the given textview
    private void setIcon(TextView item, Status status) {
        item.setCompoundDrawablesWithIntrinsicBounds(Helpers.statusToIcon(status), 0, 0, 0);
    }
}
