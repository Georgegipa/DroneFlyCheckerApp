package com.example.flychecker;

public class AnalyzedWeather {

    //check if current time is night or day
    private Status checkTime(String time) {
        //throw not implemented yet exception
        throw new UnsupportedOperationException("Not implemented yet");
    }
    //check if the temperature is safe
    private Status checkTemperature(double temp) {
        //temp is in Celsius
        Status status;
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

    private Status checkWindSpeed(double windSpeed) {
        Status status;
        double maxDroneSpeed = 13;//TODO: get max drone speed from settings
        //windspeed in m/s
        if(windSpeed > maxDroneSpeed)
            status = Status.DANGER;
        //drones can't fly faster than half of max speed when returning home automatically
        //manual control may be needed
        else if(windSpeed > maxDroneSpeed/2)
            status = Status.CAUTION;
        else
            status = Status.SAFE;
        return status;
    }

    private Status checkPrecipitation(double precip) {
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

    private Status checkHumidity(double humidity) {
        Status status;
        if( humidity > 80)//extremely humid
            status = Status.CAUTION;
        else
            status = Status.SAFE;
        return status;
    }


    private void checkCloudCover(float cloudCover) {

    }

    private void checkGusts(float gusts) {

    }

    AnalyzedWeather(RawWeatherData weather) {

    }
}
