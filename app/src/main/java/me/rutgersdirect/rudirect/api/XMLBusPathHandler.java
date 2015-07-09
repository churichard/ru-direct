package me.rutgersdirect.rudirect.api;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusData;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class XMLBusPathHandler extends DefaultHandler {

    private static final String TAG = XMLBusPathHandler.class.getSimpleName();
    private BusData busData;
    private String busTag;
    private boolean isGettingStops;
    private boolean inPath;
    private int pathSize;

    private HashMap<String, String[]> latitudeHashMap;
    private HashMap<String, String[]> longitudeHashMap;
    private HashMap<String, String[][]> pathLatsHashMap;
    private HashMap<String, String[][]> pathLonsHashMap;

    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<ArrayList<String>> pathLats;
    private ArrayList<ArrayList<String>> pathLons;

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();

        latitudeHashMap = new HashMap<>();
        longitudeHashMap = new HashMap<>();
        pathLatsHashMap = new HashMap<>();
        pathLonsHashMap = new HashMap<>();

        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        pathLats = new ArrayList<>();
        pathLons = new ArrayList<>();

        isGettingStops = false;
        inPath = false;
        pathSize = -1;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (!isGettingStops && qName.equalsIgnoreCase("route")) {
            isGettingStops = true;
            busTag = atts.getValue("tag");
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            latitudes.add(atts.getValue("lat"));
            longitudes.add(atts.getValue("lon"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            latitudeHashMap.put(busTag, RUDirectUtil.arrayListToArray(latitudes));
            longitudeHashMap.put(busTag, RUDirectUtil.arrayListToArray(longitudes));

            latitudes.clear();
            longitudes.clear();
        }
        if (!inPath && qName.equalsIgnoreCase("path")) {
            inPath = true;
            pathLats.add(new ArrayList<String>());
            pathLons.add(new ArrayList<String>());
            pathSize++;
        }
        if (inPath && qName.equalsIgnoreCase("point")) {
            pathLats.get(pathSize).add(atts.getValue("lat"));
            pathLons.get(pathSize).add(atts.getValue("lon"));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inPath && qName.equalsIgnoreCase("path")) {
            inPath = false;
        }
        if (qName.equalsIgnoreCase("route")) {
            pathLatsHashMap.put(busTag, RUDirectUtil.arrayListToTwoDimenArray(pathLats));
            pathLonsHashMap.put(busTag, RUDirectUtil.arrayListToTwoDimenArray(pathLons));

            pathLats.clear();
            pathLons.clear();
            pathSize = -1;
        }
    }

    public void endDocument() throws SAXException {
        busData.setBusTagToStopLatitudes(latitudeHashMap);
        busData.setBusTagToStopLongitudes(longitudeHashMap);
        busData.setBusTagToPathLatitudes(pathLatsHashMap);
        busData.setBusTagToPathLongitudes(pathLonsHashMap);

        try {
            RUDirectApplication.getDatabaseHelper().getDao().createOrUpdate(busData);
        } catch (SQLException e) {
            Log.e(TAG, e.toString(), e);
        }
    }
}