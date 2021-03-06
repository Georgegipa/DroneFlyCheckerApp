package Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.flychecker.R;

import java.util.Locale;

import Models.Status;

//Helper functions to avoid code repetition
public abstract class Helpers {

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


    //check if unixtime is in the future
    public static boolean isFuture(long unixtime) {
        return unixtime > System.currentTimeMillis() / 1000;
    }

    //check if unixtime is within the last hour
    public static boolean isLastHour(long unixtime) {
        return unixtime > System.currentTimeMillis() / 1000 - 3600 && unixtime < System.currentTimeMillis() / 1000;
    }

    //convert unix timestamp to time with date (time is always with 00 minutes)
    public static String convertUnixToDate(Context context, long unixTime) {
        if(isLastHour(unixTime))
            return context.getString(R.string.now);
        java.util.Date date = new java.util.Date(unixTime * 1000L);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        String formattedDate = sdf.format(date).split(" ")[0];
        String time = sdf.format(date).split(" ")[1];
        if (!PreferencesHelpers.is24HourFormat(context)) {
            formattedDate += " " + convertTo12h(context, time);
        } else
            formattedDate += " " + time;
        return formattedDate;
    }

    //convert unix timestamp to time (minutes are always accurate)
    public static String convertUnixToTime(Context context, long unixTime) {
        //convert unix timestamp to time
        java.util.Date date = new java.util.Date(unixTime);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        if(!PreferencesHelpers.is24HourFormat(context))
            return convertTo12h(context,formattedDate);
        else
            return formattedDate;
    }

    //convert a 24hr time to 12hr time (12:00 -> 12:00 PM)
    private static String convertTo12h(Context context, String time) {
        String[] timeSplit = time.split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        String amPm = "";
        if (hour > 12) {
            hour -= 12;
            amPm = context.getString(R.string.pm);
        } else if (hour == 12) {
            amPm = context.getString(R.string.pm);
        } else if (hour == 0) {
            hour = 12;
            amPm = context.getString(R.string.am);
        } else {
            amPm = context.getString(R.string.am);
        }
        return hour + ":" + timeSplit[1] + " " + amPm;
    }


    //change app language
    public static void setLocale(Context context) {
        Locale locale = new Locale(PreferencesHelpers.getLanguage(context));
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
