package me.rutgersdirect.rudirect.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {

    private SettingsActivity settingsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsActivity = (SettingsActivity) getActivity();

        addPreferencesFromResource(R.xml.preferences);
        Preference mapsPref = findPreference(getString(R.string.google_maps_key));
        mapsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // Open dialog box with google maps attribution
                return true;
            }
        });
//        pref.setSummary(GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(settingsActivity));
    }
}