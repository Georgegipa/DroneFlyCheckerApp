package com.example.flychecker;

import android.os.Parcel;
import android.os.Parcelable;

public class Weather implements Parcelable {
    private String temperature_2m;
    private int precipitation;
    private int cloudcover;
//    private float winddirection_10m;
//    private float windgusts_10m;
    private float windspeed_10m;
    private String time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.temperature_2m);
        dest.writeInt(this.precipitation);
        dest.writeInt(this.cloudcover);
//        dest.writeFloat(this.winddirection_10m);
//        dest.writeFloat(this.windgusts_10m);
        dest.writeFloat(this.windspeed_10m);
        dest.writeString(this.time);
    }

    public void readFromParcel(Parcel source) {
        this.temperature_2m = source.readString();
        this.precipitation = source.readInt();
        this.cloudcover = source.readInt();
//        this.winddirection_10m = source.readFloat();
//        this.windgusts_10m = source.readFloat();
        this.windspeed_10m = source.readFloat();
        this.time = source.readString();
    }

    public Weather() {
    }

    protected Weather(Parcel in) {
        this.temperature_2m = in.readString();
        this.precipitation = in.readInt();
        this.cloudcover = in.readInt();
//        this.winddirection_10m = in.readFloat();
//        this.windgusts_10m = in.readFloat();
        this.windspeed_10m = in.readFloat();
        this.time = in.readString();
    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel source) {
            return new Weather(source);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    //add getters and setters
    public String getTemperature_2m() {
        return temperature_2m;
    }

    public void setTemperature_2m(String temperature_2m) {
        this.temperature_2m = temperature_2m;
    }

    public int getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public int getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(int cloudcover) {
        this.cloudcover = cloudcover;
    }

//    public float getWinddirection_10m() {
//        return winddirection_10m;
//    }
//
//    public void setWinddirection_10m(float winddirection_10m) {
//        this.winddirection_10m = winddirection_10m;
//    }
//
//    public float getWindgusts_10m() {
//        return windgusts_10m;
//    }
//
//    public void setWindgusts_10m(float windgusts_10m) {
//        this.windgusts_10m = windgusts_10m;
//    }

    public float getWindspeed_10m() {
        return windspeed_10m;
    }

    public void setWindspeed_10m(float windspeed_10m) {
        this.windspeed_10m = windspeed_10m;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
