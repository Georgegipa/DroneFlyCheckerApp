package com.example.flychecker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import java.util.Objects;

import Helpers.*;

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
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //find the preference
        Preference preference = findPreference(key);
        Log.d(TAG, "onSharedPreferenceChanged: " + key);
        if (preference instanceof ListPreference) {
            //if it is listpreference cast preference to ListPreference
            ListPreference listPreference = (ListPreference) preference;
            switch (key) {
                case "pref_key_theme":
                    String theme = listPreference.getValue();
                    Helpers.setNewTheme(this.requireActivity(), theme);
                    break;
                case "pref_key_language":
                    String language = listPreference.getValue();
                    this.requireActivity().recreate();//recreate the activity to change the language

                    PreferencesHelpers.setLanguage(this.requireActivity(), language);
                    Helpers.setLocale(this.getActivity());
                    break;
                case "pref_key_wind_unit":
                    String windUnit = listPreference.getValue();
                    PreferencesHelpers.setWindSpeedUnit(this.requireActivity(), windUnit);
                    break;
                case "pref_key_temperature_unit":
                    String tempUnit = listPreference.getValue();
                    PreferencesHelpers.setTemperatureUnit(this.requireActivity(), tempUnit);
                    break;
                case "pref_key_hour_format":
                    String hourFormat = listPreference.getValue();
                    PreferencesHelpers.setTimeFormat(this.requireActivity(), hourFormat);
                    break;
            }

        } else if (preference instanceof SwitchPreference) {
            if (key.equals("pref_key_waterproof")) {
                boolean waterproof = sharedPreferences.getBoolean("pref_key_waterproof", false);
                PreferencesHelpers.setDroneWaterproof(this.requireActivity(), waterproof);
            }
        } else if (preference instanceof SeekBarPreference) {
            if (key.equals("pref_key_max_speed")) {
                int windSpeed = sharedPreferences.getInt("pref_key_max_speed", 15);
                if(windSpeed > 3) {
                    PreferencesHelpers.setMaxSpeed(this.requireActivity(), windSpeed);
                }
                else {
                    sharedPreferences.edit().putInt("pref_key_max_speed", 3).apply();
                    Toast.makeText(this.requireActivity(), R.string.speed_to_low, Toast.LENGTH_SHORT).show();
                    PreferencesHelpers.setMaxSpeed(this.requireActivity(), 3);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
    }
}
