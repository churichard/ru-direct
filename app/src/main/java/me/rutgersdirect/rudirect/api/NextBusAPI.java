package me.rutgersdirect.rudirect.api;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.XMLActiveBusHandler;
import me.rutgersdirect.rudirect.helper.XMLBusStopHandler;
import me.rutgersdirect.rudirect.helper.XMLBusTimesHandler;

public class NextBusAPI {
    // Returns the input stream from the parameter url
    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
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
        String[] busArray = {"No active buses"};
        parseXML(BusConstants.VEHICLE_LOCATIONS_LINK, new XMLActiveBusHandler());

        // Return active buses
        if (BusConstants.ACTIVE_BUSES.size() > 0) {
            busArray = BusConstants.ACTIVE_BUSES.toArray(new String[BusConstants.ACTIVE_BUSES.size()]);
            Arrays.sort(busArray);
        }

        return busArray;
    }

    // Returns an ArrayList of bus stop titles or tags
    private static void getBusStops(String busTag) {
        BusConstants.currentBusTag = busTag;
        parseXML(BusConstants.ALL_ROUTES_LINK, new XMLBusStopHandler());
    }

    // Takes in a bus tag and returns a list of the bus stop titles
    public static String[] getBusStopTitles(String busTag) {
        if (!BusConstants.BUS_TAGS_TO_STOP_TITLES.containsKey(busTag)) {
            getBusStops(busTag);
        }
        return BusConstants.BUS_TAGS_TO_STOP_TITLES.get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus stop tags
    public static String[] getBusStopTags(String busTag) {
        if (!BusConstants.BUS_TAGS_TO_STOP_TAGS.containsKey(busTag)) {
            getBusStops(busTag);
        }
        return BusConstants.BUS_TAGS_TO_STOP_TAGS.get(busTag);
    }

    // Returns a list of the bus stop times
    public static String[] getBusStopTimes(String busTag) {
        BusConstants.currentBusTag = busTag;
        String[] busStopTags = getBusStopTags(busTag);
        StringBuilder link = new StringBuilder(BusConstants.PREDICTIONS_LINK);
        for (String stopTag : busStopTags) {
            String stop = "&stops=" + busTag + "|null|" + stopTag;
            link.append(stop);
        }
        parseXML(link.toString(), new XMLBusTimesHandler());

        return BusConstants.BUS_TAGS_TO_STOP_TIMES.get(busTag);
    }
}