package org.rudirect.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.rudirect.android.BuildConfig;
import org.rudirect.android.R;
import org.rudirect.android.activity.AttributionsActivity;
import org.rudirect.android.activity.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {

    private SettingsActivity settingsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsActivity = (SettingsActivity) getActivity();

        addPreferencesFromResource(R.xml.preferences);
        setupContributors();
        setupVersion();
        setupAttributions();
    }

    // Setup contributors
    private void setupContributors() {
        Preference contributorsPref = findPreference(getString(R.string.contributors_key));
        contributorsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.pref_contributors_title)
                        .setMessage(R.string.contributors_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { /* Do nothing */ }
                        }).create().show();
                return true;
            }
        });

        Preference specialThanksPref = findPreference(getString(R.string.special_thanks_key));
        specialThanksPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.pref_special_thanks_title)
                        .setMessage(R.string.special_thanks_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { /* Do nothing */ }
                        }).create().show();
                return true;
            }
        });
    }

    // Setup version information
    private void setupVersion() {
        Preference versionPref = findPreference(getString(R.string.version_key));
        versionPref.setTitle("RU Direct v" + BuildConfig.VERSION_NAME);
    }

    // Setup open source software license text
    private void setupAttributions() {
        Preference openSourcePref = findPreference(getString(R.string.open_source_software_key));
        openSourcePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(settingsActivity, AttributionsActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
}