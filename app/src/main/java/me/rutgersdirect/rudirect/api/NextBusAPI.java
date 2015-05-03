package me.rutgersdirect.rudirect.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.helper.XMLActiveBusHandler;
import me.rutgersdirect.rudirect.helper.XMLBusStopHandler;
import me.rutgersdirect.rudirect.helper.XMLBusTimesHandler;

public class NextBusAPI {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns a list of the active buses
    public static String[] getActiveBusTags() {
        // Default value if there is no Internet or there are no active buses
        BusConstants.ACTIVE_BUSES = new String[1];

        parseXML(BusConstants.VEHICLE_LOCATIONS_LINK, new XMLActiveBusHandler());
        return BusConstants.ACTIVE_BUSES;
    }

    // Saves the bus stops to shared preferences
    public static void saveBusStops(Context context) {
        parseXML(BusConstants.ALL_ROUTES_LINK, new XMLBusStopHandler(context));
    }

    // Loads an array from shared preferences
    private static String[] loadArray(int preference, String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(preference), Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
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

    // Returns a list of the bus stop times
    public static String[] getBusStopTimes(String busTag, Context context) {
        // If there is no Internet
        int length = loadArray(R.string.bus_tags_to_stop_tags_key, busTag, context).length;
        String[] defaultTime = new String[length];
        for (int i = 0; i < length; i++) {
            defaultTime[i] = "Offline";
        }
        BusConstants.BUS_TAGS_TO_STOP_TIMES.put(busTag, defaultTime);

        String[] busStopTags = getBusStopTags(busTag, context);
        StringBuilder link = new StringBuilder(BusConstants.PREDICTIONS_LINK);
        for (String stopTag : busStopTags) {
            String stop = "&stops=" + busTag + "|null|" + stopTag;
            link.append(stop);
        }
        parseXML(link.toString(), new XMLBusTimesHandler(busTag));

        return BusConstants.BUS_TAGS_TO_STOP_TIMES.get(busTag);
    }
}