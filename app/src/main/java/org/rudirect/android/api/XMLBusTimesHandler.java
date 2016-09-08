package org.rudirect.android.api;

import android.util.Log;

import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.data.model.BusStopTime;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class XMLBusTimesHandler extends DefaultHandler {

    private static final String TAG = XMLBusTimesHandler.class.getSimpleName();
    private BusData busData;
    private BusRoute route;
    private BusStop[] busStops;
    private HashMap<String, ArrayList<BusStopTime>> busStopTimes;

    private ArrayList<BusStopTime> times;
    private String stopTag;
    private boolean inBusTag;

    public XMLBusTimesHandler(BusRoute route) {
        this.route = route;
    }

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();
        route.setLastUpdatedTime(Calendar.getInstance().getTimeInMillis());
        busStops = route.getBusStops();
        busStopTimes = new HashMap<>();
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
            BusStopTime time = new BusStopTime(Integer.parseInt(atts.getValue("minutes")),
                    atts.getValue("vehicle"));
            times.add(time);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inBusTag && qName.equalsIgnoreCase("predictions")) {
            busStopTimes.put(stopTag, times);
            inBusTag = false;
            stopTag = null;
        }
    }

    public void endDocument() throws SAXException {
        // Update times
        for (BusStop stop : busStops) {
            ArrayList<BusStopTime> times = busStopTimes.get(stop.getTag());
            if (times != null && times.size() != 0) {
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