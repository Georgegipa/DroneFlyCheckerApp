package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class RawWeatherData implements Parcelable {
    private int unixtime;//unixtimestamp
    private double temperature;//Celsius
    private double gust;//m/s
    private double windSpeed10m;//m/s
    private double windSpeed80m;//m/s
    private double windSpeed120m;//m/s
    private double precipitation;//mm
    private int cloudcover;
    private int weathercode;
    private double humidity;

    public int getTime() {
        return unixtime;
    }

    public void setTime(int unixtime) {
        this.unixtime = unixtime;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public int getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(int cloudcover) {
        this.cloudcover = cloudcover;
    }

    public int getWeathercode() {
        return weathercode;
    }

    public void setWeathercode(int weathercode) {
        this.weathercode = weathercode;
    }

    public double getGust() {
        return gust;
    }

    public void setGust(double gust) {
        this.gust = gust;
    }

    public double getWindSpeed10m() {
        return windSpeed10m;
    }

    public void setWindSpeed10m(double windSpeed10m) {
        this.windSpeed10m = windSpeed10m;
    }

    public double getWindSpeed80m() {
        return windSpeed80m;
    }

    public void setWindSpeed80m(double windSpeed80m) {
        this.windSpeed80m = windSpeed80m;
    }

    public double getWindSpeed120m() {
        return windSpeed120m;
    }

    public void setWindSpeed120m(double windSpeed120m) {
        this.windSpeed120m = windSpeed120m;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.unixtime);
        dest.writeDouble(this.temperature);
        dest.writeDouble(this.gust);
        dest.writeDouble(this.windSpeed10m);
        dest.writeDouble(this.windSpeed80m);
        dest.writeDouble(this.windSpeed120m);
        dest.writeDouble(this.precipitation);
        dest.writeInt(this.cloudcover);
        dest.writeInt(this.weathercode);
        dest.writeDouble(this.humidity);
    }

    public void readFromParcel(Parcel source) {
        this.unixtime = source.readInt();
        this.temperature = source.readDouble();
        this.gust = source.readDouble();
        this.windSpeed10m = source.readDouble();
        this.windSpeed80m = source.readDouble();
        this.windSpeed120m = source.readDouble();
        this.precipitation = source.readDouble();
        this.cloudcover = source.readInt();
        this.weathercode = source.readInt();
        this.humidity = source.readDouble();
    }

    public RawWeatherData() {
    }

    protected RawWeatherData(Parcel in) {
        this.unixtime = in.readInt();
        this.temperature = in.readDouble();
        this.gust = in.readDouble();
        this.windSpeed10m = in.readDouble();
        this.windSpeed80m = in.readDouble();
        this.windSpeed120m = in.readDouble();
        this.precipitation = in.readDouble();
        this.cloudcover = in.readInt();
        this.weathercode = in.readInt();
        this.humidity = in.readDouble();
    }

    public static final Parcelable.Creator<RawWeatherData> CREATOR = new Parcelable.Creator<RawWeatherData>() {
        @Override
        public RawWeatherData createFromParcel(Parcel source) {
            return new RawWeatherData(source);
        }

        @Override
        public RawWeatherData[] newArray(int size) {
            return new RawWeatherData[size];
        }
    };
}
