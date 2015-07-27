package me.rutgersdirect.rudirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.jgrapht.GraphPath;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.util.DirectionsUtil;

public class DirectionsActivity extends AppCompatActivity {

    private static final String TAG = DirectionsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Get origin and destination
        Intent intent = getIntent();
        BusStop origin = intent.getParcelableExtra(getString(R.string.origin_text_message));
        BusStop destination = intent.getParcelableExtra(getString(R.string.destination_text_message));

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
        originTextView.setText("Origin: " + origin.getTitle());
        destinationTextView.setText("Destination: " + destination.getTitle());

        // Compute the shortest path
        computeShortestPath(origin, destination);
    }

    // Computes the shortest path
    private void computeShortestPath(BusStop origin, BusStop destination) {
        TextView result = (TextView) findViewById(R.id.directions_result);

        // Check to see if the origin is equal to the destination
        if (origin.equals(destination)) {
            result.setText("You're already at your destination!");
            return;
        }

        // Build the bus stops graph
        DirectionsUtil.setupBusStopsGraph();

        // Compute the shortest path between the origin and the destination
        try {
            GraphPath<BusStop, BusRouteEdge> shortestPath = DirectionsUtil.calculateShortestPath(origin, destination);
            result.setText("Path: " + shortestPath.toString());
        } catch (IllegalArgumentException e) {
            result.setText("You can't go from " + origin.toString() + " to "
                    + destination.toString() + " by riding buses right now!");
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.abc_shrink_fade_out_from_bottom);
    }
}