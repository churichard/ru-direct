package me.rutgersdirect.rudirect.api;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.util.ArrayList;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusData;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class XMLBusTimesHandler extends DefaultHandler {

    private static final String TAG = XMLBusTimesHandler.class.getSimpleName();
    private String busTag;
    private boolean inBusTag;
    private BusData busData;
    private BusStop[] busStops;
    private ArrayList<Integer> times;
    private int currentStopIndex;

    public XMLBusTimesHandler(String busTag) {
        this.busTag = busTag;
    }

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();
        busStops = busData.getBusTagToBusStops().get(busTag);
        inBusTag = false;
        currentStopIndex = 0;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("predictions")) {
            inBusTag = true;
            times = new ArrayList<>();
        }
        if (inBusTag && qName.equalsIgnoreCase("prediction")) {
            int min = Integer.parseInt(atts.getValue("minutes"));
            times.add(min);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inBusTag && qName.equalsIgnoreCase("predictions")) {
            inBusTag = false;
            int[] timesTemp;

            if (times.size() != 0) {
                timesTemp = new int[times.size()];
                for (int i = 0; i < timesTemp.length; i++) {
                    timesTemp[i] = times.get(i);
                }
            } else {
                timesTemp = new int[]{-1}; // Offline bus
            }

            busStops[currentStopIndex].setTimes(timesTemp);
            currentStopIndex++;
        }
    }

    public void endDocument() throws SAXException {
        // Update bus data
        try {
            RUDirectApplication.getDatabaseHelper().getDao().createOrUpdate(busData);
        } catch (SQLException e) {
            Log.e(TAG, e.toString(), e);
        }
    }
}