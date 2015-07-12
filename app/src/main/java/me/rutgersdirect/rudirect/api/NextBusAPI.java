package me.rutgersdirect.rudirect.api;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class NextBusAPI {

    private static final String TAG = NextBusAPI.class.getSimpleName();
    private static OkHttpClient okHttpClient;
    private static SAXParser saxParser;

    private static InputStream downloadUrl(String url) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().byteStream();
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }

        return null;
    }

    // Setups the SAX parser and parses the XML from the url
    private static void parseXML(String url, DefaultHandler handler) {
        try {
            if (saxParser == null) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                saxParser = factory.newSAXParser();
            }
            InputStream inputStream = downloadUrl(url);
            if (inputStream == null) {
                throw new IOException("Can't connect to the Internet");
            }
            saxParser.parse(inputStream, handler);
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
        AppData.ACTIVE_BUSES = new String[1]; // Default value if no Internet / no active buses
        updateActiveBuses();
        return AppData.ACTIVE_BUSES;
    }

    // Saves the bus stop times to the database
    public static void saveBusStopTimes(String busTag) {
        BusStop[] busStops = RUDirectApplication.getBusData().getBusTagToBusStops().get(busTag);

        // Set no Internet stop times and create predictions link
        StringBuilder link = new StringBuilder(AppData.PREDICTIONS_URL);
        for (BusStop stop : busStops) {
            stop.setTimes(new int[]{-1});
            link.append("&stops=").append(busTag).append("%7Cnull%7C").append(stop.getTag());
        }

        parseXML(link.toString(), new XMLBusTimesHandler(busTag));
    }

    // Saves the bus stops to the database
    public static void saveBusStops() {
        parseXML(AppData.ALL_ROUTES_URL, new XMLBusStopHandler());
    }

    // Takes in a bus tag and returns a list of bus stops (only called after saving bus stops)
    public static BusStop[] getBusStops(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToBusStops().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus path latitudes (only called after saving bus stops)
    public static String[][] getBusPathLats(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToPathLatitudes().get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus path longitudes (only called after saving bus stops)
    public static String[][] getBusPathLons(String busTag) {
        return RUDirectApplication.getBusData().getBusTagToPathLongitudes().get(busTag);
    }
}