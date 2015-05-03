package me.rutgersdirect.rudirect.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.BusConstants;

public class XMLBusStopHandler extends DefaultHandler {
    private boolean inBusTag;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;

    public void startDocument() throws SAXException {
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();
        inBusTag = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("route") && atts.getValue("tag").equals(BusConstants.currentBusTag)) {
            inBusTag = true;
        }
        if (inBusTag && qName.equalsIgnoreCase("stop")) {
            stopTitles.add(atts.getValue("title"));
            stopTags.add(atts.getValue("tag"));
        }
        if (inBusTag && qName.equalsIgnoreCase("direction")) {
            inBusTag = false;
        }
    }

    public void endDocument() throws SAXException {
        BusConstants.BUS_TAGS_TO_STOP_TITLES.put(BusConstants.currentBusTag, stopTitles.toArray(new String[stopTitles.size()]));
        BusConstants.BUS_TAGS_TO_STOP_TAGS.put(BusConstants.currentBusTag, stopTags.toArray(new String[stopTags.size()]));
    }
}
