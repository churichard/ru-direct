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
    private boolean isGettingStops;
    private String busTag;

    private ArrayList<String> busTags;
    private ArrayList<String> busTitles;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<String> pathLats;
    private ArrayList<String> pathLons;

    private SharedPreferences.Editor busTagsToStopTagsEdit;
    private SharedPreferences.Editor busTagsToStopTitlesEdit;
    private SharedPreferences.Editor latitudesEdit;
    private SharedPreferences.Editor longitudesEdit;
    private SharedPreferences.Editor pathLatsEdit;
    private SharedPreferences.Editor pathLonsEdit;

    public XMLBusStopHandler(Context context) {
        this.context = context;
    }

    // Saves a string array to shared preferences
    private static void saveArray(SharedPreferences.Editor editor, String[] array, String arrayName) {
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "_" + i, array[i]);
        editor.apply();
    }

    // Converts a string ArrayList to a string array
    private static String[] arrayListToArray(ArrayList<String> arrayList) {
        return arrayList.toArray(new String[arrayList.size()]);
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
        SharedPreferences pathLatsPref = context.getSharedPreferences(
                context.getString(R.string.path_latitudes_key), Context.MODE_PRIVATE);
        SharedPreferences pathLonsPref = context.getSharedPreferences(
                context.getString(R.string.path_longitudes_key), Context.MODE_PRIVATE);

        // Initialize SharedPreference editors
        busTagsToStopTagsEdit = busTagsToStopTagsPref.edit();
        busTagsToStopTitlesEdit = busTagsToStopTitlesPref.edit();
        latitudesEdit = latitudesPref.edit();
        longitudesEdit = longitudesPref.edit();
        pathLatsEdit = pathLatsPref.edit();
        pathLonsEdit = pathLonsPref.edit();

        // Initialize ArrayLists
        busTags = new ArrayList<>();
        busTitles = new ArrayList<>();
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        pathLats = new ArrayList<>();
        pathLons = new ArrayList<>();

        isGettingStops = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (qName.equalsIgnoreCase("route") && !isGettingStops) {
            isGettingStops = true;
            busTag = atts.getValue("tag");
            busTags.add(busTag);
            busTitles.add(atts.getValue("title"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            stopTitles.add(atts.getValue("title"));
            stopTags.add(atts.getValue("tag"));
            latitudes.add(atts.getValue("lat"));
            longitudes.add(atts.getValue("lon"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            saveArray(busTagsToStopTagsEdit, arrayListToArray(stopTags), busTag);
            saveArray(busTagsToStopTitlesEdit, arrayListToArray(stopTitles), busTag);
            saveArray(latitudesEdit, arrayListToArray(latitudes), busTag);
            saveArray(longitudesEdit, arrayListToArray(longitudes), busTag);

            stopTags.clear();
            stopTitles.clear();
            latitudes.clear();
            longitudes.clear();
        }
        if (qName.equalsIgnoreCase("point")) {
            pathLats.add(atts.getValue("lat"));
            pathLons.add(atts.getValue("lon"));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("route")) {
            saveArray(pathLatsEdit, arrayListToArray(pathLats), busTag);
            saveArray(pathLonsEdit, arrayListToArray(pathLons), busTag);

            pathLats.clear();
            pathLons.clear();
        }
    }

    public void endDocument() throws SAXException {
        SharedPreferences.Editor tagsToBusesEdit = context.getSharedPreferences(
                context.getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor busesToTagsEdit = context.getSharedPreferences(
                context.getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE).edit();

        for (int i = 0; i < busTags.size(); i++) {
            tagsToBusesEdit.putString(busTags.get(i), busTitles.get(i));
            busesToTagsEdit.putString(busTitles.get(i), busTags.get(i));
        }

        tagsToBusesEdit.apply();
        busesToTagsEdit.apply();
    }
}