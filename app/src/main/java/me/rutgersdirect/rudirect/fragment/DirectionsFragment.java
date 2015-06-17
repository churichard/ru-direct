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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

import me.rutgersdirect.rudirect.activity.BusStopsActivity;


public class DirectionsFragment extends MapFragment implements
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        handleNewLocation(location);
        startLocationUpdates();
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(location), 12.0f));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
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

    private void handleNewLocation(Location location) {
        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (location != null) {
            Log.d(TAG, "Latitude: " + location.getLatitude()
                    + "\tLongitude: " + location.getLongitude()
                    + "\tLast Update Time: " + mLastUpdateTime);

            MarkerOptions options = new MarkerOptions()
                    .position(getLatLng(location))
                    .title("Current location");
            mMap.addMarker(options);
        }
    }

    private LatLng getLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}