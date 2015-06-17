package me.rutgersdirect.rudirect.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;


public class XMLBusStopHandler extends DefaultHandler {

    private Context context;
    private boolean inBusTag;
    private String busTag;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private SharedPreferences.Editor busTagsToStopTagsEdit;
    private SharedPreferences.Editor busTagsToStopTitlesEdit;
    private SharedPreferences.Editor latitudesEdit;
    private SharedPreferences.Editor longitudesEdit;

    public XMLBusStopHandler(Context context) {
        this.context = context;
    }

    // Saves an array to shared preferences
    private static void saveArray(SharedPreferences.Editor editor, String[] array, String arrayName) {
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "_" + i, array[i]);
        editor.apply();
    }

    public void startDocument() throws SAXException {
        // Initialize SharedPreferences
        SharedPreferences busTagsToStopTagsPref = context.getSharedPreferences(
                context.getString(R.string.bus_tags_to_stop_tags_key), Context.MODE_PRIVATE);
        SharedPreferences busTagsToStopTitlesPref = context.getSharedPreferences(
                context.getString(R.string.bus_tags_to_stop_titles_key), Context.MODE_PRIVATE);
        SharedPreferences latitudesPref = context.getSharedPreferences(
                context.getString(R.string.latitudes_key), Context.MODE_PRIVATE);
        SharedPreferences longitudesPref = context.getSharedPreferences(
                context.getString(R.string.longitudes_key), Context.MODE_PRIVATE);

        // Initialize SharedPreference editors
        busTagsToStopTagsEdit = busTagsToStopTagsPref.edit();
        busTagsToStopTitlesEdit = busTagsToStopTitlesPref.edit();
        latitudesEdit = latitudesPref.edit();
        longitudesEdit = longitudesPref.edit();

        // Initialize ArrayLists
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();

        inBusTag = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("route")) {
            inBusTag = true;
            busTag = atts.getValue("tag");
        }
        if (inBusTag && qName.equalsIgnoreCase("stop")) {
            stopTitles.add(atts.getValue("title"));
            stopTags.add(atts.getValue("tag"));
            latitudes.add(atts.getValue("lat"));
            longitudes.add(atts.getValue("lon"));
        }
        if (inBusTag && qName.equalsIgnoreCase("direction")) {
            inBusTag = false;

            saveArray(busTagsToStopTagsEdit, stopTags.toArray(new String[stopTags.size()]), busTag);
            saveArray(busTagsToStopTitlesEdit, stopTitles.toArray(new String[stopTitles.size()]), busTag);
            saveArray(latitudesEdit, latitudes.toArray(new String[latitudes.size()]), busTag);
            saveArray(longitudesEdit, longitudes.toArray(new String[longitudes.size()]), busTag);

            stopTags.clear();
            stopTitles.clear();
            latitudes.clear();
            longitudes.clear();
        }
    }
}