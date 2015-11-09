package org.rudirect.android.api;

import android.util.Log;

import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.data.model.BusTime;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class XMLStopTimesHandler extends DefaultHandler {

    private static final String TAG = XMLRouteTimesHandler.class.getSimpleName();
    private BusData busData;
    private BusStop stop;
    private ArrayList<BusRoute> busRoutes;
    private HashMap<String, ArrayList<BusTime>> busStopTimes;

    private ArrayList<BusTime> times;
    private String routeTag;
    private boolean inBusTag;

    public XMLStopTimesHandler(BusStop stop) {
        this.stop = stop;
    }

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();
        stop.setLastUpdatedTime(Calendar.getInstance().getTimeInMillis());
        busRoutes = stop.getBusRoutes();
        busStopTimes = new HashMap<>();
        inBusTag = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("predictions")) {
            inBusTag = true;
            routeTag = atts.getValue("routeTag");
            times = new ArrayList<>();
        }
        if (inBusTag && qName.equalsIgnoreCase("prediction")) {
            // Add bus stop time
            BusTime time = new BusTime(Integer.parseInt(atts.getValue("minutes")),
                    atts.getValue("vehicle"));
            times.add(time);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inBusTag && qName.equalsIgnoreCase("predictions")) {
            busStopTimes.put(routeTag, times);
            inBusTag = false;
            routeTag = null;
        }
    }

    public void endDocument() throws SAXException {
        // Update times
        for (BusRoute route : busRoutes) {
            ArrayList<BusTime> times = busStopTimes.get(route.getTag());
            if (times.size() != 0) {
                route.setTimes(times);
            }
        }

        // Update bus data
        try {
            RUDirectApplication.getDatabaseHelper().getDao().createOrUpdate(busData);
        } catch (SQLException e) {
            Log.e(TAG, e.toString(), e);
        }
    }
}