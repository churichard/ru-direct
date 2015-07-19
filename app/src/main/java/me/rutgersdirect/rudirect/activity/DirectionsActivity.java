package me.rutgersdirect.rudirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class DirectionsActivity extends AppCompatActivity {

    private BusStop origin;
    private BusStop destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Get origin and destination
        Intent intent = getIntent();
        origin = intent.getParcelableExtra(getString(R.string.origin_text_message));
        destination = intent.getParcelableExtra(getString(R.string.destination_text_message));

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_action_toolbar_back, getTheme()));
        }

        // Setup origin and destination textviews
        TextView originTextView = (TextView) findViewById(R.id.origin_textview);
        TextView destinationTextView = (TextView) findViewById(R.id.destination_textview);
        originTextView.setText(origin.getTitle());
        destinationTextView.setText(destination.getTitle());
    }
}