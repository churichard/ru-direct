package me.rutgersdirect.rudirect.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;

public class XMLBusPathHandler extends DefaultHandler {

    private Context context;
    private String busTag;
    private boolean isGettingStops;
    private boolean inPath;
    private int pathSize;

    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<ArrayList<String>> pathLats;
    private ArrayList<ArrayList<String>> pathLons;

    private SharedPreferences.Editor latitudesEdit;
    private SharedPreferences.Editor longitudesEdit;
    private SharedPreferences.Editor pathLatsEdit;
    private SharedPreferences.Editor pathLonsEdit;

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

    // Saves a 2D string array to shared preferences
    private static void saveTwoDimenArray(SharedPreferences.Editor editor, ArrayList<ArrayList<String>> array, String arrayName) {
        int arraySize = array.size();
        StringBuilder builder = new StringBuilder();
        builder.append(arrayName).append("_size");
        editor.putInt(builder.toString(), arraySize);
        for (int i = 0; i < arraySize; i++) {
            int size = array.get(i).size();
            builder.delete(arrayName.length(), builder.length());
            builder.append("_arr_").append(i).append("_size");
            editor.putInt(builder.toString(), size);
            for (int j = 0; j < size; j++) {
                builder.delete(arrayName.length() + 3, builder.length());
                builder.append(i).append("_ele_").append(j);
                editor.putString(builder.toString(), array.get(i).get(j));
            }
        }
        editor.apply();
    }

    public void startDocument() throws SAXException {
        context = RUDirectApplication.getContext();

        SharedPreferences latitudesPref = context.getSharedPreferences(
                context.getString(R.string.latitudes_key), Context.MODE_PRIVATE);
        SharedPreferences longitudesPref = context.getSharedPreferences(
                context.getString(R.string.longitudes_key), Context.MODE_PRIVATE);
        SharedPreferences pathLatsPref = context.getSharedPreferences(
                context.getString(R.string.path_latitudes_key), Context.MODE_PRIVATE);
        SharedPreferences pathLonsPref = context.getSharedPreferences(
                context.getString(R.string.path_longitudes_key), Context.MODE_PRIVATE);

        latitudesEdit = latitudesPref.edit();
        longitudesEdit = longitudesPref.edit();
        pathLatsEdit = pathLatsPref.edit();
        pathLonsEdit = pathLonsPref.edit();

        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        pathLats = new ArrayList<>();
        pathLons = new ArrayList<>();

        isGettingStops = false;
        inPath = false;
        pathSize = -1;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        if (!isGettingStops && qName.equalsIgnoreCase("route")) {
            isGettingStops = true;
            busTag = atts.getValue("tag");
        }
        if (isGettingStops && qName.equalsIgnoreCase("stop")) {
            latitudes.add(atts.getValue("lat"));
            longitudes.add(atts.getValue("lon"));
        }
        if (isGettingStops && qName.equalsIgnoreCase("direction")) {
            isGettingStops = false;

            saveArray(latitudesEdit, latitudes, busTag);
            saveArray(longitudesEdit, longitudes, busTag);

            latitudes.clear();
            longitudes.clear();
        }
        if (!inPath && qName.equalsIgnoreCase("path")) {
            inPath = true;
            pathLats.add(new ArrayList<String>());
            pathLons.add(new ArrayList<String>());
            pathSize++;
        }
        if (inPath && qName.equalsIgnoreCase("point")) {
            pathLats.get(pathSize).add(atts.getValue("lat"));
            pathLons.get(pathSize).add(atts.getValue("lon"));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inPath && qName.equalsIgnoreCase("path")) {
            inPath = false;
        }
        if (qName.equalsIgnoreCase("route")) {
            saveTwoDimenArray(pathLatsEdit, pathLats, busTag);
            saveTwoDimenArray(pathLonsEdit, pathLons, busTag);

            pathLats.clear();
            pathLons.clear();
            pathSize = -1;
        }
    }
}