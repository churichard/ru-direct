package me.rutgersdirect.rudirect.api;

import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;

public class NextBusAPI {

    private static final String TAG = NextBusAPI.class.getName();

    // Setups the SAX parser and parses the XML from the url
    private static void parseXML(String urlString, DefaultHandler handler) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(urlString, handler);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Log.e(TAG, e.toString());
        }
    }

    // Updates active buses
    public static void updateActiveBuses() {
        parseXML(AppData.VEHICLE_LOCATIONS_URL, new XMLActiveBusHandler());
    }

    // Returns a list of the active buses
    public static String[] getActiveBusTags() {
        // Default value if there is no Internet or there are no active buses
        AppData.ACTIVE_BUSES = new String[1];

        updateActiveBuses();
        return AppData.ACTIVE_BUSES;
    }

    // Returns a list of the bus stop times
    public static int[][] getBusStopTimes(String busTag) {
        // If there is no Internet
        int length = RUDirectApplication.getBusData().getBusTagsToStopTags().get(busTag).length;
        int[][] defaultTime = new int[length][1];
        for (int i = 0; i < length; i++) {
            defaultTime[i][0] = -1;
        }
        AppData.BUS_TAGS_TO_STOP_TIMES.put(busTag, defaultTime);

        String[] busStopTags = getBusStopTags(busTag);
        StringBuilder link = new StringBuilder(AppData.PREDICTIONS_URL);
        for (String stopTag : busStopTags) {
            String stop = "&stops=" + busTag + "|null|" + stopTag;
            link.append(stop);
        }
        parseXML(link.toString(), new XMLBusTimesHandler(busTag));

        return AppData.BUS_TAGS_TO_STOP_TIMES.get(busTag);
    }

    // Saves the bus stops to shared preferences
    public static void saveBusStops() {
        parseXML(AppData.ALL_ROUTES_URL, new XMLBusStopHandler());
    }

    // Saves the bus paths to shared preferences
    public static void saveBusPaths() {
        parseXML(AppData.ALL_ROUTES_URL, new XMLBusPathHandler());
    }

    // Takes in a bus tag and returns a list of the bus stop titles
    public static String[] getBusStopTitles(String busTag) {
        return RUDirectApplication.getBusData().getBusTagsToStopTitles().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus stop tags
    public static String[] getBusStopTags(String busTag) {
        return RUDirectApplication.getBusData().getBusTagsToStopTags().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus stop latitudes
    public static String[] getBusStopLats(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToStopLatitudes().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus stop longitudes
    public static String[] getBusStopLons(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToStopLongitudes().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus path latitudes
    public static String[][] getBusPathLats(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToPathLatitudes().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus path longitudes
    public static String[][] getBusPathLons(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToPathLongitudes().get(busTag);
    }
}