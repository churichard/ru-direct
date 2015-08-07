package me.rutgersdirect.rudirect.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jgrapht.GraphPath;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.DirectionsAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.util.DirectionsUtil;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class DirectionsActivity extends AppCompatActivity {

    private static final String TAG = DirectionsActivity.class.getSimpleName();
    private RecyclerView directionsRecyclerView;
    private ProgressBar progressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        progressSpinner = (ProgressBar) findViewById(R.id.progress_spinner);

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

        // Setup recyclerview
        setupRecyclerView();

        // Setup origin and destination textviews
        TextView originTextView = (TextView) findViewById(R.id.origin_textview);
        TextView destinationTextView = (TextView) findViewById(R.id.destination_textview);
        originTextView.setText("Origin: " + origin.getTitle());
        destinationTextView.setText("Destination: " + destination.getTitle());

        // Compute the shortest path
        // Check to see if the origin is equal to the destination
        if (origin.getTitle().equals(destination.getTitle())) {
            directionsRecyclerView.setAdapter(new DirectionsAdapter(new String[]{"You're already at your destination!"}, null));
        } else {
            progressSpinner.setVisibility(View.VISIBLE);
            new GetDirections().execute(origin, destination);
        }
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        // Initialize recycler view
        directionsRecyclerView = (RecyclerView) findViewById(R.id.directions_recyclerview);
        // Set layout manager
        directionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Set adapter
        directionsRecyclerView.setAdapter(new DirectionsAdapter());
    }

    // Computes the shortest path
    private void computeShortestPath(BusStop origin, BusStop destination) {
        // Calculate the shortest path between the origin and the destination
        try {
            GraphPath<BusStop, BusRouteEdge> shortestPath = DirectionsUtil.calculateShortestPath(origin, destination);
            if (shortestPath != null) {
                // Display path and total time
                directionsRecyclerView.setAdapter(new DirectionsAdapter(shortestPath));
                ((TextView) findViewById(R.id.path_time_textview))
                        .setText("Total time: " + DirectionsUtil.getPathTime(shortestPath) + " minutes");
            } else {
                // No path available
                directionsRecyclerView.setAdapter(new DirectionsAdapter(new String[]{"There isn't a path from " + origin.toString() + " to "
                        + destination.toString() + " right now!"}, null));
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString(), e);
            directionsRecyclerView.setAdapter(new DirectionsAdapter(new String[]{"There isn't a path from " + origin.toString() + " to "
                    + destination.toString() + " right now!"}, null));
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

    private class GetDirections extends AsyncTask<BusStop, Void, String[]> {

        private BusStop origin;
        private BusStop destination;

        protected String[] doInBackground(BusStop... stops) {
            origin = stops[0];
            destination = stops[1];

            if (RUDirectApplication.getBusData().getBusTagToBusTitle() == null) {
                NextBusAPI.saveBusStops();
            }
            String[] activeBusTags = NextBusAPI.getActiveBusTags();
            if (RUDirectApplication.getBusData().getBusTagToBusStops() != null) {
                activeBusTags = NextBusAPI.getActiveBusTags();
                for (String busTag : activeBusTags) {
                    NextBusAPI.saveBusStopTimes(busTag);
                }
            }
            return activeBusTags;
        }

        protected void onPostExecute(String[] activeBusTags) {
            if (activeBusTags.length == 1 && activeBusTags[0] == null) {
                if (RUDirectUtil.isNetworkAvailable()) {
                    Snackbar.make(findViewById(R.id.directions_activity_layout),
                            "There are no active buses right now!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(R.id.directions_activity_layout),
                            "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
                }
            } else {
                // Build the bus stops graph and compute the shortest path
                DirectionsUtil.setupBusStopsGraph();
                computeShortestPath(origin, destination);
            }
            progressSpinner.setVisibility(View.GONE);
        }
    }
}