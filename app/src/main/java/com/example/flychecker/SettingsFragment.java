package com.example.flychecker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference listTheme, listLanguage, listWindUnit, listTempUnit;

    private static final String TAG = SettingsFragment.class.getSimpleName();
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//
//        getPreferenceScreen().getSharedPreferences()
//                .unregisterOnSharedPreferenceChangeListener(this);
//    }
//
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Toast.makeText(getContext(), key, Toast.LENGTH_SHORT).show();
//        Log.d(TAG,key);
//    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //find the preference
        Preference preference = findPreference(key);
        Log.d(TAG, "onSharedPreferenceChanged: " + key);
        if (preference instanceof ListPreference) {
            //if it is listpreference cast preference to ListPreference
            ListPreference listPreference = (ListPreference) preference;
            if(key.equals("pref_theme")){
                String theme = listPreference.getValue();
                if(theme.equals("light")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else if(theme.equals("dark")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            else if(key.equals("pref_language")){
                String language = listPreference.getValue();
                if(language.equals("en"))
                {//TODO: change the language support
                    //getActivity().getResources().updateConfiguration(new Configuration(getResources().getConfiguration()), getResources().getDisplayMetrics());
                }else if(language.equals("gr")){
                    //getActivity().getResources().updateConfiguration(new Configuration(getResources().getConfiguration()), getResources().getDisplayMetrics());
                }
            }
            else if(key.equals("pref_wind_unit")){
                String windUnit = listPreference.getValue();
                //TODO: change the wind unit support

            }
            else if(key.equals("pref_temp_unit")){
                String tempUnit = listPreference.getValue();
                //TODO: change the temperature unit  support
            }

        }
        else if (preference instanceof SwitchPreference) {
            Log.d(TAG, "onSharedPreferenceChanged: " + key);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void test() {
        //toast message
        Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();

    }

}
