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
        if (!BusConstants.TITLES_TO_STOPS.containsKey(busTag)) {
            BusConstants.TITLES_TO_STOPS.put(busTag, getBusStops(busTag, true));
        }
        return BusConstants.TITLES_TO_STOPS.get(busTag);
    }

    // Takes in a bus tag and returns a list of the bus stop tags
    public static String[] getBusStopTags(String busTag) {
        if (!BusConstants.TAGS_TO_STOPS.containsKey(busTag)) {
            BusConstants.TAGS_TO_STOPS.put(busTag, getBusStops(busTag, false));
        }
        return BusConstants.TAGS_TO_STOPS.get(busTag);
    }

    // Returns an ArrayList of bus stop titles or tags
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
        for (String stopTag : busStopTags) {
            String stop = "&stops=" + busTag + "|null|" + stopTag;
            link.append(stop);
        }
        String[] result = null;
        String[] xmlTags = {"predictions"};

        try {
            ArrayList<Object> times = XMLHelper.parse(link.toString(), xmlTags);
            result = new String[times.size()];
            for (int i = 0; i < times.size(); i++) {
                result[i] = (String) times.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}