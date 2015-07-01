package me.rutgersdirect.rudirect.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class XMLBusPathHandler extends DefaultHandler {

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

    public void startDocument() throws SAXException {
        Context context = RUDirectApplication.getContext();

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

            RUDirectUtil.saveArray(latitudesEdit, latitudes, busTag);
            RUDirectUtil.saveArray(longitudesEdit, longitudes, busTag);

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
            RUDirectUtil.saveTwoDimenArray(pathLatsEdit, pathLats, busTag);
            RUDirectUtil.saveTwoDimenArray(pathLonsEdit, pathLons, busTag);

            pathLats.clear();
            pathLons.clear();
            pathSize = -1;
        }
    }
}