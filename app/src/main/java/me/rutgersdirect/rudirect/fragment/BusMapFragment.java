package me.rutgersdirect.rudirect.fragment;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = BusMapFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 9000;
    private static final int CURRENT_LOC_REFRESH_INTERVAL = 5000;
    private static final int ACTIVE_BUS_REFRESH_INTERVAL = 10000;

    private GoogleMap mMap;
    private BusStopsActivity busStopsActivity;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Handler refreshHandler;
    private ArrayList<Marker> markers;
    private Marker currentLocation;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        busStopsActivity = (BusStopsActivity) getActivity();
        mMap = getMap();
        markers = new ArrayList<>();

        buildGoogleApiClient();
        createLocationRequest();
        drawRoute();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                getLatLng(busStopsActivity.getLatitudes()[0], busStopsActivity.getLongitudes()[0]), 13.0f));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(busStopsActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        handleNewLocation(location);
        startLocationUpdates();
    }

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
    public void onLocationChanged(Location location) {
        currentLocation.remove();
        handleNewLocation(location);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
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

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(CURRENT_LOC_REFRESH_INTERVAL)
                .setFastestInterval(CURRENT_LOC_REFRESH_INTERVAL);
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

    // Draws the current location on the map
    private void handleNewLocation(Location location) {
        if (location != null) {
            // Add current location to map
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(getLatLng(location))
                    .title("Your current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            currentLocation = mMap.addMarker(markerOptions);
        }
    }

    private LatLng getLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private LatLng getLatLng(String latitude, String longitude) {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
            HashMap<String, ArrayList<String>> activeLatsHashMap = NextBusAPI.activeLatsHashMap;
            HashMap<String, ArrayList<String>> activeLonsHashMap = NextBusAPI.activeLonsHashMap;
            ArrayList<String> activeLats = activeLatsHashMap.get(busStopsActivity.getBusTag());
            ArrayList<String> activeLons = activeLonsHashMap.get(busStopsActivity.getBusTag());

            if (activeLats != null && activeLons != null) {
                // Clear map of active bus markers
                for (int i = 0; i < markers.size(); i++) {
                    markers.get(i).remove();
                }

                // Add active bus markers
                for (int i = 0; i < activeLats.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(getLatLng(activeLats.get(i), activeLons.get(i)))
                            .title("Active Bus")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markers.add(mMap.addMarker(markerOptions));
                }
            }
        }
    }
}