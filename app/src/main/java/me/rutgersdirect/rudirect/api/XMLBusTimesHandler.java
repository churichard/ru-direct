package me.rutgersdirect.rudirect.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.data.AppData;


public class XMLBusTimesHandler extends DefaultHandler {

    private String busTag;
    private boolean inBusTag;
    private ArrayList<int[]> stopTimes;
    private ArrayList<Integer> times;

    public XMLBusTimesHandler(String busTag) {
        this.busTag = busTag;
    }

    public void startDocument() throws SAXException {
        AppData.BUS_TAGS_TO_STOP_TIMES = new HashMap<>();
        stopTimes = new ArrayList<>();
        inBusTag = false;
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
            if (times.size() != 0) {
                int[] temp = new int[times.size()];
                for (int i = 0; i < temp.length; i++) {
                    temp[i] = times.get(i);
                }
                stopTimes.add(temp);
            } else {
                int[] temp = {-1}; // Offline bus
                stopTimes.add(temp);
            }
        }
    }

    public void endDocument() throws SAXException {
        if (stopTimes.size() > 0) {
            int[][] temp = new int[stopTimes.size()][];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = stopTimes.get(i);
            }
            AppData.BUS_TAGS_TO_STOP_TIMES.put(busTag, temp);
        }
    }
}