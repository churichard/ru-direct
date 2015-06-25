package me.rutgersdirect.rudirect.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import me.rutgersdirect.rudirect.data.constants.AppData;

public class XMLActiveBusHandler extends DefaultHandler {

    public static LinkedHashSet<String> activeBuses;
    private String busTag;
    private ArrayList<String> lats;
    private ArrayList<String> lons;

    public void startDocument() throws SAXException {
        activeBuses = new LinkedHashSet<>();
        NextBusAPI.activeLatsHashMap = new HashMap<>();
        NextBusAPI.activeLonsHashMap = new HashMap<>();
        lats = new ArrayList<>();
        lons = new ArrayList<>();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            busTag = atts.getValue("routeTag");
            lats.add(atts.getValue("lat"));
            lons.add(atts.getValue("lon"));
            activeBuses.add(busTag);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            NextBusAPI.activeLatsHashMap.put(busTag, collectionToArray(lats));
            NextBusAPI.activeLonsHashMap.put(busTag, collectionToArray(lons));
            lats = new ArrayList<>();
            lons = new ArrayList<>();
        }
    }

    public void endDocument() throws SAXException {
        if (activeBuses.size() > 0) {
            AppData.ACTIVE_BUSES = collectionToArray(activeBuses);
            Arrays.sort(AppData.ACTIVE_BUSES);
        }
    }

    // Returns an array representation of the given collection
    private String[] collectionToArray(AbstractCollection<String> arrayList) {
        return arrayList.toArray(new String[arrayList.size()]);
    }
}