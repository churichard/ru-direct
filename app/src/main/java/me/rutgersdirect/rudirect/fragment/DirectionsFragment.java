package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class DirectionsFragment extends Fragment {

    private static final String TAG = DirectionsFragment.class.getSimpleName();
    private MainActivity mainActivity;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setupAutoCompleteTextViews() {
        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView originTextView = (AutoCompleteTextView) mainActivity.findViewById(R.id.origin_textview);
        AutoCompleteTextView destTextView = (AutoCompleteTextView) mainActivity.findViewById(R.id.destination_textview);

        // Get the string array
        SharedPreferences busTagsToStopTitlesPref = mainActivity.getSharedPreferences(
                mainActivity.getString(R.string.bus_tags_to_stop_titles_key), Context.MODE_PRIVATE);
        Map<String, ?> busTagsToStopTitlesMap = busTagsToStopTitlesPref.getAll();

        if (busTagsToStopTitlesMap.size() != 0) {
            // Create list of bus stops
            LinkedHashSet<String> busStops = new LinkedHashSet<>();
            String[] busTags = getBusTags();
            for (String busTag : busTags) {
                String[] stops = RUDirectUtil.loadArray(busTagsToStopTitlesPref, busTag);
                Collections.addAll(busStops, stops);
            }

            // Create the adapter and set it to the AutoCompleteTextViews
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, busStops.toArray(new String[busStops.size()]));
            originTextView.setAdapter(adapter);
            destTextView.setAdapter(adapter);
        }
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
}