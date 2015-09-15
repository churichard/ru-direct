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

    private static ArrayList<BusRoute> activeRoutes; // Active bus routes

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
    }

    public HashMap<String, BusStop> getStopTagsToBusStops() {
        return stopTagsToBusStops;
    }

    public void setStopTagsToBusStops(HashMap<String, BusStop> stopTagsToBusStops) {
        this.stopTagsToBusStops = stopTagsToBusStops;
    }

    public static ArrayList<BusRoute> getActiveRoutes() {
        return activeRoutes;
    }

    public static void setActiveRoutes(ArrayList<BusRoute> activeRoutes) {
        BusData.activeRoutes = activeRoutes;
    }

    // Returns a list of all bus stops in sorted order
    public ArrayList<BusStop> getAllBusStops() {
        // Create list of bus stops
        TreeSet<BusStop> busStops = new TreeSet<>();
        ArrayList<BusRoute> busRoutes = getBusRoutes();
        if (busRoutes != null) {
            for (BusRoute route : busRoutes) {
                busStops.addAll(Arrays.asList(route.getBusStops()));
            }
            return new ArrayList<>(busStops);
        } else {
            return null;
        }
    }

    // Returns a list of the bus routes in sorted order
    public ArrayList<BusRoute> getBusRoutes() {
        if (routeTagsToBusRoutes != null) {
            Collection<BusRoute> routeCollection = routeTagsToBusRoutes.values();
            ArrayList<BusRoute> busRoutes = new ArrayList<>(routeCollection);
            Collections.sort(busRoutes);
            return busRoutes;
        }
        return null;
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