package me.rutgersdirect.rudirect.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.BusConstants;

public class XMLActiveBusHandler extends DefaultHandler {
    public void startDocument() throws SAXException {
        BusConstants.ACTIVE_BUSES = new ArrayList<>();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            String busTag = atts.getValue("routeTag");
            if (!BusConstants.ACTIVE_BUSES.contains(busTag)) {
                BusConstants.ACTIVE_BUSES.add(busTag);
            }
        }
    }
}
