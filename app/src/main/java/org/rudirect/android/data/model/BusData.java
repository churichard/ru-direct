package org.rudirect.android.data.model;

import android.location.Location;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

public class BusData {

    @DatabaseField(id = true)
    private final int ID = 9000;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BusRoute> routeTagsToBusRoutes;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BusStop> stopTagsToBusStops;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, ArrayList<String>> stopTitleToRouteTags;

    private static ArrayList<BusRoute> activeRoutes; // Active bus routes
    private ArrayList<BusRoute> busRoutes; // All bus routes
    private ArrayList<BusStop> busStops; // All bus stops

    public BusData() {
        // Needed for ormlite
    }

    public int getID() {
        return ID;
    }

    public HashMap<String, BusRoute> getRouteTagsToBusRoutes() {
        return routeTagsToBusRoutes;
    }

    public void setRouteTagsToBusRoutes(HashMap<String, BusRoute> routeTagsToBusRoutes) {
        this.routeTagsToBusRoutes = routeTagsToBusRoutes;
        busRoutes = null;
        busStops = null;
    }

    public HashMap<String, BusStop> getStopTagsToBusStops() {
        return stopTagsToBusStops;
    }

    public void setStopTagsToBusStops(HashMap<String, BusStop> stopTagsToBusStops) {
        this.stopTagsToBusStops = stopTagsToBusStops;
    }

    public HashMap<String, ArrayList<String>> getStopTitleToRouteTags() {
        return stopTitleToRouteTags;
    }

    public void setStopTitleToRouteTags(HashMap<String, ArrayList<String>> stopTitleToRouteTags) {
        this.stopTitleToRouteTags = stopTitleToRouteTags;
    }

    public static ArrayList<BusRoute> getActiveRoutes() {
        return activeRoutes;
    }

    public static void setActiveRoutes(ArrayList<BusRoute> activeRoutes) {
        BusData.activeRoutes = activeRoutes;
    }

    // Returns a list of all bus stops in sorted order
    public ArrayList<BusStop> getAllBusStops() {
        if (busStops == null) {
            // Create list of bus stops
            TreeSet<BusStop> stops = new TreeSet<>();
            ArrayList<BusRoute> busRoutes = getBusRoutes();
            if (busRoutes != null) {
                for (BusRoute route : busRoutes) {
                    stops.addAll(Arrays.asList(route.getBusStops()));
                }
                busStops = new ArrayList<>(stops);
            }
        }
        return busStops;
    }

    // Returns a list of the bus routes in sorted order
    public ArrayList<BusRoute> getBusRoutes() {
        if (busRoutes == null && routeTagsToBusRoutes != null) {
            Collection<BusRoute> routeCollection = routeTagsToBusRoutes.values();
            ArrayList<BusRoute> routes = new ArrayList<>(routeCollection);
            Collections.sort(routes);
            busRoutes = routes;
        }
        return busRoutes;
    }

    // Returns the bus stop nearest to the argument location
    public BusStop getNearestStop(Location location) {
        ArrayList<BusStop> busStops = getAllBusStops();
        BusStop closestStop = null;
        double minDistSq = Double.MAX_VALUE;

        if (busStops != null) {
            for (BusStop stop : busStops) {
                double lat = stop.getLatitude() - location.getLatitude();
                double lon = stop.getLongitude() - location.getLongitude();
                double distSq = lat * lat + lon * lon;
                if (distSq < minDistSq) {
                    minDistSq = distSq;
                    closestStop = stop;
                }
            }
        }

        return closestStop;
    }
}