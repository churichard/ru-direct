package me.rutgersdirect.rudirect.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedHashSet;

import me.rutgersdirect.rudirect.BusConstants;

public class XMLActiveBusHandler extends DefaultHandler {
    private LinkedHashSet<String> activeBuses;

    public void startDocument() throws SAXException {
        activeBuses = new LinkedHashSet<>();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            activeBuses.add(atts.getValue("routeTag"));
        }
    }

    public void endDocument() throws SAXException {
        BusConstants.ACTIVE_BUSES = activeBuses.toArray(new String[activeBuses.size()]);
    }
}
