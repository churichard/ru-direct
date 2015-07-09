package me.rutgersdirect.rudirect.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class RUDirectUtil {

    // Checks to see if the network is available or not
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) RUDirectApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Returns an array from an ArrayList
    public static String[] arrayListToArray(ArrayList<String> arrayList) {
        return arrayList.toArray(new String[arrayList.size()]);
    }

    // Returns a 2D array from an ArrayList of ArrayLists
    public static String[][] arrayListToTwoDimenArray(ArrayList<ArrayList<String>> arrayList) {
        int size = arrayList.size();
        String[][] array = new String[size][];

        for (int i = 0; i < size; i++) {
            array[i] = arrayListToArray(arrayList.get(i));
        }

        return array;
    }

    // Returns a sorted array of keys given a map
    public static String[] mapKeySetToSortedArray(Map<String, String> map) {
        if (map != null) {
            Object[] busNamesObj = map.keySet().toArray();
            String[] busNames = Arrays.copyOf(busNamesObj, busNamesObj.length, String[].class);
            Arrays.sort(busNames);
            return busNames;
        }
        return null;
    }

    // Return an ArrayList of BusStops given the titles and the times
    public static ArrayList<BusStop> getBusStopArrayList(String tag, String[] titles, int[][] times) {
        ArrayList<BusStop> buses = new ArrayList<>(titles.length);
        for (int i = 0; i < titles.length; i++) {
            buses.add(new BusStop(tag, titles[i], times[i]));
        }
        return buses;
    }
}