package com.example.flychecker;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.RawWeatherData;
import Models.Status;
import Helpers.PreferencesHelpers;

public class WeatherAnalyzer {

    private final RawWeatherData weather;
    private final int maxDroneSpeed;
    private final Context context;

    WeatherAnalyzer(Context context, RawWeatherData weather) {
        this.weather = weather;
        this.context = context;
        maxDroneSpeed = PreferencesHelpers.getMaxSpeed(context);
    }


    public Status checkWeatherCode() {
        int wCode = weather.getWeathercode();
        if (wCode <= 3)
            return Status.SAFE;
        else if (wCode == 45)
            return Status.CAUTION;
        else
            return Status.DANGER;
    }

    public String getWeatherCodeSummary() {
        //data from https://open-meteo.com/en/docs/
        int wCode = weather.getWeathercode();
        String result = "";
        switch (wCode) {
            case 0:
                return context.getString(R.string.clear_sky);
            case 1:
                return context.getString(R.string.mainly_clear);
            case 2:
                return context.getString(R.string.partly_cloudy);
            case 3:
                return context.getString(R.string.overcast);
            case 45:
            case 48:
                return context.getString(R.string.fog);
            case 51:
            case 53:
            case 55:
                result = context.getString(R.string.drizzle);
                break;
            case 56:
            case 57:
                result = context.getString(R.string.freezing) + " " + context.getString(R.string.drizzle);
                break;
            case 61:
            case 63:
            case 65:
                result = context.getString(R.string.rain);
                break;
            case 66:
            case 67:
                result = context.getString(R.string.freezing) + " " + context.getString(R.string.rain);
                break;
            case 71:
            case 73:
            case 75:
                result = context.getString(R.string.snow_fall);
                break;
            case 77:
                return context.getString(R.string.snow_grains);
            case 80:
            case 81:
            case 82:
                result = context.getString(R.string.rain_showers);
                break;
            case 85:
            case 86:
                result = context.getString(R.string.snow_showers);
                break;
            case 95:
            case 96:
            case 99:
                return context.getString(R.string.thunderstorm);

        }
        //51,56,61,66,71,80,85 is light
        //53,63,73,81 is moderate
        //55,57,65,67,75,82,86 is heavy
        switch (wCode) {
            case 51:
            case 56:
            case 61:
            case 66:
            case 71:
            case 80:
            case 85:
                //return (light) result
                return "(" + context.getString(R.string.weather_light) + ")" + " " + result;
            case 53:
            case 63:
            case 73:
            case 81:
                return "(" + context.getString(R.string.weather_moderate) + ")" + " " + result;
            case 55:
            case 57:
            case 65:
            case 67:
            case 75:
            case 82:
            case 86:
                return "(" + context.getString(R.string.weather_heavy) + ")" + " " + result;
        }
        return context.getString(R.string.unknown_weather);
    }

    //check if the temperature is safe
    public Status checkTemperature() {
        //temp is in Celsius
        Status status;
        double temp = weather.getTemperature();
        if (temp > 0 && temp <= 5) //between -10 and 0 WARN
            status = Status.CAUTION;
        else if (temp > 5 && temp < 35) //between 0 and 35 SAFE
            status = Status.SAFE;
        else if (temp >= 35 && temp < 40)//between 35 and 40 CAUTION
            status = Status.CAUTION;
        else //above 40 and below 0 DANGER
            status = Status.DANGER;
        return status;
    }

    public String getTemperatureSummary() {
        Status status = checkTemperature();
        String summary = "";
        double temp = weather.getTemperature();
        switch (status) {
            default:
                break;
            case CAUTION:
                if (temp > 0 && temp <= 5)
                    summary = context.getString(R.string.low_temperature_caution);
                else
                    summary = context.getString(R.string.high_temperature_caution);
                break;
            case DANGER:
                summary = context.getString(R.string.temperature_danger);
                break;
        }
        return summary;
    }

