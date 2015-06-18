package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.adapter.BusRouteAdapter;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;


public class AllRoutesFragment extends Fragment {

    private MainActivity mainActivity;
    public static RecyclerView allBusesRecyclerView;

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
        AllRoutesFragment.allBusesRecyclerView.setAdapter(new BusRouteAdapter(mainActivity.getBusRoutes(), mainActivity, this));
    }
}