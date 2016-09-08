package org.rudirect.android.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.rudirect.android.R;
import org.rudirect.android.activity.RouteActivity;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusPathSegment;
import org.rudirect.android.data.model.BusStop;

import java.util.ArrayList;

public class BusMapFragment extends MapFragment implements OnMapReadyCallback {

    private static final int ACTIVE_BUS_REFRESH_INTERVAL = 10000;

    private RouteActivity routeActivity;
    private GoogleMap mMap;
    private Handler refreshHandler;
    private ArrayList<Marker> busStopMarkers;
    private ArrayList<Marker> activeBusMarkers;
    private BusPathSegment[] pathSegments;
    private boolean isVisible;
    private boolean connectedToPlayServices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeActivity = (RouteActivity) getActivity();
        setHasOptionsMenu(true);
        connectedToPlayServices = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(routeActivity) == ConnectionResult.SUCCESS;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activeBusMarkers = new ArrayList<>();
        busStopMarkers = new ArrayList<>();
        isVisible = false;
        if (connectedToPlayServices) {
            pathSegments = routeActivity.getRoute().getBusPathSegments();
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        if (connectedToPlayServices) {
            mMap = map;

            // Change map settings and set center location
            mMap.getUiSettings().setMapToolbarEnabled(false);
            BusStop stop = routeActivity.getRoute().getBusStops()[0];
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    getLatLng(stop.getLatitude(), stop.getLongitude()), 13.0f));

            // Move Google logo up so that it's visible
            final TypedArray styledAttributes = RUDirectApplication.getContext().getTheme().obtainStyledAttributes(
                    new int[] { android.R.attr.actionBarSize });
            int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();
            mMap.setPadding(0, 0, 0, actionBarHeight);

            // Show current location on map
            if (ContextCompat.checkSelfPermission(routeActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(routeActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }

            drawRoute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Auto refreshes active bus locations
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && isVisible) new UpdateMarkers().execute();
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
            if (connectedToPlayServices) new UpdateMarkers().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isAdded()) {
            RUDirectApplication.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.route_map_category))
                    .setAction(getString(R.string.view_action))
                    .setLabel(routeActivity.getTitle().toString())
                    .build());
            if (connectedToPlayServices) new UpdateMarkers().execute();
        }
    }

    // Draws the bus route on the map
    private void drawRoute() {
        // Draws the active bus locations
        new UpdateMarkers().execute();

        // Draws the bus route
        int polyLineColor = ContextCompat.getColor(RUDirectApplication.getContext(), R.color.polyline_color);
        for (BusPathSegment pathSegment : pathSegments) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(polyLineColor);

            double[] latitudes = pathSegment.getLatitudes();
            double[] longitudes = pathSegment.getLongitudes();
            int size = latitudes.length;
            for (int j = 0; j < size; j++) {
                polylineOptions.add(getLatLng(latitudes[j], longitudes[j]));
            }

            mMap.addPolyline(polylineOptions);
        }
    }

    private LatLng getLatLng(double latitude, double longitude) {
        return new LatLng(latitude, longitude);
    }

    public void setPathSegments(BusPathSegment[] pathSegments) {
        this.pathSegments = pathSegments;
    }

    // Update active bus locations
    private class UpdateMarkers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NextBusAPI.updateActiveRoutes();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // Update active bus locations
            ArrayList<double[]> activeBusLocations = routeActivity.getRoute().getActiveBusLocations();
            if (activeBusLocations != null) {
                // Clear map of active bus markers
                for (int i = 0; i < activeBusMarkers.size(); i++) {
                    activeBusMarkers.get(i).remove();
                }
                activeBusMarkers.clear();

                // Add active bus markers
                for (int i = 0; i < activeBusLocations.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(getLatLng(activeBusLocations.get(i)[0], activeBusLocations.get(i)[1]))
                            .title("Active Bus")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus));
                    activeBusMarkers.add(mMap.addMarker(markerOptions));
                }
            }

            // Draw the bus stop markers
            BusStop[] busStops = routeActivity.getRoute().getBusStops();
            if (busStops != null) {
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
}