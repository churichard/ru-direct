package me.rutgersdirect.rudirect.api;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.XMLHelper;
import me.rutgersdirect.rudirect.model.BusStop;

public class NextbusAPI {

    // Returns a list of the active buses
    public static String[] getActiveBusTags() {
        ArrayList<String> buses = new ArrayList<>();
        String[] xmlTags = {"vehicle"};
        try {
            ArrayList routeTags = XMLHelper.parse(BusConstants.VEHICLE_LOCATIONS_LINK, xmlTags);
            for (Object rt : routeTags) {
                String rTag = (String) rt;
                if (!buses.contains(rTag)) {
                    buses.add(rTag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buses.toArray(new String[buses.size()]);
    }

    // Takes in a bus tag and returns a list of the bus stop titles
    public static String[] getBusStopTitles(String busTag) {
        return getBusStops(busTag, true);
    }

    // Takes in a bus tag and returns a list of the bus stop tags
    public static String[] getBusStopTags(String busTag) {
        return getBusStops(busTag, false);
    }

    // Takes in a bus tag and whether or not it is getting titles and returns an ArrayList of bus stops
    private static String[] getBusStops(String busTag, boolean isGettingTitles) {
        String[] result = null;
        String[] xmlTags = {"route", busTag};
        try {
            ArrayList<Object> stops = XMLHelper.parse(BusConstants.ALL_ROUTES_LINK, xmlTags);
            result = new String[stops.size()];
            if (isGettingTitles) {
                for (int i = 0; i < stops.size(); i++) {
                    result[i] = ((BusStop) stops.get(i)).title;
                }
            } else {
                for (int i = 0; i < stops.size(); i++) {
                    result[i] = ((BusStop) stops.get(i)).tag;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // Returns a list of the bus stop times
    public static String[] getBusStopTimes(String busTag) {
        String[] busStopTags = getBusStopTags(busTag);
        StringBuilder link = new StringBuilder(BusConstants.PREDICTIONS_LINK);
        for (String tag : busStopTags) {
            String stop = "&stops=" + busTag + "|null|" + tag;
            link.append(stop);
        }
        ArrayList<String> timesStrings = new ArrayList<>();
        String[] xmlTags = {"predictions"};

        try {
            ArrayList<Object> times = XMLHelper.parse(link.toString(), xmlTags);
            for (Object t : times) {
                timesStrings.add((String) t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timesStrings.toArray(new String[timesStrings.size()]);
    }
}