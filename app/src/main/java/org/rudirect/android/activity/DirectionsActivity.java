package org.rudirect.android.activity;

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

import com.google.android.gms.analytics.HitBuilders;

import org.jgrapht.GraphPath;
import org.rudirect.android.R;
import org.rudirect.android.adapter.DirectionsAdapter;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.data.constants.AppData;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.data.model.BusRouteEdge;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.util.DirectionsUtil;
import org.rudirect.android.util.RUDirectUtil;

import java.util.ArrayList;

public class DirectionsActivity extends AppCompatActivity {

    private static final String TAG = DirectionsActivity.class.getSimpleName();
    private RecyclerView directionsRecyclerView;
    private TextView pathTimeTextView;
    private ProgressBar progressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        progressSpinner = (ProgressBar) findViewById(R.id.routes_progress_spinner);
        pathTimeTextView = (TextView) findViewById(R.id.path_time_textview);

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

        // Setup RecyclerView
        setupRecyclerView();

        // Check to see if the origin is equal to the destination
        if (origin.getTitle().equals(destination.getTitle())) {
            pathTimeTextView.setText(getString(R.string.same_origin_dest_error));
        } else {
            progressSpinner.setVisibility(View.VISIBLE);
            new GetDirections().execute(origin, destination);
        }

        // Log the screen
        RUDirectApplication.getTracker().setScreenName(getString(R.string.directions_screen));
        RUDirectApplication.getTracker().send(new HitBuilders.ScreenViewBuilder()
                .setCustomDimension(AppData.ORIGIN_DIMEN, origin.toString())
                .setCustomDimension(AppData.DESTINATION_DIMEN, destination.toString()).build());
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
    private GraphPath<BusStop, BusRouteEdge> computeShortestPath(BusStop origin, BusStop destination) {
        // Calculate the shortest path between the origin and the destination
        try {
            return DirectionsUtil.calculateShortestPath(origin, destination);
        } catch (IllegalArgumentException e) {
            // Log.e(TAG, e.toString(), e);
            Log.e(TAG, "There isn't a path from " + origin.toString() + " to " + destination.toString()
                    + " right now!");
            return null;
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

    private class GetDirections extends AsyncTask<BusStop, Void, ArrayList<BusRoute>> {

        private BusStop origin;
        private BusStop destination;
        private GraphPath<BusStop, BusRouteEdge> path;

        protected ArrayList<BusRoute> doInBackground(BusStop... stops) {
            origin = stops[0];
            destination = stops[1];

            if (RUDirectApplication.getBusData().getBusTagsToBusRoutes() == null) {
                NextBusAPI.saveBusRoutes();
            }
            ArrayList<BusRoute> activeRoutes = NextBusAPI.getActiveRoutes();

            // Check that there is Internet connection and active routes is not null
            if (activeRoutes == null) return null;

            for (BusRoute route : activeRoutes) {
                NextBusAPI.saveBusStopTimes(route);
            }

            // Build the bus stops graph and compute the shortest path
            DirectionsUtil.setupBusStopsGraph();
            path = computeShortestPath(origin, destination);

            return activeRoutes;
        }

        protected void onPostExecute(ArrayList<BusRoute> activeRoutes) {
            if (activeRoutes == null) {
                View layout = findViewById(R.id.directions_activity_layout);
                if (RUDirectUtil.isNetworkAvailable()) {
                    String error = getString(R.string.no_active_buses_error);
                    pathTimeTextView.setText(error);
                    if (layout != null) Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                } else {
                    String error = getString(R.string.no_internet_error);
                    pathTimeTextView.setText(error);
                    if (layout != null) Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                }
            } else {
                if (path != null) {
                    // Display path and total time
                    directionsRecyclerView.setAdapter(new DirectionsAdapter(DirectionsActivity.this, path));
                    String text = String.format(getString(R.string.total_time_text), (int) DirectionsUtil.getTotalPathTime());
                    pathTimeTextView.setText(text);
                } else {
                    // No path available
                    String text = String.format(getString(R.string.no_path_error), origin.toString(), destination.toString());
                    pathTimeTextView.setText(text);
                }
            }
            progressSpinner.setVisibility(View.GONE);
        }
    }
}