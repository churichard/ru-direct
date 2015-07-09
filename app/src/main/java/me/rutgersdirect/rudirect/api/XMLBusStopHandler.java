package me.rutgersdirect.rudirect.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusData;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class XMLBusStopHandler extends DefaultHandler {

    private String busTag;
    private boolean isGettingStops;
    private BusData busData;

    private HashMap<String, String[]> busTagsToStopTagsHashMap;
    private HashMap<String, String[]> busTagsToStopTitlesHashMap;
    private HashMap<String, ArrayList<String>> stopTitlesToStopTagsHashMap;

    private ArrayList<String> busTags;
    private ArrayList<String> busTitles;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;

    public void startDocument() throws SAXException {
        busData = RUDirectApplication.getBusData();

        // Initialize HashMaps
        busTagsToStopTagsHashMap = new HashMap<>();
        busTagsToStopTitlesHashMap = new HashMap<>();
        stopTitlesToStopTagsHashMap = new HashMap<>();

        // Initialize ArrayLists
        busTags = new ArrayList<>();
        busTitles = new ArrayList<>();
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();

        isGettingStops = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (!isGettingStops && qName.equalsIgnoreCase("route")) {
            isGettingStops = true;
            busTag = atts.getValue("tag");
            busTags.add(busTag);
            busTitles.add(atts.getValue("title"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            String title = atts.getValue("title");
            String tag = atts.getValue("tag");

            stopTitles.add(title);
            stopTags.add(tag);

            ArrayList<String> arrayList = stopTitlesToStopTagsHashMap.get(title);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                stopTitlesToStopTagsHashMap.put(atts.getValue("title"), arrayList);
            }
            arrayList.add(tag);
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            busTagsToStopTagsHashMap.put(busTag, RUDirectUtil.arrayListToArray(stopTags));
            busTagsToStopTitlesHashMap.put(busTag, RUDirectUtil.arrayListToArray(stopTitles));

            stopTags.clear();
            stopTitles.clear();
        }
    }

    public void endDocument() throws SAXException {
        HashMap<String, String> busTagsToBusTitlesHashMap = new HashMap<>();
        HashMap<String, String> busTitlesToBusTagsHashMap = new HashMap<>();

        for (int i = 0; i < busTags.size(); i++) {
            String tag = busTags.get(i);
            String title = busTitles.get(i);
            busTagsToBusTitlesHashMap.put(tag, title);
            busTitlesToBusTagsHashMap.put(title, tag);
        }

        busData.setBusTagsToStopTags(busTagsToStopTagsHashMap);
        busData.setBusTagsToStopTitles(busTagsToStopTitlesHashMap);
        busData.setBusTagsToBusTitles(busTagsToBusTitlesHashMap);
        busData.setBusTitlesToBusTags(busTitlesToBusTagsHashMap);

        HashMap<String, String[]> stopTitlesToStopTags = new HashMap<>();
        for (String busTag : stopTitlesToStopTagsHashMap.keySet()) {
            stopTitlesToStopTags.put(busTag, RUDirectUtil.arrayListToArray(stopTitlesToStopTagsHashMap.get(busTag)));
        }
        busData.setStopTitlesToStopTags(stopTitlesToStopTags);
    }
}