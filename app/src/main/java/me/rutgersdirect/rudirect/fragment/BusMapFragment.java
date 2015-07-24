package me.rutgersdirect.rudirect.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.model.BusPathSegment;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.util.ShowBusPathHelper;

public class BusMapFragment extends MapFragment implements OnMapReadyCallback {

    private static final int ACTIVE_BUS_REFRESH_INTERVAL = 10000;

    private GoogleMap mMap;
    private BusStopsActivity busStopsActivity;
    private Handler refreshHandler;
    private ArrayList<Marker> busStopMarkers;
    private ArrayList<Marker> activeBusMarkers;
    private BusPathSegment[] pathSegments;
    private boolean isVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStopsActivity = (BusStopsActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activeBusMarkers = new ArrayList<>();
        busStopMarkers = new ArrayList<>();
        isVisible = false;
        new ShowBusPathHelper().execute(busStopsActivity.getBusTag(), this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        BusStop stop = busStopsActivity.getBusStops()[0];
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                getLatLng(stop.getLatitude(), stop.getLongitude()), 13.0f));
        mMap.setMyLocationEnabled(true);
        drawRoute();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Auto refreshes active bus locations
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && isVisible) {
                    new UpdateMarkers().execute();
                }
                refreshHandler.postDelayed(this, ACTIVE_BUS_REFRESH_INTERVAL);
            }
        }, ACTIVE_BUS_REFRESH_INTERVAL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            new UpdateMarkers().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible) {
            new UpdateMarkers().execute();
        }
    }

    // Draws the bus route on the map
    private void drawRoute() {
        // Draws the active bus locations
        new UpdateMarkers().execute();

        // Draws the bus route
        int polyLineColor = getResources().getColor(R.color.polyline_color);
        for (BusPathSegment pathSegment : pathSegments) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(polyLineColor);

            String[] latitudes = pathSegment.getLatitudes();
            String[] longitudes = pathSegment.getLongitudes();
            int size = latitudes.length;
            for (int j = 0; j < size; j++) {
                polylineOptions.add(getLatLng(latitudes[j], longitudes[j]));
            }

            mMap.addPolyline(polylineOptions);
        }
    }

    private LatLng getLatLng(String latitude, String longitude) {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    public void setPathSegments(BusPathSegment[] pathSegments) {
        this.pathSegments = pathSegments;
    }

    // Update active bus locations
    private class UpdateMarkers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NextBusAPI.updateActiveBuses();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // Update active bus locations
            if (AppData.activeLatsHashMap != null && AppData.activeLonsHashMap != null) {
                HashMap<String, ArrayList<String>> activeLatsHashMap = AppData.activeLatsHashMap;
                HashMap<String, ArrayList<String>> activeLonsHashMap = AppData.activeLonsHashMap;
                String busTag = busStopsActivity.getBusTag();

                ArrayList<String> activeLats = activeLatsHashMap.get(busTag);
                ArrayList<String> activeLons = activeLonsHashMap.get(busTag);

                if (activeLats != null && activeLons != null) {
                    // Clear map of active bus markers
                    for (int i = 0; i < activeBusMarkers.size(); i++) {
                        activeBusMarkers.get(i).remove();
                    }
                    activeBusMarkers.clear();

                    // Add active bus markers
                    for (int i = 0; i < activeLats.size(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(getLatLng(activeLats.get(i), activeLons.get(i)))
                                .title("Active Bus")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus));
                        activeBusMarkers.add(mMap.addMarker(markerOptions));
                    }
                }
            }

            // Draw the bus stop markers
            BusStop[] busStops = busStopsActivity.getBusStops();
            if (busStopMarkers.isEmpty()) { // Create the markers
                for (BusStop stop : busStops) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(getLatLng(stop.getLatitude(), stop.getLongitude()))
                            .title(stop.getTitle());
                    if (!stop.isActive()) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }
                    busStopMarkers.add(mMap.addMarker(markerOptions));
                }
            } else { // Change the color if necessary
                for (int i = 0; i < busStops.length; i++) {
                    if (!busStops[i].isActive()) {
                        busStopMarkers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    } else {
                        busStopMarkers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                }
            }
        }
    }
}