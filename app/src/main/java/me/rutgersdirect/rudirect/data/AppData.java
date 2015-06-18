package me.rutgersdirect.rudirect.data;

import java.util.HashMap;


public class AppData {

    // NextBus API links
    public static final String VEHICLE_LOCATIONS_LINK = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=vehicleLocations";
    public static final String ALL_ROUTES_LINK = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=routeConfig";
    public static final String PREDICTIONS_LINK = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=predictionsForMultiStops";
    // Intent messages
    public static final String BUS_TAG_MESSAGE = "Bus Tag";
    public static final String BUS_STOPS_MESSAGE = "Bus Stops";
    public static final String BUS_STOP_LATS_MESSAGE = "Bus Stop Latitudes";
    public static final String BUS_STOP_LONS_MESSAGE = "Bus Stop Longitudes";
    public static final String BUS_PATH_LATS_MESSAGE = "Bus Path Latitudes";
    public static final String BUS_PATH_LONS_MESSAGE = "Bus Path Longitudes";
    // All bus names and tags
//    public static final String[] allBusNames = {"A", "B", "C", "EE", "F", "H", "LX", "REX B", "REX L", "All Campuses", "New Brunsquick 1 Shuttle", "New Brunsquick 2 Shuttle", "Weekend 1", "Weekend 2"};
//    public static final String[] allBusTags = {"a", "b", "c", "ee", "f", "h", "lx", "rexb", "rexl", "s", "w1", "w2", "wknd1", "wknd2"};
    // HashMap of bus tags to bus stop times
    public static HashMap<String, int[][]> BUS_TAGS_TO_STOP_TIMES;
    // Array of active buses
    public static String[] ACTIVE_BUSES;
}