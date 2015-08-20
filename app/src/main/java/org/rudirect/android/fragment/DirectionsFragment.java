package org.rudirect.android.fragment;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.Spinner;

import com.google.android.gms.analytics.HitBuilders;

import org.rudirect.android.R;
import org.rudirect.android.activity.DirectionsActivity;
import org.rudirect.android.activity.MainActivity;
import org.rudirect.android.activity.SettingsActivity;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.interfaces.NetworkCallFinishListener;
import org.rudirect.android.util.RUDirectUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

public class DirectionsFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, NetworkCallFinishListener {

    private MainActivity mainActivity;
    private Spinner originSpinner;
    private Spinner destSpinner;
    private BusStop origin;
    private BusStop destination;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
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
            // Create list of bus stops
            TreeSet<BusStop> busStops = new TreeSet<>(new Comparator<BusStop>() {
                @Override
                public int compare(BusStop stop1, BusStop stop2) {
                    if (stop1 == stop2) {
                        return 0;
                    }
                    return stop1.getTitle().compareTo(stop2.getTitle());
                }
            });
            String[] busTags = RUDirectUtil.mapKeySetToSortedArray(busTagsToBusStops);
            for (String busTag : busTags) {
                busStops.addAll(Arrays.asList(busTagsToBusStops.get(busTag)));
            }
            BusStop[] busStopArray = busStops.toArray(new BusStop[busStops.size()]);

            // Initialize origin and destination
            origin = busStopArray[0];
            destination = busStopArray[0];

            // Create the adapter and set it to the spinners
            ArrayAdapter<BusStop> adapter =
                    new ArrayAdapter<>(mainActivity, R.layout.list_spinner, busStopArray);
            originSpinner.setAdapter(adapter);
            originSpinner.setOnItemSelectedListener(this);
            destSpinner.setAdapter(adapter);
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
}