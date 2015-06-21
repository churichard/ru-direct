package me.rutgersdirect.rudirect.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.AppData;

public class NextBusAPI {

    private static final String TAG = NextBusAPI.class.getName();
    public static HashMap<String, ArrayList<String>> activeLatsHashMap;
    public static HashMap<String, ArrayList<String>> activeLonsHashMap;

    // Returns the input stream from the parameter url
    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000); // milliseconds
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    // Setups the SAX parser and parses the XML from the url
    private static void parseXML(String urlString, DefaultHandler handler) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(downloadUrl(urlString), handler);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Log.e(TAG, e.toString());
        }
    }

    // Updates active buses
    public static void updateActiveBuses() {
        parseXML(AppData.VEHICLE_LOCATIONS_LINK, new XMLActiveBusHandler());
    }

    // Returns a list of the active buses
    public static String[] getActiveBusTags() {
        // Default value if there is no Internet or there are no active buses
        AppData.ACTIVE_BUSES = new String[1];

        updateActiveBuses();
        return AppData.ACTIVE_BUSES;
    }

    // Returns a list of the bus stop times
    public static int[][] getBusStopTimes(String busTag, Context context) {
        // If there is no Internet
        int length = loadArray(R.string.bus_tags_to_stop_tags_key, busTag, context).length;
        int[][] defaultTime = new int[length][1];
        for (int i = 0; i < length; i++) {
            defaultTime[i][0] = -1;
        }
        AppData.BUS_TAGS_TO_STOP_TIMES.put(busTag, defaultTime);

        String[] busStopTags = getBusStopTags(busTag, context);
        StringBuilder link = new StringBuilder(AppData.PREDICTIONS_LINK);
        for (String stopTag : busStopTags) {
            String stop = "&stops=" + busTag + "|null|" + stopTag;
            link.append(stop);
        }
        parseXML(link.toString(), new XMLBusTimesHandler(busTag));

        return AppData.BUS_TAGS_TO_STOP_TIMES.get(busTag);
    }

    // Saves the bus stops to shared preferences
    public static void saveBusStops(Context context) {
        parseXML(AppData.ALL_ROUTES_LINK, new XMLBusStopHandler(context));
    }

    // Loads an array from shared preferences
    private static String[] loadArray(int preference, String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(preference), Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = prefs.getString(arrayName + "_" + i, null);
        }
        return array;
    }

    // Loads a 2D string array from shared preferences
    private static String[][] loadTwoDimenArray(int preference, String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(preference), Context.MODE_PRIVATE);
        int arraySize = prefs.getInt(arrayName + "_size", 0);
        String[][] array = new String[arraySize][];
        for (int i = 0; i < arraySize; i++) {
            int size = prefs.getInt(arrayName + "_array_" + i + "_size", 0);
            array[i] = new String[size];
            for (int j = 0; j < size; j++) {
                array[i][j] = prefs.getString(arrayName + "_array_" + i + "_element_" + j, null);
            }
        }
        return array;
    }

    // Takes in a bus tag and returns a list of the bus stop titles
    public static String[] getBusStopTitles(String busTag, Context context) {
        return loadArray(R.string.bus_tags_to_stop_titles_key, busTag, context);
    }

    // Takes in a bus tag and returns a list of the bus stop tags
    public static String[] getBusStopTags(String busTag, Context context) {
        return loadArray(R.string.bus_tags_to_stop_tags_key, busTag, context);
    }

    // Takes in a bus tag and returns a list of the bus stop latitudes
    public static String[] getBusStopLats(String busTag, Context context) {
        return loadArray(R.string.latitudes_key, busTag, context);
    }

    // Takes in a bus tag and returns a list of the bus stop longitudes
    public static String[] getBusStopLons(String busTag, Context context) {
        return loadArray(R.string.longitudes_key, busTag, context);
    }

    // Takes in a bus tag and returns a list of the bus path latitudes
    public static String[][] getBusPathLats(String busTag, Context context) {
        return loadTwoDimenArray(R.string.path_latitudes_key, busTag, context);
    }

    // Takes in a bus tag and returns a list of the bus path longitudes
    public static String[][] getBusPathLons(String busTag, Context context) {
        return loadTwoDimenArray(R.string.path_longitudes_key, busTag, context);
    }
}