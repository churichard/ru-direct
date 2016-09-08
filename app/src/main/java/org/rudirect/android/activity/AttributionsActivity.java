package org.rudirect.android.activity;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.GoogleApiAvailability;

import org.rudirect.android.R;
import org.rudirect.android.data.constants.RUDirectApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AttributionsActivity extends AppCompatActivity {

    private static final String TAG = AttributionsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributions);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_action_toolbar_back, getTheme()));
        }

        // Build attributions text and set it to the textview text
        TextView attrTextView = (TextView) findViewById(R.id.attributions_textview);
        if (attrTextView != null) attrTextView.setText(getAttributionsMessage());

        // Log the screen
        RUDirectApplication.getTracker().setScreenName(getString(R.string.attributions_screen));
        RUDirectApplication.getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    // Builds the attributions message and returns it
    private SpannableStringBuilder getAttributionsMessage() {
        try {
            String googlePlayLicense = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this);
            return new SpannableStringBuilder(Html.fromHtml("<h4>Google Play Services</h4>"))
                    .append(googlePlayLicense).append("\n")
                    .append(Html.fromHtml(convertStreamToString(getAssets().open("open_source_software.html"))));
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
