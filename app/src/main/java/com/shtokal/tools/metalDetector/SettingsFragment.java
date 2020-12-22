package com.shtokal.tools.metalDetector;

import android.os.Bundle;

import com.shtokal.tools.R;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferencesFix(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);
    }

}