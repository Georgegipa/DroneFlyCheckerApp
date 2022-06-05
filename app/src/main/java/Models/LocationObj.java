package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationObj implements Parcelable {
    private double latitude;
    private double longitude;
    private String city;
    private boolean usingCachedLocation;

    public LocationObj(double latitude, double longitude, String city, boolean usingCachedLocation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.usingCachedLocation = usingCachedLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public boolean isUsingCachedLocation() {
        return usingCachedLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.city);
        dest.writeByte(this.usingCachedLocation ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.city = source.readString();
        this.usingCachedLocation = source.readByte() != 0;
    }

    protected LocationObj(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.city = in.readString();
        this.usingCachedLocation = in.readByte() != 0;
    }

    public static final Parcelable.Creator<LocationObj> CREATOR = new Parcelable.Creator<LocationObj>() {
        @Override
        public LocationObj createFromParcel(Parcel source) {
            return new LocationObj(source);
        }

        @Override
        public LocationObj[] newArray(int size) {
            return new LocationObj[size];
        }
    };
}
