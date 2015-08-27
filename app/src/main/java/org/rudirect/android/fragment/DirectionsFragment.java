package org.rudirect.android.fragment;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

public class DirectionsFragment extends Fragment implements NetworkCallFinishListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 5001;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private MainActivity mainActivity;
    private AutoCompleteTextView originACTextView;
    private AutoCompleteTextView destACTextView;
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

        originACTextView = (AutoCompleteTextView) mainActivity.findViewById(R.id.origin_ac_textview);
        destACTextView = (AutoCompleteTextView) mainActivity.findViewById(R.id.dest_ac_textview);

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

        initACTextViews();
    }

    private void initACTextViews() {
        HashMap<String, BusStop[]> busTagsToBusStops = RUDirectApplication.getBusData().getBusTagToBusStops();

        if (busTagsToBusStops != null) {
            BusStop[] busStopArray = RUDirectApplication.getBusData().getBusStops();
            ArrayAdapter<BusStop> busStopArrayAdapter = new ArrayAdapter<>(mainActivity, R.layout.list_autocomplete_textview, busStopArray);

            // Create the busStopArrayAdapter and set it to the autocomplete textviews
            setupACTextView(originACTextView, busStopArrayAdapter, true);
            setupACTextView(destACTextView, busStopArrayAdapter, false);

            // Initialize origin and destination
            boolean originSet = setOriginToNearestBusStop();
            if (!originSet) origin = busStopArray[0];
            destination = busStopArray[0];
        }
    }

    private void setupACTextView(AutoCompleteTextView textView, ArrayAdapter<BusStop> busStopArrayAdapter, boolean isOrigin) {
        textView.setThreshold(1); // Start autocompleting after 1 char is typed
        textView.setAdapter(busStopArrayAdapter); // Set the array adapter

        // Hide the keyboard when the focus is not on the textviews
        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(v);
                }
            }
        });

        // Set the origin/destination when an item is clicked
        AdapterView.OnItemClickListener listener;
        if (isOrigin) {
            listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    origin = (BusStop) parent.getItemAtPosition(position);
                }
            };
        } else {
            listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    destination = (BusStop) parent.getItemAtPosition(position);
                }
            };
        }
        textView.setOnItemClickListener(listener);
    }

    @Override
    public void onBusStopsUpdated() {
        initACTextViews();
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

            setOriginToNearestBusStop();
        }
    }

    private boolean setOriginToNearestBusStop() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            origin = RUDirectUtil.getNearestStop(location);
            originACTextView.setText(origin.getTitle());
            originACTextView.dismissDropDown();
            return true;
        }
        return false;
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    // Build Google Api Client
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