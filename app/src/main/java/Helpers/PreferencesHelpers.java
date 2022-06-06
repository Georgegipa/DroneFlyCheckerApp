package Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import androidx.fragment.app.FragmentActivity;

public abstract class PreferencesHelpers {

    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", languageCode);
        editor.apply();
    }

    //get app language
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getString("language", "en");
    }

    public static String getTemperatureUnit(Context context, double ms) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String temp = prefs.getString("temperature_unit", "");
        if (prefs.contains("temperature_unit")) {
            switch (temp) {
                case "°F":
                    return UnitConverters.convertToFarhenheit(ms) + "°F";
                case "°K":
                    return UnitConverters.convertToKelvin(ms) + "°K";
                default:
                    return ms + "°C";
            }
        } else
            return ms + "°C";
    }

    public static void setTemperatureUnit(Context context, String temperatureUnit) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        prefs.putString("temperature_unit", temperatureUnit);
        prefs.apply();
    }

    public static String getWindSpeedUnit(Context context, double ms) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String speed = prefs.getString("speed_unit", "");
        switch (speed) {
            case "km/h":
                return UnitConverters.convertToKmph(ms) + "km/h";
            case "mph":
                return UnitConverters.convertToMph(ms) + "mph";
            case "knots":
                return UnitConverters.convertToKnots(ms) + "knots";
            default:
                return ms + "m/s";
        }

    }

    public static void setWindSpeedUnit(Context context, String speedUnit) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        switch (speedUnit) {
            case "km/h":
                prefs.putString("speed_unit", "km/h");
                break;
            case "mph":
                prefs.putString("speed_unit", "mph");
                break;
            case "knots":
                prefs.putString("speed_unit", "knots");
                break;
            default:
                prefs.putString("speed_unit", "m/s");
                break;
        }
        prefs.apply();
    }

    public static boolean is24HourFormat(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getBoolean("time_format",DateFormat.is24HourFormat(context));
    }

    public static void setTimeFormat(Context context, String is24HourFormat) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        switch (is24HourFormat) {
            case "12h":
                prefs.putBoolean("time_format", false);
                break;
            case "24h":
                prefs.putBoolean("time_format", true);
                break;
            default:
                prefs.putBoolean("time_format", DateFormat.is24HourFormat(context));
                break;
        }
        prefs.apply();
    }

    public static boolean getDroneWaterproof(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getBoolean("drone_waterproof", false);
    }

    public static void setDroneWaterproof(Context context, boolean isWaterproof) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        prefs.putBoolean("drone_waterproof", isWaterproof);
        prefs.apply();
    }

    public static int getMaxSpeed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getInt("max_speed", 15);
    }

    public static void setMaxSpeed(Context context, int windSpeed) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        prefs.putInt("max_speed", windSpeed);
        prefs.apply();
    }

    public static void setGPSpref(Context context, boolean value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        prefs.putBoolean("gps_pref", value);
        prefs.apply();
    }

    public static boolean getGPSpref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getBoolean("gps_pref", false);
    }

}
