package org.rudirect.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.rudirect.android.R;
import org.rudirect.android.activity.DirectionsActivity;
import org.rudirect.android.activity.MainActivity;
import org.rudirect.android.activity.SettingsActivity;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.interfaces.NetworkCallFinishListener;
import org.rudirect.android.util.RUDirectUtil;

import java.util.HashMap;

public class DirectionsFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, NetworkCallFinishListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 5001;

    private MainActivity mainActivity;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private ArrayAdapter<BusStop> busStopArrayAdapter;
    private Spinner originSpinner;
    private Spinner destSpinner;
    private ImageButton originGeolocateButton;
    private BusStop origin;
    private BusStop destination;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        mGoogleApiClient = buildGoogleApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        originSpinner = (Spinner) mainActivity.findViewById(R.id.origin_spinner);
        destSpinner = (Spinner) mainActivity.findViewById(R.id.destination_spinner);

        originGeolocateButton = (ImageButton) mainActivity.findViewById(R.id.origin_geolocate_button);
        originGeolocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                BusStop nearest = RUDirectUtil.getNearestStop(location);
                if (nearest != null) {
                    origin = nearest;

                    originSpinner.setSelection(busStopArrayAdapter.getPosition(origin));
                }
            }
        });

        // Set up find route button
        Button findRouteButton = (Button) mainActivity.findViewById(R.id.find_route_button);
        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (origin != null && destination != null) {
                    Intent intent = new Intent(mainActivity, DirectionsActivity.class);
                    intent.putExtra(getString(R.string.origin_text_message), (Parcelable) origin);
                    intent.putExtra(getString(R.string.destination_text_message), (Parcelable) destination);
                    startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
                }
            }
        });

        populateSpinners();
    }

    public void populateSpinners() {
        HashMap<String, BusStop[]> busTagsToBusStops = RUDirectApplication.getBusData().getBusTagToBusStops();

        if (busTagsToBusStops != null) {
            BusStop[] busStopArray = RUDirectApplication.getBusData().getBusStops();

            // Initialize origin and destination
            origin = busStopArray[0];
            destination = busStopArray[0];

            // Create the busStopArrayAdapter and set it to the spinners
            busStopArrayAdapter = new ArrayAdapter<>(mainActivity, R.layout.list_spinner, busStopArray);
            originSpinner.setAdapter(busStopArrayAdapter);
            originSpinner.setOnItemSelectedListener(this);
            destSpinner.setAdapter(busStopArrayAdapter);
            destSpinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == originSpinner) {
            origin = (BusStop) parent.getItemAtPosition(position);
        } else if (parent == destSpinner) {
            destination = (BusStop) parent.getItemAtPosition(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* Do nothing */ }

    @Override
    public void onBusStopsUpdated() {
        // Populate the directions spinners
        populateSpinners();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(mainActivity, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            RUDirectApplication.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.directions_selector_category))
                    .setAction(getString(R.string.view_action))
                    .build());
        }
    }

    // Build Google Api Client for displaying maps
    private synchronized GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(mainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) { /* Do nothing */ }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    // Connection to Google Play Services failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError && result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(mainActivity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        }
    }
}