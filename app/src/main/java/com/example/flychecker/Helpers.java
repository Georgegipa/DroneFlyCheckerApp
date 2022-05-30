package com.example.flychecker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public abstract class Helpers {
    //convert unix timestamp to time with date
    public static String convertUnixToDate(int unixTime, boolean return12HourFormat) {
        java.util.Date date = new java.util.Date(unixTime * 1000L);
        //change hour to 12 hour format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getDefault());

        //concat amPm to the end
        String formatted = sdf.format(date);
        if (return12HourFormat) {//TODO: add 12hour format support
            //convert a 24hr format to 12hr format
            int hour = date.getHours();
            String amPm = hour >= 12 ? "PM" : "AM";
            if (hour > 12) {
                hour = hour - 12;
            }
            formatted = formatted.replace("HH", String.valueOf(hour));
            return formatted + " " + amPm;
        } else
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


}
