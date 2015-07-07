package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.DirectionsActivity;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.interfaces.UpdateBusStopsListener;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class DirectionsFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, UpdateBusStopsListener {

    private static final String TAG = DirectionsFragment.class.getSimpleName();
    private MainActivity mainActivity;

    private Spinner originSpinner;
    private Spinner destSpinner;
    private String origin;
    private String destination;

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
        setupAutoCompleteTextViews();
    }

    public void setupAutoCompleteTextViews() {
        originSpinner = (Spinner) mainActivity.findViewById(R.id.origin_spinner);
        destSpinner = (Spinner) mainActivity.findViewById(R.id.destination_spinner);

        // Get the string array
        SharedPreferences busTagsToStopTitlesPref = mainActivity.getSharedPreferences(
                mainActivity.getString(R.string.bus_tags_to_stop_titles_key), Context.MODE_PRIVATE);
        Map<String, ?> busTagsToStopTitlesMap = busTagsToStopTitlesPref.getAll();

        if (busTagsToStopTitlesMap.size() != 0) {
            // Create list of bus stops
            TreeSet<String> busStops = new TreeSet<>();
            String[] busTags = getBusTags();
            for (String busTag : busTags) {
                Collections.addAll(busStops, RUDirectUtil.loadArray(busTagsToStopTitlesPref, busTag));
            }
            String[] busStopArray = busStops.toArray(new String[busStops.size()]);

            // Initialize origin and destination
            origin = busStopArray[0];
            destination = busStopArray[0];

            // Create the adapter and set it to the AutoCompleteTextViews
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, busStopArray);
            originSpinner.setAdapter(adapter);
            originSpinner.setOnItemSelectedListener(this);
            destSpinner.setAdapter(adapter);
            destSpinner.setOnItemSelectedListener(this);
        }

        Button findRouteButton = (Button) mainActivity.findViewById(R.id.find_route_button);
        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (origin != null && destination != null) {
                    Intent intent = new Intent(mainActivity, DirectionsActivity.class);
                    intent.putExtra(getString(R.string.intent_origin_text), origin);
                    intent.putExtra(getString(R.string.intent_destination_text), destination);
                    startActivity(intent);
                }
            }
        });
    }

    // Returns an array of bus route tags
    private String[] getBusTags() {
        Map<String, ?> tagsToBusesMap = mainActivity.getSharedPreferences(
                getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE).getAll();
        Object[] busNamesObj = tagsToBusesMap.keySet().toArray();
        String[] busNames = Arrays.copyOf(busNamesObj, busNamesObj.length, String[].class);
        Arrays.sort(busNames);
        return busNames;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == originSpinner) {
            origin = (String) parent.getItemAtPosition(position);
        } else {
            destination = (String) parent.getItemAtPosition(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* Do nothing */ }

    @Override
    public void onBusStopsUpdate() {
        setupAutoCompleteTextViews();
    }
}