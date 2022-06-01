package com.example.flychecker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import android.text.format.DateFormat;

import java.util.Locale;

//Helper functions to avoid code repetition
public abstract class Helpers {

    //convert unix timestamp to time with date
    public static String convertUnixToDate(int unixTime) {
        java.util.Date date = new java.util.Date(unixTime * 1000L);
        //change hour to 12 hour format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getDefault());

        //concat amPm to the end
        String formatted = sdf.format(date);
        //TODO: get amPm from the locale and system

        return formatted;
    }

    //change app language
    public static void setLocale(Activity activity, String languageCode) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", languageCode);
        editor.apply();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static int statusToIcon(Status status) {
        switch (status) {
            case CAUTION:
                return R.drawable.ic_baseline_warning_24;
            case DANGER:
                return R.drawable.ic_baseline_close_24;
            default:
                return R.drawable.ic_baseline_check_24;
        }
    }

    public static int statusToString(Status status) {
        switch (status) {
            case CAUTION:
                return R.string.caution;
            case DANGER:
                return R.string.danger;
            default:
                return R.string.safe;
        }

    }

    public static int statusToColor(Status status) {
        switch (status) {
            case CAUTION:
                return R.color.yellow;
            case DANGER:
                return R.color.red;
            default:
                return R.color.green;
        }
    }

    //get app language
    public static String getLanguage(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getString("language", "");
    }

    //change the theme
    public static void setNewTheme(Activity activity, String theme) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString("theme", theme);
        editor.apply();
        setTheme(theme);
    }

    //retrieve the last applied theme
    public static void setPrevTheme(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("theme", "");
        setTheme(theme);
    }

    //changes the theme between light and dark and auto
    private static void setTheme(String theme) {
        if (theme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    //get the system time format
    public static String getTimeFormat(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (prefs.contains("time_format")) {
            return prefs.getString("time_format", "");
        } else {
            return DateFormat.is24HourFormat(activity) ? "24" : "12";
        }
    }

    public static void setTimeFormat(Activity activity, String timeFormat) {
        SharedPreferences.Editor prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        switch (timeFormat) {
            case "12h":
                prefs.putString("time_format", "12h");
                break;
            case "24h":
                prefs.putString("time_format", "24h");
                break;
            default:
                prefs.putString("time_format", DateFormat.is24HourFormat(activity) ? "24h" : "12h");
                break;
        }
        prefs.apply();
    }
    
    public static String getTemperatureUnit(Activity activity, double ms) {
        SharedPreferences prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
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


    public static void setTemperatureUnit(Activity activity, String temperatureUnit) {
        SharedPreferences.Editor prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        prefs.putString("temperature_unit", temperatureUnit);
        prefs.apply();
    }

    public static String getWindSpeedUnit(Activity activity, double ms) {
        SharedPreferences prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
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

    public static void setWindSpeedUnit(Activity activity, String speedUnit) {
        SharedPreferences.Editor prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
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
}
