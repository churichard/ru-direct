package org.rudirect.android.data.constants;

public class AppData {

    // NextBus API links
    private static final String BASE_URL = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=";
    public static final String VEHICLE_LOCATIONS_URL = BASE_URL + "vehicleLocations";
    public static final String ALL_ROUTES_URL = BASE_URL + "routeConfig";
    public static final String PREDICTIONS_URL = BASE_URL + "predictionsForMultiStops";

    // Google Analytics
    public static final int ROUTE_OR_STOP_NAME_DIMEN = 1;
    public static final int PAGE_CLICKED_FROM_DIMEN = 2;
    public static final int ORIGIN_DIMEN = 3;
    public static final int DESTINATION_DIMEN = 4;
}