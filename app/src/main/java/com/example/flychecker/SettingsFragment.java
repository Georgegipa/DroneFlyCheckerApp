package com.example.flychecker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }


    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //find the preference
        Preference preference = findPreference(key);
        Log.d(TAG, "onSharedPreferenceChanged: " + key);
        if (preference instanceof ListPreference) {
            //if it is listpreference cast preference to ListPreference
            ListPreference listPreference = (ListPreference) preference;
            if (key.equals("pref_key_theme")) {
                String theme = listPreference.getValue();
                Helpers.setNewTheme(this.getActivity(), theme);
            } else if (key.equals("pref_key_language")) {
                String language = listPreference.getValue();
                this.getActivity().recreate();//recreate the activity to change the language
                if (language.equals("en")) {
                    //change language to english
                    Helpers.setLocale(this.getActivity(), "en");
                } else if (language.equals("el")) {
                    //change language to greek
                    Helpers.setLocale(this.getActivity(), "el");
                }
            } else if (key.equals("pref_key_wind_unit")) {
                String windUnit = listPreference.getValue();

                Helpers.setWindSpeedUnit(this.getActivity(), windUnit);

            } else if (key.equals("pref_key_temperature_unit")) {
                String tempUnit = listPreference.getValue();
                Helpers.setTemperatureUnit(this.getActivity(), tempUnit);
            }
            else if(key.equals("pref_key_hour_format")){
                String hourFormat = listPreference.getValue();
                Helpers.setTimeFormat(this.getActivity(), hourFormat);
            }

        } else if (preference instanceof SwitchPreference) {
            Log.d(TAG, "onSharedPreferenceChanged: " + key);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
