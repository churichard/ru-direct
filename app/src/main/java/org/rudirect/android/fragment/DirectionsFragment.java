package org.rudirect.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

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
    private RelativeLayout relativeLayout;
    private AutoCompleteTextView originACTextView;
    private AutoCompleteTextView destACTextView;

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

        // Hide the keyboard when the textviews are not in focus
        relativeLayout = (RelativeLayout) mainActivity.findViewById(R.id.directions_relative_layout);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RUDirectUtil.hideKeyboard(mainActivity.getCurrentFocus());
                return true;
            }
        });

        // Set up find route button
        Button findRouteButton = (Button) mainActivity.findViewById(R.id.find_route_button);
        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RUDirectUtil.hideKeyboard(view);
                BusStop origin = null;
                BusStop destination = null;

                BusStop[] busStops = RUDirectApplication.getBusData().getBusStops();
                for (BusStop stop : busStops) {
                    if (stop.getTitle().equalsIgnoreCase(originACTextView.getText().toString())) {
                        origin = stop;
                    }
                    if (stop.getTitle().equalsIgnoreCase(destACTextView.getText().toString())) {
                        destination = stop;
                    }
                    if (origin != null && destination != null) break;
                }

                if (origin == null) {
                    originACTextView.setError(getString(R.string.directions_textview_error));
                }
                if (destination == null) {
                    destACTextView.setError(getString(R.string.directions_textview_error));
                }

                if (origin != null && destination != null) {
                    Intent intent = new Intent(mainActivity, DirectionsActivity.class);
                    intent.putExtra(getString(R.string.origin_text_message), (Parcelable) origin);
                    intent.putExtra(getString(R.string.destination_text_message), (Parcelable) destination);
                    startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
                } else {
                    relativeLayout.requestFocus();
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(" ");
                    builder.setSpan(new ImageSpan(mainActivity, android.R.drawable.stat_notify_error), 0, 1, 0);
                    builder.append("\t\t").append(getString(R.string.directions_snackbar_error));
                    Snackbar.make(relativeLayout, builder, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        initACTextViews();
    }

    // Initialize the autocomplete textviews
    private void initACTextViews() {
        HashMap<String, BusStop[]> busTagsToBusStops = RUDirectApplication.getBusData().getBusTagToBusStops();

        if (busTagsToBusStops != null) {
            BusStop[] busStopArray = RUDirectApplication.getBusData().getBusStops();
            ArrayAdapter<BusStop> busStopArrayAdapter = new ArrayAdapter<>(mainActivity, R.layout.list_autocomplete_textview, busStopArray);

            // Setup the autocomplete textviews
            setupACTextView(originACTextView, busStopArrayAdapter);
            setupACTextView(destACTextView, busStopArrayAdapter);

            // Initialize origin and destination
            setOriginToNearestBusStop();
        }
    }

    // Set up the autocomplete textview
    private void setupACTextView(AutoCompleteTextView textView, ArrayAdapter<BusStop> busStopArrayAdapter) {
        textView.setThreshold(1); // Start autocompleting after 1 char is typed
        textView.setAdapter(busStopArrayAdapter); // Set the array adapter

        // Hide the keyboard when an autocomplete option is selected
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RUDirectUtil.hideKeyboard(mainActivity.getCurrentFocus());
            }
        });
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
            originACTextView.setError(null);
        }
    }

    // Sets the origin autocomplete textview to the nearest bus stop
    private void setOriginToNearestBusStop() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            originACTextView.setText(RUDirectUtil.getNearestStop(location).getTitle());
            originACTextView.dismissDropDown();
        }
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