package org.rudirect.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
        setupAbout();
    }

    // Set up the Contributors section
    private void setupContributors() {
        // Set up team info
        Preference teamPref = findPreference(getString(R.string.team_key));
        teamPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity);
                builder.setTitle(R.string.pref_team_title)
                        .setMessage(R.string.team_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { /* Do nothing */ }
                        }).create().show();
                return true;
            }
        });

        // Set up contributors info
        Preference contributorsPref = findPreference(getString(R.string.contributors_key));
        contributorsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity);
                builder.setTitle(R.string.pref_other_contributors_title)
                        .setMessage(R.string.contributors_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { /* Do nothing */ }
                        }).create().show();
                return true;
            }
        });

        // Set up special thanks info
        Preference specialThanksPref = findPreference(getString(R.string.special_thanks_key));
        specialThanksPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity);
                builder.setTitle(R.string.pref_special_thanks_title)
                        .setMessage(R.string.special_thanks_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { /* Do nothing */ }
                        }).create().show();
                return true;
            }
        });
    }

    // Set up the About section
    private void setupAbout() {
        // Set up version info
        Preference versionPref = findPreference(getString(R.string.version_key));
        versionPref.setTitle("RU Direct v" + BuildConfig.VERSION_NAME);

        // Set up open source info
        Preference openSourcePref = findPreference(getString(R.string.open_source_key));
        openSourcePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/churichard/ru-direct"));
                startActivity(browserIntent);
                return true;
            }
        });

        // Set up attributions
        Preference attributionsPref = findPreference(getString(R.string.attributions_key));
        attributionsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(settingsActivity, AttributionsActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
}