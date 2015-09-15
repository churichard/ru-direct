package org.rudirect.android.api;

import android.util.Log;

import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.data.model.BusPathSegment;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.data.model.BusStop;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLBusRouteHandler extends DefaultHandler {

    private static final String TAG = XMLBusRouteHandler.class.getSimpleName();
    private BusData busData;
    private HashMap<String, BusRoute> busTagsToBusRoutes;

    private BusRoute currentRoute;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;
    private ArrayList<Double> latitudes;
    private ArrayList<Double> longitudes;
    private ArrayList<Double> pathLats;
    private ArrayList<Double> pathLons;
    private ArrayList<BusPathSegment> busPathSegments;
    private boolean isGettingStops;
    private boolean inPath;

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();

        // Get bus tags to bus routes hash map
        busTagsToBusRoutes = busData.getRouteTagsToBusRoutes();
        if (busTagsToBusRoutes == null) {
            busTagsToBusRoutes = new HashMap<>();
        }

        // Initialize helper variables
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        pathLats = new ArrayList<>();
        pathLons = new ArrayList<>();
        busPathSegments = new ArrayList<>();
        isGettingStops = false;
        inPath = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (!isGettingStops && qName.equalsIgnoreCase("route")) {
            isGettingStops = true;

            String routeTag = atts.getValue("tag");
            String routeTitle = atts.getValue("title");

            // Update route tag and title
            currentRoute = busTagsToBusRoutes.get(routeTag);
            if (currentRoute == null) {
                currentRoute = new BusRoute(routeTag, routeTitle);
                busTagsToBusRoutes.put(routeTag, currentRoute);
            } else {
                currentRoute.setTag(routeTag);
                currentRoute.setTitle(routeTitle);
            }
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            String title = atts.getValue("title");
            String tag = atts.getValue("tag");

            // Update stop titles and stop tags
            stopTitles.add(title);
            stopTags.add(tag);

            // Update bus stop latitudes and longitudes
            latitudes.add(Double.parseDouble(atts.getValue("lat")));
            longitudes.add(Double.parseDouble(atts.getValue("lon")));
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            // Update bus tag to bus stops hash map
            BusStop[] busStops = new BusStop[stopTitles.size()];
            for (int i = 0; i < busStops.length; i++) {
                busStops[i] = new BusStop(stopTags.get(i), stopTitles.get(i), null,
                        latitudes.get(i), longitudes.get(i));
            }
            currentRoute.setBusStops(busStops);

            stopTags.clear();
            stopTitles.clear();
            latitudes.clear();
            longitudes.clear();
        }
        if (!inPath && qName.equalsIgnoreCase("path")) {
            inPath = true;
        }
        if (inPath && qName.equalsIgnoreCase("point")) {
            // Update lats and lons in path segment
            pathLats.add(Double.parseDouble(atts.getValue("lat")));
            pathLons.add(Double.parseDouble(atts.getValue("lon")));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inPath && qName.equalsIgnoreCase("path")) {
            inPath = false;

            // Add new path segment
            busPathSegments.add(new BusPathSegment(doubleArrayListToArray(pathLats),
                    doubleArrayListToArray(pathLons)));

            pathLats.clear();
            pathLons.clear();
        }
        if (qName.equalsIgnoreCase("route")) {
            // Update path lats and lons hash maps
            currentRoute.setBusPathSegments(busPathSegments.toArray(new BusPathSegment[busPathSegments.size()]));
            busPathSegments.clear();
        }
    }

    public void endDocument() throws SAXException {
        // Update bus tags to bus routes hash map
        busData.setRouteTagsToBusRoutes(busTagsToBusRoutes);

        // Update bus data
        try {
            RUDirectApplication.getDatabaseHelper().getDao().createOrUpdate(busData);
        } catch (SQLException e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    // Helper method to convert a Double ArrayList to a double array
    private double[] doubleArrayListToArray(ArrayList<Double> arrayList) {
        double[] arr = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            arr[i] = arrayList.get(i);
        }
        return arr;
    }
}