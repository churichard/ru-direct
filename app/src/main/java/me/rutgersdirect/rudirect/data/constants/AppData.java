package me.rutgersdirect.rudirect.data.constants;

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
    // HashMap of bus tags to bus stop times
    public static HashMap<String, int[][]> BUS_TAGS_TO_STOP_TIMES;
    // Array of active buses
    public static String[] ACTIVE_BUSES;
}