package me.rutgersdirect.rudirect.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private SettingsActivity settingsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsActivity = (SettingsActivity) getActivity();

        addPreferencesFromResource(R.xml.preferences);
        setupAttributions();
    }

    // Setup open source software license text
    private void setupAttributions() {
        Preference openSourcePref = findPreference(getString(R.string.open_source_software_key));
        openSourcePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.pref_open_source_software_title)
                            .setMessage(new SpannableStringBuilder(Html.fromHtml("<h5>Google Maps</h5>"))
                                    .append(GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(settingsActivity))
                                    .append("\n").append(Html.fromHtml(convertStreamToString(
                                            settingsActivity.getAssets().open("open_source_software.html")))))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) { /* Do nothing */ }
                            }).create().show();
                    return true;
                } catch (IOException e) {
                    Log.e(TAG, e.toString(), e);
                    return false;
                }
            }
        });
    }

    // Convert input stream to string
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}