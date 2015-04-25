package me.rutgersdirect.rudirect.helper;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import me.rutgersdirect.rudirect.model.BusStop;

public class XMLHelper {
    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
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

    // Parses the XML feed.
    public static ArrayList<Object> parse(String urlString, String[] xmlTags) throws XmlPullParserException, IOException {
        InputStream in = downloadUrl(urlString);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser, xmlTags);
        } finally {
            in.close();
        }
    }

    // Reads in the XML feed.
    private static ArrayList<Object> readFeed(XmlPullParser parser, String[] xmlTags) throws XmlPullParserException, IOException {
        ArrayList<Object> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "body");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the first xml tag
            if (name.equals(xmlTags[0]) && xmlTags[0].equals("vehicle")) {
                entries.add(readRouteTag(parser));
            } else if (name.equals(xmlTags[0]) && xmlTags[0].equals("route") && parser.getAttributeValue(null, "tag").equals(xmlTags[1])) {
                entries.addAll(readStopTags(parser));
            } else if (name.equals(xmlTags[0]) && xmlTags[0].equals("predictions")) {
                entries.add(readPredictions(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Processes route tags in the feed.
    private static String readRouteTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "vehicle");
        String routeTag = parser.getAttributeValue(null, "routeTag");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "vehicle");
        return routeTag;
    }

    // Processes stop tags in the feed.
    private static ArrayList<Object> readStopTags(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "route");
        ArrayList<Object> stops = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("stop")) {
                stops.add(readStop(parser));
            } else {
                skip(parser);
            }
        }
        return stops;
    }

    // Reads in a stop.
    private static BusStop readStop(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "stop");
        String stopTag = parser.getAttributeValue(null, "tag");
        String stopTitle = parser.getAttributeValue(null, "title");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "stop");
        return new BusStop(stopTag, stopTitle);
    }

    // Processes prediction tags in the feed.
    private static String readPredictions(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "predictions");
        String preds = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("direction")) {
                preds = readDirection(parser);
            } else {
                skip(parser);
            }
        }
        // If there are no active buses
        if (preds == null) {
            preds = "Offline";
        }
        return preds;
    }

    // Reads in a direction for a stop.
    private static String readDirection(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "direction");
        StringBuilder pred = new StringBuilder();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("prediction")) {
                if (pred.length() != 0) {
                    pred.append(", ");
                }
                pred.append(readTimes(parser));
            }
        }
        pred.append(" minutes");
        return pred.toString();
    }

    // Reads in predictions for a stop.
    private static String readTimes(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "prediction");
        String min = parser.getAttributeValue(null, "minutes");
        if (min.equals("0")) {
            min = "<1";
        }
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "prediction");
        return min;
    }

    // Skips the tag.
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
