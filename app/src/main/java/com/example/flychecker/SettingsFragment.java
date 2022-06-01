package com.example.flychecker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
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
                    PreferencesHelpers.setLocale(this.getActivity(), "en");
                } else if (language.equals("el")) {
                    //change language to greek
                    PreferencesHelpers.setLocale(this.getActivity(), "el");
                }
            } else if (key.equals("pref_key_wind_unit")) {
                String windUnit = listPreference.getValue();

                PreferencesHelpers.setWindSpeedUnit(this.getActivity(), windUnit);

            } else if (key.equals("pref_key_temperature_unit")) {
                String tempUnit = listPreference.getValue();
                PreferencesHelpers.setTemperatureUnit(this.getActivity(), tempUnit);
            } else if (key.equals("pref_key_hour_format")) {
                String hourFormat = listPreference.getValue();
                PreferencesHelpers.setTimeFormat(this.getActivity(), hourFormat);
            }

        } else if (preference instanceof SwitchPreference) {
            if (key.equals("pref_key_waterproof")) {
                boolean waterproof = sharedPreferences.getBoolean("pref_key_waterproof", false);
                PreferencesHelpers.setDroneWaterproof(this.getActivity(), waterproof);
            }
        } else if (preference instanceof SeekBarPreference) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
