package com.brianroper.popularmovies;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
    }
    private void bindPreferenceSummaryToValue(Preference pref){

        pref.setOnPreferenceChangeListener(this);

        onPreferenceChange(pref, PreferenceManager
                .getDefaultSharedPreferences(pref.getContext())
                .getString(pref.getKey(), ""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String stringValue = newValue.toString();

        if(preference instanceof ListPreference){

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if(prefIndex >= 0){

                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
            else{

                preference.setSummary(stringValue);
            }
        }
        return true;
    }
}
