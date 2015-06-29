package me.rutgersdirect.rudirect.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
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
import me.rutgersdirect.rudirect.data.model.BusStop;

public class BusMapFragment extends MapFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final String TAG = BusMapFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 9000;
    private static final int ACTIVE_BUS_REFRESH_INTERVAL = 10000;

    private GoogleMap mMap;
    private BusStopsActivity busStopsActivity;
    private GoogleApiClient mGoogleApiClient;
    private Handler refreshHandler;
    private ArrayList<Marker> activeBusMarkers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStopsActivity = (BusStopsActivity) getActivity();
        MapsInitializer.initialize(busStopsActivity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activeBusMarkers = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildGoogleApiClient();
        getMapAsync(this);
    }

    // Build the Google API Client
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(busStopsActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                getLatLng(busStopsActivity.getLatitudes()[0], busStopsActivity.getLongitudes()[0]), 13.0f));
        mMap.setMyLocationEnabled(true);
        drawRoute();
    }

    @Override
    public void onConnected(Bundle connectionHint) { /* Do nothing */}

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(busStopsActivity);
        if (status != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), busStopsActivity, REQUEST_CODE);
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        // Auto refreshes active bus locations
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new UpdateActiveBusLocation().execute();
                refreshHandler.postDelayed(this, ACTIVE_BUS_REFRESH_INTERVAL);
            }
        }, ACTIVE_BUS_REFRESH_INTERVAL);
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

    // Draws the bus route on the map
    private void drawRoute() {
        ArrayList<BusStop> busStops = busStopsActivity.getBusStops();
        String[] latitudes = busStopsActivity.getLatitudes();
        String[] longitudes = busStopsActivity.getLongitudes();
        String[][] pathLats = busStopsActivity.getPathLats();
        String[][] pathLons = busStopsActivity.getPathLons();
        int polyLineColor = getResources().getColor(R.color.polyline_color);

        // Draws the active bus locations
        new UpdateActiveBusLocation().execute();

        // Draws the bus stop markers
        for (int i = 0; i < busStops.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(getLatLng(latitudes[i], longitudes[i]))
                    .title(busStops.get(i).getTitle());
            int[] times = busStops.get(i).getTimes();
            if (times.length == 1 && times[0] == -1) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
            mMap.addMarker(markerOptions);
        }

        // Draws the bus route
        for (int i = 0; i < pathLats.length; i++) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(polyLineColor);

            int size = pathLats[i].length;
            for (int j = 0; j < size; j++) {
                polylineOptions.add(getLatLng(pathLats[i][j], pathLons[i][j]));
            }

            mMap.addPolyline(polylineOptions);
        }
    }

    private LatLng getLatLng(String latitude, String longitude) {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    // Update active bus locations
    private class UpdateActiveBusLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NextBusAPI.updateActiveBuses();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (NextBusAPI.activeLatsHashMap != null && NextBusAPI.activeLonsHashMap != null) {
                HashMap<String, ArrayList<String>> activeLatsHashMap = NextBusAPI.activeLatsHashMap;
                HashMap<String, ArrayList<String>> activeLonsHashMap = NextBusAPI.activeLonsHashMap;
                String busTag = busStopsActivity.getBusTag();

                ArrayList<String> activeLats = activeLatsHashMap.get(busTag);
                ArrayList<String> activeLons = activeLonsHashMap.get(busTag);

                if (activeLats != null && activeLons != null) {
                    // Clear map of active bus markers
                    for (int i = 0; i < activeBusMarkers.size(); i++) {
                        activeBusMarkers.get(i).remove();
                    }

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
        }
    }
}