package me.rutgersdirect.rudirect.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;

public class RUDirectUtil {

    // Checks to see if the network is available or not
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) RUDirectApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Returns an array from an ArrayList
    @SuppressWarnings("unchecked")
    public static <T> T[] arrayListToArray(ArrayList<T> arrayList, Class<T> cls) {
        return arrayList.toArray((T[]) Array.newInstance(cls, arrayList.size()));
    }

    // Returns a sorted array of keys given a map
    public static <T> String[] mapKeySetToSortedArray(Map<String, T> map) {
        if (map != null) {
            Object[] busNamesObj = map.keySet().toArray();
            String[] busNames = Arrays.copyOf(busNamesObj, busNamesObj.length, String[].class);
            Arrays.sort(busNames);
            return busNames;
        }
        return null;
    }
}