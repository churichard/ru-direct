package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;


public class BusMapFragment extends Fragment {

    private BusStopsActivity busStopsActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        busStopsActivity = (BusStopsActivity) getActivity();
        return inflater.inflate(R.layout.fragment_bus_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            busStopsActivity.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}