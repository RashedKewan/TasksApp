package com.example.tasksapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tasksapp.R;

public class SettingFragment extends PreferenceFragmentCompat {
    private CheckBoxPreference checkBoc;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setPreferencesFromResource(R.xml.preferences, rootKey);
        checkBoc=findPreference("remember");
        checkBoc.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("sortedByTime", (Boolean) newValue);
            editor.apply();
            return true;
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}
