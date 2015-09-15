package org.rudirect.android.api;

import android.util.Log;

import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.data.model.BusRoute;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class XMLActiveRoutesHandler extends DefaultHandler {

    private static final String TAG = XMLActiveRoutesHandler.class.getSimpleName();
    private BusData busData;
    private HashMap<String, BusRoute> busTagsToBusRoutes;
    private TreeSet<BusRoute> activeRoutes;

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();
        busTagsToBusRoutes = busData.getRouteTagsToBusRoutes();
        if (busTagsToBusRoutes == null) {
            busTagsToBusRoutes = new HashMap<>();
        }

        for (BusRoute route : busTagsToBusRoutes.values()) {
            route.setActiveBusLocations(new ArrayList<double[]>());
        }

        activeRoutes = new TreeSet<>();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            // Add to active routes
            String busTag = atts.getValue("routeTag");
            BusRoute route = busTagsToBusRoutes.get(busTag);
            if (route == null) {
                route = new BusRoute(busTag, busTag);
            }
            activeRoutes.add(route);

            // Add active bus location
            ArrayList<double[]> activeBusLocations = route.getActiveBusLocations();
            activeBusLocations.add(new double[]{Double.parseDouble(atts.getValue("lat")),
                    Double.parseDouble(atts.getValue("lon"))});
            route.setActiveBusLocations(activeBusLocations);
        }
    }

    public void endDocument() throws SAXException {
        // Update active routes
        BusData.setActiveRoutes(new ArrayList<>(activeRoutes));

        // Update bus data
        try {
            RUDirectApplication.getDatabaseHelper().getDao().createOrUpdate(busData);
        } catch (SQLException e) {
            Log.e(TAG, e.toString(), e);
        }
    }
}