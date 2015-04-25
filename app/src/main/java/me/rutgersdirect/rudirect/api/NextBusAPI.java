package me.rutgersdirect.rudirect.api;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.XMLHelper;
import me.rutgersdirect.rudirect.model.BusStop;

public class NextBusAPI {

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
    public static Object[] getBusStopTitles(String busTag) {
        return getBusStops(busTag, true);
    }

    // Takes in a bus tag and returns a list of the bus stop tags
    public static Object[] getBusStopTags(String busTag) {
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
            }
            else {
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
        return null;
    }

    // Gets JSON from an address
//    public static String getJSON(String address) {
//        StringBuilder builder = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet(address);
//        try {
//            HttpResponse response = client.execute(httpGet);
//            StatusLine statusLine = response.getStatusLine();
//            int statusCode = statusLine.getStatusCode();
//            if (statusCode == 200) {
//                HttpEntity entity = response.getEntity();
//                InputStream content = entity.getContent();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line);
//                }
//            } else {
//                Log.e(MainActivity.class.toString(), "Failed to get JSON object");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return builder.toString();
//    }

    // Returns bus stop titles and times
//    public static String[][] getBusStopTitlesAndTimes(String json) {
//        ArrayList<String> busStopTitles = new ArrayList<>();
//        ArrayList<String> busStopTimes = new ArrayList<>();
//        try {
//            JSONArray jArray = new JSONArray(json);
//            for (int i = 0; i < jArray.length(); i++) {
//                StringBuilder allTimes = new StringBuilder();
//                JSONObject stopObject = jArray.getJSONObject(i);
//                busStopTitles.add(stopObject.getString("title"));
//                if (!stopObject.getString("predictions").equals("null")) {
//                    JSONArray predictions = stopObject.getJSONArray("predictions");
//                    for (int j = 0; j < predictions.length(); j++) {
//                        JSONObject times = predictions.getJSONObject(j);
//                        String min = times.getString("minutes");
//                        if (min.equals("0")) {
//                            min = "<1";
//                        }
//                        allTimes.append(min);
//                        if (j != predictions.length() - 1) {
//                            allTimes.append(", ");
//                        }
//                    }
//                    allTimes.append(" minutes");
//                }
//                else {
//                    allTimes.append("Offline");
//                }
//                busStopTimes.add(allTimes.toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String[][] titlesAndTimes = {busStopTitles.toArray(new String[busStopTitles.size()]),
//                busStopTimes.toArray(new String[busStopTimes.size()])};
//        return titlesAndTimes;
//    }
}