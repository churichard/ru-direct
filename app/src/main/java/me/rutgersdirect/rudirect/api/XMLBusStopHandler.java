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
    private SharedPreferences.Editor busTagsToStopTagsEdit;
    private SharedPreferences.Editor busTagsToStopTitlesEdit;

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
        SharedPreferences busTagsToStopTagsPref = context.getSharedPreferences(
                context.getString(R.string.bus_tags_to_stop_tags_key), Context.MODE_PRIVATE);
        SharedPreferences busTagsToStopTitlesPref = context.getSharedPreferences(
                context.getString(R.string.bus_tags_to_stop_titles_key), Context.MODE_PRIVATE);
        busTagsToStopTagsEdit = busTagsToStopTagsPref.edit();
        busTagsToStopTitlesEdit = busTagsToStopTitlesPref.edit();
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();
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
        }
        if (inBusTag && qName.equalsIgnoreCase("direction")) {
            inBusTag = false;
            saveArray(busTagsToStopTagsEdit, stopTags.toArray(new String[stopTags.size()]), busTag);
            saveArray(busTagsToStopTitlesEdit, stopTitles.toArray(new String[stopTitles.size()]), busTag);
            stopTags.clear();
            stopTitles.clear();
        }
    }
}