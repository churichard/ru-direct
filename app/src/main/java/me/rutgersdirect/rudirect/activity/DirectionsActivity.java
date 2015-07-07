package me.rutgersdirect.rudirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;

public class DirectionsActivity extends AppCompatActivity {

    private String origin;
    private String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Intent intent = getIntent();
        origin = intent.getStringExtra(getString(R.string.intent_origin_text));
        destination = intent.getStringExtra(getString(R.string.intent_destination_text));

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_toolbar_back, getTheme()));
        }

        TextView originTextView = (TextView) findViewById(R.id.origin_textview);
        TextView destinationTextView = (TextView) findViewById(R.id.destination_textview);
        originTextView.setText(origin);
        destinationTextView.setText(destination);
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