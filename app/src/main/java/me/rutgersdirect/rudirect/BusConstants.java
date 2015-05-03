package me.rutgersdirect.rudirect;

import java.util.ArrayList;
import java.util.HashMap;

public class BusConstants {
    // Nextbus API links
    public static final String VEHICLE_LOCATIONS_LINK = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=vehicleLocations";
    public static final String ALL_ROUTES_LINK = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=routeConfig";
    public static final String PREDICTIONS_LINK = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=predictionsForMultiStops";
    // Intent messages
    public static final String BUS_TAG_MESSAGE = "Bus Tag";
    public static final String BUS_STOP_TITLES_MESSAGE = "Bus Stop Titles";
    public static final String BUS_STOP_TIMES_MESSAGE = "Bus Stop Times";
    // All bus names and tags
    public static final String[] allBusNames = {"A", "B", "C", "EE", "F", "H", "LX", "REX B", "REX L", "All Campuses", "New Brunsquick 1 Shuttle", "New Brunsquick 2 Shuttle", "Weekend 1", "Weekend 2"};
    public static final String[] allBusTags = {"a", "b", "c", "ee", "f", "h", "lx", "rexb", "rexl", "s", "w1", "w2", "wknd1", "wknd2"};
    // Caching of bus names to tags (and vice versa) and bus tags to stop titles/tags
    public static HashMap<String, String> TAGS_TO_BUSES;
    public static HashMap<String, String> BUSES_TO_TAGS;
    public static HashMap<String, String[]> BUS_TAGS_TO_STOP_TAGS;
    public static HashMap<String, String[]> BUS_TAGS_TO_STOP_TITLES;
    // HashMap of bus tags to bus stop times
    public static HashMap<String, String[]> BUS_TAGS_TO_STOP_TIMES;
    // ArrayList of active buses
    public static ArrayList<String> ACTIVE_BUSES;
    // Current bus tag being worked on
    public static String currentBusTag;
}