package me.rutgersdirect.rudirect.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.BusConstants;

public class XMLBusTimesHandler extends DefaultHandler {
    private String busTag;
    private boolean inBusTag;
    private ArrayList<String> stopTimes;
    private StringBuilder times;

    public XMLBusTimesHandler(String busTag) {
        this.busTag = busTag;
    }

    public void startDocument() throws SAXException {
        BusConstants.BUS_TAGS_TO_STOP_TIMES = new HashMap<>();
        stopTimes = new ArrayList<>();
        inBusTag = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("predictions")) {
            inBusTag = true;
            times = new StringBuilder();
        }
        if (inBusTag && qName.equalsIgnoreCase("prediction")) {
            if (times.length() != 0) {
                times.append(", ");
            }
            else if (times.length() == 0) {
                times.append("Arriving in ");
            }
            String min = atts.getValue("minutes");
            if (min.equals("0")) {
                min = "<1";
            }
            times.append(min);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inBusTag && qName.equalsIgnoreCase("predictions")) {
            inBusTag = false;
            if (times.length() != 0) {
                times.append(" minutes.");
            }
            else {
                times.append("Offline");
            }
            stopTimes.add(times.toString());
        }
    }

    public void endDocument() throws SAXException {
        if (stopTimes.size() > 0) {
            BusConstants.BUS_TAGS_TO_STOP_TIMES.put(busTag, stopTimes.toArray(new String[stopTimes.size()]));
        }
    }
}
