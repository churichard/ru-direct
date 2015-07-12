package me.rutgersdirect.rudirect.data.constants;

import java.util.ArrayList;
import java.util.HashMap;

public class AppData {
    // NextBus API links
    private static final String BASE_URL = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=";
    public static final String VEHICLE_LOCATIONS_URL = BASE_URL + "vehicleLocations";
    public static final String ALL_ROUTES_URL = BASE_URL + "routeConfig";
    public static final String PREDICTIONS_URL = BASE_URL + "predictionsForMultiStops";

    // Active buses
    public static String[] ACTIVE_BUSES;
    // Bus tag to latitude segments / longitude segments
    public static HashMap<String, ArrayList<String>> activeLonsHashMap;
    public static HashMap<String, ArrayList<String>> activeLatsHashMap;
}