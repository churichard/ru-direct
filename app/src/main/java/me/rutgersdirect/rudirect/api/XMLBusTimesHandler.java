package me.rutgersdirect.rudirect.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class XMLBusTimesHandler extends DefaultHandler {

    private String busTag;
    private boolean inBusTag;
    private BusStop[] busStops;
    private ArrayList<Integer> times;
    private int currentStopIndex;

    public XMLBusTimesHandler(String busTag) {
        this.busTag = busTag;
    }

    public void startDocument() throws SAXException {
        busStops = RUDirectApplication.getBusData().getBusTagToBusStops().get(busTag);
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
}