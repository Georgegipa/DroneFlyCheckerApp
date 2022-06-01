package com.example.flychecker;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyzeWeatherData {

    private RawWeatherData weather;
    private double maxDroneSpeed;

    AnalyzeWeatherData(RawWeatherData weather) {
        this.weather = weather;
        maxDroneSpeed = 13;//TODO: get max drone speed from settings
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
    //check if current time is night or day
    private Status checkTime() {
        //throw not implemented yet exception
        return Status.SAFE;
    }

    public Status[] checkWindSpeeds() {
        //windspeed in m/s
        double[] windSpeeds= {weather.getWindSpeed10m(), weather.getWindSpeed80m(), weather.getWindSpeed120m()};
        Status[] status = new Status[windSpeeds.length];
        int i = 0;
        for (double windSpeed : windSpeeds) {
            if (windSpeed > maxDroneSpeed)
                status[i] = Status.DANGER;
                //drones can't fly faster than half of max speed when returning home automatically
                //manual control may be needed
            else if (windSpeed > maxDroneSpeed / 2)
                status[i] = Status.CAUTION;
            else
                status[i] = Status.SAFE;
            i++;
        }
        return status;
    }

    public Status checkPrecipitation() {
        double precip = weather.getPrecipitation();
        Status status;
        //TODO: ask user if they have a waterproof drone
        if(precip > 0 && precip < 1.0)
            status = Status.CAUTION;
        else if(precip >= 1.0 && precip < 2.0)
            status = Status.DANGER;
        else
            status = Status.SAFE;
        return status;
    }

    public Status checkHumidity() {
        double humidity = weather.getHumidity();
        Status status;
        if( humidity > 80)//extremely humid
            status = Status.CAUTION;
        else
            status = Status.SAFE;
        return status;
    }

    public Status checkCloudCover() {
        int cloudCover = weather.getCloudcover();
        Status status;
        if( cloudCover > 50 && cloudCover <90)//very cloudy
            status = Status.CAUTION;
        else  if (cloudCover >= 90)// extremely cloudy
            status = Status.DANGER;
        else
            status = Status.SAFE;
        return status;
    }

    public Status checkGust() {
        double gust = weather.getGust();
        Status status;
        //if gust is higher than 2/3 of max speed
        if(gust > maxDroneSpeed * 2/3)
            status = Status.DANGER;
        else if(gust > maxDroneSpeed / 2)
            status = Status.CAUTION;
        else
            status = Status.SAFE;
        return status;
    }

    public Status checkAll() {
        List<Status> statusList = new ArrayList<>(Arrays.asList(checkWindSpeeds()));
        statusList.add(checkTemperature());
        statusList.add(checkTime());
        statusList.add(checkPrecipitation());
        statusList.add(checkHumidity());
        statusList.add(checkCloudCover());
        statusList.add(checkGust());
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
