package me.rutgersdirect.rudirect.fragment;

import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.model.BusStop;


public class BusMapFragment extends MapFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = BusMapFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 9000;
    private static final int REFRESH_INTERVAL = 5000;
    private static final int FASTEST_REFRESH_INTERVAL = 5000;
    private GoogleMap mMap;
    private BusStopsActivity busStopsActivity;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        busStopsActivity = (BusStopsActivity) getActivity();
        mMap = getMap();

        buildGoogleApiClient();
        createLocationRequest();
        drawRoute();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                getLatLng(busStopsActivity.getLatitudes()[0], busStopsActivity.getLongitudes()[0]), 13.0f));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(busStopsActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(REFRESH_INTERVAL)
                .setFastestInterval(FASTEST_REFRESH_INTERVAL);
    }

    // Draws the bus route on the map
    private void drawRoute() {
        ArrayList<BusStop> busStops = busStopsActivity.getBusStops();
        String[] latitudes = busStopsActivity.getLatitudes();
        String[] longitudes = busStopsActivity.getLongitudes();
        String[] pathLats = busStopsActivity.getPathLats();
        String[] pathLons = busStopsActivity.getPathLons();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(getResources().getColor(R.color.polyline_color));

        for (int i = 0; i < busStops.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(getLatLng(latitudes[i], longitudes[i]))
                    .title(busStops.get(i).getTitle());
            mMap.addMarker(markerOptions);
//            polylineOptions.add(getLatLng(latitudes[i], longitudes[i]));
        }

        for (int i = 0; i < pathLats.length; i++) {
            polylineOptions.add(getLatLng(pathLats[i], pathLons[i]));
        }

        mMap.addPolyline(polylineOptions);
    }

    // Draws the current location on the map
    private void handleNewLocation(Location location) {
        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (location != null) {
            Log.d(TAG, "Last Update Time: " + mLastUpdateTime);

            // Add current location to map
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(getLatLng(location))
                    .title("Your current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
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
}