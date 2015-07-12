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
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class XMLBusStopHandler extends DefaultHandler {

    private static final String TAG = XMLBusStopHandler.class.getSimpleName();
    private BusData busData;
    private String busTag;
    private boolean isGettingStops;
    private boolean inPath;
    private int pathSize;

    private HashMap<String, BusStop[]> busTagToBusStops;
    private HashMap<String, ArrayList<String>> stopTitlesToStopTagsHashMap;
    private HashMap<String, String[][]> pathLatsHashMap;
    private HashMap<String, String[][]> pathLonsHashMap;

    private ArrayList<String> busTags;
    private ArrayList<String> busTitles;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<ArrayList<String>> pathLats;
    private ArrayList<ArrayList<String>> pathLons;

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();

        // Initialize HashMaps
        busTagToBusStops = new HashMap<>();
        stopTitlesToStopTagsHashMap = new HashMap<>();
        pathLatsHashMap = new HashMap<>();
        pathLonsHashMap = new HashMap<>();

        // Initialize ArrayLists
        busTags = new ArrayList<>();
        busTitles = new ArrayList<>();
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        pathLats = new ArrayList<>();
        pathLons = new ArrayList<>();

        // Initialize misc vars
        isGettingStops = false;
        inPath = false;
        pathSize = -1;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (!isGettingStops && qName.equalsIgnoreCase("route")) {
            isGettingStops = true;
            // Update bus tags and bus titles
            busTag = atts.getValue("tag");
            busTags.add(busTag);
            busTitles.add(atts.getValue("title"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            String title = atts.getValue("title");
            String tag = atts.getValue("tag");

            // Update stop titles and stop tags
            stopTitles.add(title);
            stopTags.add(tag);

            // Update bus stop latitudes and longitudes
            latitudes.add(atts.getValue("lat"));
            longitudes.add(atts.getValue("lon"));

            // Update stop titles to stop tags hash map
            ArrayList<String> arrayList = stopTitlesToStopTagsHashMap.get(title);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                stopTitlesToStopTagsHashMap.put(atts.getValue("title"), arrayList);
            }
            arrayList.add(tag);
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            // Update bus tag to bus stops hash map
            BusStop[] busStops = new BusStop[stopTitles.size()];
            for (int i = 0; i < busStops.length; i++) {
                busStops[i] = new BusStop(stopTags.get(i), stopTitles.get(i), null, latitudes.get(i), longitudes.get(i));
            }
            busTagToBusStops.put(busTag, busStops);

            stopTags.clear();
            stopTitles.clear();
            latitudes.clear();
            longitudes.clear();
        }
        if (!inPath && qName.equalsIgnoreCase("path")) {
            inPath = true;
            pathSize++;

            // Add new path segment
            pathLats.add(new ArrayList<String>());
            pathLons.add(new ArrayList<String>());
        }
        if (inPath && qName.equalsIgnoreCase("point")) {
            // Update lats and lons in path segment
            pathLats.get(pathSize).add(atts.getValue("lat"));
            pathLons.get(pathSize).add(atts.getValue("lon"));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inPath && qName.equalsIgnoreCase("path")) {
            inPath = false;
        }
        if (qName.equalsIgnoreCase("route")) {
            // Update path lats and lons hash maps
            pathLatsHashMap.put(busTag, RUDirectUtil.arrayListToTwoDimenArray(pathLats));
            pathLonsHashMap.put(busTag, RUDirectUtil.arrayListToTwoDimenArray(pathLons));

            pathLats.clear();
            pathLons.clear();
            pathSize = -1;
        }
    }

    public void endDocument() throws SAXException {
        // Update bus tag to bus stops hash map
        busData.setBusTagToBusStops(busTagToBusStops);

        // Update bus tags to bus titles hash map and vice versa
        HashMap<String, String> busTagsToBusTitlesHashMap = new HashMap<>();
        HashMap<String, String> busTitlesToBusTagsHashMap = new HashMap<>();
        for (int i = 0; i < busTags.size(); i++) {
            String tag = busTags.get(i);
            String title = busTitles.get(i);
            busTagsToBusTitlesHashMap.put(tag, title);
            busTitlesToBusTagsHashMap.put(title, tag);
        }
        busData.setBusTagsToBusTitles(busTagsToBusTitlesHashMap);
        busData.setBusTitlesToBusTags(busTitlesToBusTagsHashMap);

        // Update bus tags to path lats/lons hash maps
        busData.setBusTagToPathLatitudes(pathLatsHashMap);
        busData.setBusTagToPathLongitudes(pathLonsHashMap);

        // Update stop titles to stop tags hash map
        HashMap<String, String[]> stopTitlesToStopTags = new HashMap<>();
        for (String busTag : stopTitlesToStopTagsHashMap.keySet()) {
            stopTitlesToStopTags.put(busTag, RUDirectUtil.arrayListToArray(stopTitlesToStopTagsHashMap.get(busTag)));
        }
        busData.setStopTitlesToStopTags(stopTitlesToStopTags);

        // Update bus data
        try {
            RUDirectApplication.getDatabaseHelper().getDao().createOrUpdate(busData);
        } catch (SQLException e) {
            Log.e(TAG, e.toString(), e);
        }
    }
}