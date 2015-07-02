package me.rutgersdirect.rudirect.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import me.rutgersdirect.rudirect.data.constants.AppData;

public class XMLActiveBusHandler extends DefaultHandler {

    public static TreeSet<String> activeBuses;

    public void startDocument() throws SAXException {
        activeBuses = new TreeSet<>();
        NextBusAPI.activeLatsHashMap = new HashMap<>();
        NextBusAPI.activeLonsHashMap = new HashMap<>();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            // Add to active buses
            String busTag = atts.getValue("routeTag");
            activeBuses.add(busTag);

            // Add to lats
            ArrayList<String> lats = NextBusAPI.activeLatsHashMap.get(busTag);
            if (lats == null) {
                lats = new ArrayList<>();
            }
            lats.add(atts.getValue("lat"));
            NextBusAPI.activeLatsHashMap.put(busTag, lats);

            // Add to lons
            ArrayList<String> lons = NextBusAPI.activeLonsHashMap.get(busTag);
            if (lons == null) {
                lons = new ArrayList<>();
            }
            lons.add(atts.getValue("lon"));
            NextBusAPI.activeLonsHashMap.put(busTag, lons);
        }
    }

    public void endDocument() throws SAXException {
        if (activeBuses.size() > 0) {
            AppData.ACTIVE_BUSES = collectionToArray(activeBuses);
        }
    }

    // Returns an array representation of the given collection
    private String[] collectionToArray(AbstractCollection<String> arrayList) {
        return arrayList.toArray(new String[arrayList.size()]);
    }
}