    public Status[] checkWindSpeeds() {
        //windspeed in m/s
        double[] windSpeeds = {weather.getWindSpeed10m(), weather.getWindSpeed80m(), weather.getWindSpeed120m()};
        Status[] status = new Status[windSpeeds.length];
        int i = 0;
        for (double windSpeed : windSpeeds) {
            if (windSpeed > maxDroneSpeed)
                status[i] = Status.DANGER;
                //drones can't fly faster than half of max speed when returning home automatically
                //manual control may be needed
            else if (windSpeed > (double)maxDroneSpeed / 2)
                status[i] = Status.CAUTION;
            else
                status[i] = Status.SAFE;
            i++;
        }
        return status;
    }

    public Status checkAllWindSpeeds() {
        Status status = Status.SAFE;
        //loop through all wind speeds and get the highest status
        for (Status s : checkWindSpeeds()) {
            if (s.ordinal() > status.ordinal()) {
                status = s;
            }
        }
        return status;
    }

    public String getWindSpeedsSummary() {
        switch (checkAllWindSpeeds()) {
            case CAUTION:
                return context.getString(R.string.wind_caution);
            case DANGER:
                return context.getString(R.string.wind_danger);
            default:
                return "";
        }
    }

    public Status checkPrecipitation() {
        double precip = weather.getPrecipitation();
        Status status;
        if (precip > 0 && precip < 1.0)
            status = Status.CAUTION;
        else if (precip >= 1.0)
            status = Status.DANGER;
        else
            status = Status.SAFE;
        return status;
    }

    public String getPrecipitationSummary() {
        switch (checkPrecipitation()) {
            default:
                return "";
            case CAUTION:
                return context.getString(R.string.precipitation_caution);
            case DANGER:
                return context.getString(R.string.precipitation_danger);
        }
    }

    public Status checkHumidity() {
        double humidity = weather.getHumidity();
        Status status;
        if (humidity > 80)//extremely humid
            status = Status.CAUTION;
        else
            status = Status.SAFE;
        return status;
    }

    public String getHumiditySummary() {
        if (checkHumidity() == Status.CAUTION) {
            return context.getString(R.string.humidity_caution);
        }
        return "";
    }

    public Status checkCloudCover() {
        int cloudCover = weather.getCloudcover();
        Status status;
        if (cloudCover > 70 && cloudCover < 90)//very cloudy
            status = Status.CAUTION;
        else if (cloudCover >= 90)// extremely cloudy
            status = Status.DANGER;
        else
            status = Status.SAFE;
        return status;
    }

    public String getCloudCoverSummary() {
        switch (checkCloudCover()) {
            case CAUTION:
                return context.getString(R.string.cloud_cover_caution);
            case DANGER:
                return context.getString(R.string.cloud_cover_danger);
            default:
                return "";
        }
    }

    public Status checkGust() {
        double gust = weather.getGust();
        Status status;
        if (gust > maxDroneSpeed)
            status = Status.DANGER;
        else if (gust > maxDroneSpeed * (double) 3 / 4)
            status = Status.CAUTION;
        else
            status = Status.SAFE;
        return status;
    }

    public String getGustSummary() {
        switch (checkGust()) {
            case CAUTION:
                return context.getString(R.string.gust_caution);
            case DANGER:
                return context.getString(R.string.gust_danger);
            default:
                return "";
        }
    }

    public Status checkAll() {
        List<Status> statusList = new ArrayList<>(Arrays.asList(checkWindSpeeds()));
        statusList.add(checkTemperature());
        statusList.add(checkPrecipitation());
        statusList.add(checkHumidity());
        statusList.add(checkCloudCover());
        statusList.add(checkGust());
        statusList.add(checkWeatherCode());
        Status finalStatus = Status.SAFE;
        for (Status s : statusList) {
            if (s == Status.DANGER)
                finalStatus = Status.DANGER;
            else if (s == Status.CAUTION)
                finalStatus = Status.CAUTION;
        }
        return finalStatus;
    }
}
