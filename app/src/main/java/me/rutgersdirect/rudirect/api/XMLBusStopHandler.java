package me.rutgersdirect.rudirect.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;

public class XMLBusStopHandler extends DefaultHandler {

    private Context context;
    private String busTag;
    private boolean isGettingStops;

    private ArrayList<String> busTags;
    private ArrayList<String> busTitles;
    private ArrayList<String> stopTitles;
    private ArrayList<String> stopTags;

    private SharedPreferences.Editor busTagsToStopTagsEdit;
    private SharedPreferences.Editor busTagsToStopTitlesEdit;

    // Saves a string array to shared preferences
    private static void saveArray(SharedPreferences.Editor editor, ArrayList<String> array, String arrayName) {
        int size = array.size();
        StringBuilder builder = new StringBuilder();
        builder.append(arrayName).append("_size");
        editor.putInt(builder.toString(), size);
        for (int i = 0; i < size; i++) {
            builder.delete(arrayName.length(), builder.length());
            builder.append("_").append(i);
            editor.putString(builder.toString(), array.get(i));
        }
        editor.apply();
    }

    public void startDocument() throws SAXException {
        context = RUDirectApplication.getContext();

        // Initialize SharedPreferences
        SharedPreferences busTagsToStopTagsPref = context.getSharedPreferences(
                context.getString(R.string.bus_tags_to_stop_tags_key), Context.MODE_PRIVATE);
        SharedPreferences busTagsToStopTitlesPref = context.getSharedPreferences(
                context.getString(R.string.bus_tags_to_stop_titles_key), Context.MODE_PRIVATE);

        // Initialize SharedPreference editors
        busTagsToStopTagsEdit = busTagsToStopTagsPref.edit();
        busTagsToStopTitlesEdit = busTagsToStopTitlesPref.edit();

        // Initialize ArrayLists
        busTags = new ArrayList<>();
        busTitles = new ArrayList<>();
        stopTitles = new ArrayList<>();
        stopTags = new ArrayList<>();

        isGettingStops = false;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (!isGettingStops && qName.equalsIgnoreCase("route")) {
            isGettingStops = true;
            busTag = atts.getValue("tag");
            busTags.add(busTag);
            busTitles.add(atts.getValue("title"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            stopTitles.add(atts.getValue("title"));
            stopTags.add(atts.getValue("tag"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            saveArray(busTagsToStopTagsEdit, stopTags, busTag);
            saveArray(busTagsToStopTitlesEdit, stopTitles, busTag);

            stopTags.clear();
            stopTitles.clear();
        }
    }

    public void endDocument() throws SAXException {
        SharedPreferences.Editor tagsToBusesEdit = context.getSharedPreferences(
                context.getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor busesToTagsEdit = context.getSharedPreferences(
                context.getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE).edit();

        for (int i = 0; i < busTags.size(); i++) {
            String tag = busTags.get(i);
            String title = busTitles.get(i);
            tagsToBusesEdit.putString(tag, title);
            busesToTagsEdit.putString(title, tag);
        }

        tagsToBusesEdit.apply();
        busesToTagsEdit.apply();
    }
}