package org.rudirect.android.data.model;

import android.location.Location;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public class BusData {

    @DatabaseField(id = true)
    private final int ID = 9000;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BusRoute> busTagsToBusRoutes;

    private static BusRoute[] activeRoutes; // Active bus routes

    public BusData() {
        // Needed for ormlite
    }

    public int getID() {
        return ID;
    }

    public HashMap<String, BusRoute> getBusTagsToBusRoutes() {
        return busTagsToBusRoutes;
    }

    public void setBusTagsToBusRoutes(HashMap<String, BusRoute> busTagsToBusRoutes) {
        this.busTagsToBusRoutes = busTagsToBusRoutes;
    }

    public static BusRoute[] getActiveRoutes() {
        return activeRoutes;
    }

    public static void setActiveRoutes(BusRoute[] activeRoutes) {
        BusData.activeRoutes = activeRoutes;
    }

    // Returns a list of all bus stops in sorted order
    public BusStop[] getAllBusStops() {
        // Create list of bus stops
        TreeSet<BusStop> busStops = new TreeSet<>();
        BusRoute[] busRoutes = getBusRoutes();
        if (busRoutes != null) {
            for (BusRoute route : busRoutes) {
                busStops.addAll(Arrays.asList(route.getBusStops()));
            }
            return busStops.toArray(new BusStop[busStops.size()]);
        } else {
            return null;
        }
    }

    // Returns a list of the bus routes in sorted order
    public BusRoute[] getBusRoutes() {
        if (busTagsToBusRoutes != null) {
            Collection<BusRoute> routeCollection = busTagsToBusRoutes.values();
            BusRoute[] busRoutes = routeCollection.toArray(new BusRoute[routeCollection.size()]);
            Arrays.sort(busRoutes);
            return busRoutes;
        }
        return null;
    }

    // Returns the bus stop nearest to the argument location
    public BusStop getNearestStop(Location location) {
        BusStop[] busStops = getAllBusStops();
        BusStop closestStop = null;
        double minDistSq = Double.MAX_VALUE;

        for (BusStop stop : busStops) {
            double lat = stop.getLatitude() - location.getLatitude();
            double lon = stop.getLongitude() - location.getLongitude();
            double distSq = lat * lat + lon * lon;
            if (distSq < minDistSq) {
                minDistSq = distSq;
                closestStop = stop;
            }
        }

        return closestStop;
    }
}