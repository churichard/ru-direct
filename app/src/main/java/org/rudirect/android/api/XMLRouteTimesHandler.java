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

public class XMLRouteTimesHandler extends DefaultHandler {

    private static final String TAG = XMLRouteTimesHandler.class.getSimpleName();
    private BusData busData;
    private BusRoute route;
    private BusStop[] busStops;
    private HashMap<String, ArrayList<BusTime>> routeTimes;

    private ArrayList<BusTime> times;
    private String stopTag;
    private boolean inBusTag;

    public XMLRouteTimesHandler(BusRoute route) {
        this.route = route;
    }

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();
        route.setLastUpdatedTime(Calendar.getInstance().getTimeInMillis());
        busStops = route.getBusStops();
        routeTimes = new HashMap<>();
        inBusTag = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("predictions")) {
            inBusTag = true;
            stopTag = atts.getValue("stopTag");
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
            routeTimes.put(stopTag, times);
            inBusTag = false;
            stopTag = null;
        }
    }

    public void endDocument() throws SAXException {
        // Update times
        for (BusStop stop : busStops) {
            ArrayList<BusTime> times = routeTimes.get(stop.getTag());
            if (times.size() != 0) {
                stop.setTimes(times);
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