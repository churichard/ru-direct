package org.rudirect.android.api;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.rudirect.android.data.constants.AppData;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.data.model.BusStopTime;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NextBusAPI {

    private static final String TAG = NextBusAPI.class.getSimpleName();
    private static OkHttpClient okHttpClient;
    private static SAXParser saxParser;

    // Downloads data from a url and returns it as an input stream
    private static InputStream downloadUrl(String url) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        Request request = new Request.Builder().url(url).build();
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
            } else {
                saxParser.parse(inputStream, handler);
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Log.e(TAG, e.toString());
        }
    }

    // Saves the bus routes to the database
    public static void saveBusRoutes() {
        parseXML(AppData.ALL_ROUTES_URL, new XMLBusRouteHandler());
    }

    // Saves the bus stop times to the database
    public static void saveBusStopTimes(BusRoute route) {
        BusStop[] busStops = route.getBusStops();

        if (busStops != null) {
            // Set no Internet stop times and create predictions link
            StringBuilder link = new StringBuilder(AppData.PREDICTIONS_URL);
            ArrayList<BusStopTime> busStopTimes = new ArrayList<>();
            busStopTimes.add(new BusStopTime(-1));
            for (BusStop stop : busStops) {
                stop.setTimes(busStopTimes);
                link.append("&stops=").append(route.getTag()).append("%7Cnull%7C").append(stop.getTag());
            }
//            Log.d("NextBus API", link.toString());

            parseXML(link.toString(), new XMLBusTimesHandler(busStops));
        }
    }

    // Updates active routes
    public static void updateActiveRoutes() {
        parseXML(AppData.VEHICLE_LOCATIONS_URL, new XMLActiveRouteHandler());
    }

    // Returns an array of the active routes
    public static BusRoute[] getActiveRoutes() {
        BusData.setActiveRoutes(new BusRoute[1]); // Default value if no Internet / no active buses
        updateActiveRoutes();
        return BusData.getActiveRoutes();
    }
}