package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Map;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.adapter.BusRouteAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;

public class AllRoutesFragment extends Fragment {

    private MainActivity mainActivity;
    public static RecyclerView allBusesRecyclerView;

    // Sets up the bus routes
    private class SetupBusRoutesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            NextBusAPI.saveBusStops(mainActivity);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            AllRoutesFragment.allBusesRecyclerView.setAdapter(
                    new BusRouteAdapter(getBusRoutes(), mainActivity, AllRoutesFragment.this));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_all_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        new SetupBusRoutesTask().execute();
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        // Initialize recycler view
        allBusesRecyclerView = (RecyclerView) mainActivity.findViewById(R.id.all_buses_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        allBusesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        allBusesRecyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        AllRoutesFragment.allBusesRecyclerView.setAdapter(new BusRouteAdapter(getBusRoutes(), mainActivity, this));
    }

    // Returns an array of bus route names
    public String[] getBusRoutes() {
        Map<String, ?> busesToTagsMap = mainActivity.getSharedPreferences(
                getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE).getAll();
        Object[] busNamesObj = busesToTagsMap.keySet().toArray();
        String[] busNames = Arrays.copyOf(busNamesObj, busNamesObj.length, String[].class);
        Arrays.sort(busNames);
        return busNames;
    }
}