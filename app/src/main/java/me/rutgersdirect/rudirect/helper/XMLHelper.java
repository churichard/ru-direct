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
import java.util.List;

import me.rutgersdirect.rudirect.model.Vehicle;

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
    public static List parse(String urlString, String entry) throws XmlPullParserException, IOException {
        InputStream in = downloadUrl(urlString);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser, entry);
        } finally {
            in.close();
        }
    }

    // Reads in the XML feed.
    private static List readFeed(XmlPullParser parser, String entry) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, null, "body");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the vehicle tag
            if (name.equals(entry)) {
                entries.add(readEntry(parser, entry));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry.
    private static Object readEntry(XmlPullParser parser, String entry) throws XmlPullParserException, IOException {
        if (entry.equals("vehicle")) {
            String routeTag = readRouteTag(parser);
            return new Vehicle(routeTag);
        }
        return null;
    }

    // Processes route tags in the feed.
    private static String readRouteTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "vehicle");
        String routeTag = parser.getAttributeValue(null, "routeTag");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "vehicle");
        return routeTag;
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
