package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.DirectionsActivity;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.interfaces.UpdateBusStopsListener;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class DirectionsFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, UpdateBusStopsListener {

    private MainActivity mainActivity;
    private Spinner originSpinner;
    private Spinner destSpinner;
    private BusStop origin;
    private BusStop destination;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSpinners();
    }

    public void setupSpinners() {
        originSpinner = (Spinner) mainActivity.findViewById(R.id.origin_spinner);
        destSpinner = (Spinner) mainActivity.findViewById(R.id.destination_spinner);

        HashMap<String, BusStop[]> busTagsToBusStops = RUDirectApplication.getBusData().getBusTagToBusStops();

        if (busTagsToBusStops != null) {
            // Create list of bus stops
            TreeSet<BusStop> busStops = new TreeSet<>();
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
                    new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, busStopArray);
            originSpinner.setAdapter(adapter);
            originSpinner.setOnItemSelectedListener(this);
            destSpinner.setAdapter(adapter);
            destSpinner.setOnItemSelectedListener(this);
        }

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
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == originSpinner) {
            origin = (BusStop) parent.getItemAtPosition(position);
        } else if (parent == destSpinner){
            destination = (BusStop) parent.getItemAtPosition(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* Do nothing */ }

    @Override
    public void onBusStopsUpdate() {
        setupSpinners();
    }
}