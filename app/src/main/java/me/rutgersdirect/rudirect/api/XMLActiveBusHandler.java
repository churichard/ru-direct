package me.rutgersdirect.rudirect.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.LinkedHashSet;

import me.rutgersdirect.rudirect.data.AppData;


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
        if (activeBuses.size() > 0) {
            AppData.ACTIVE_BUSES = activeBuses.toArray(new String[activeBuses.size()]);
            Arrays.sort(AppData.ACTIVE_BUSES);
        }
    }
}