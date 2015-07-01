package me.rutgersdirect.rudirect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;

public class RUDirectUtil {

    // Checks to see if the network is available or not
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) RUDirectApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Loads an array from shared preferences
    public static String[] loadArray(SharedPreferences prefs, String arrayName) {
        StringBuilder builder = new StringBuilder();
        builder.append(arrayName).append("_size");
        int size = prefs.getInt(builder.toString(), 0);
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            builder.delete(arrayName.length(), builder.length());
            builder.append("_").append(i);
            array[i] = prefs.getString(builder.toString(), null);
        }
        return array;
    }

    // Loads an array from shared preferences
    public static String[] loadArray(int preference, String arrayName) {
        SharedPreferences prefs = RUDirectApplication.getContext().getSharedPreferences(
                RUDirectApplication.getContext().getString(preference), Context.MODE_PRIVATE);
        return loadArray(prefs, arrayName);
    }

    // Loads a 2D string array from shared preferences
    public static String[][] loadTwoDimenArray(int preference, String arrayName) {
        SharedPreferences prefs = RUDirectApplication.getContext().getSharedPreferences(
                RUDirectApplication.getContext().getString(preference), Context.MODE_PRIVATE);
        StringBuilder builder = new StringBuilder();
        builder.append(arrayName).append("_size");
        int arraySize = prefs.getInt(builder.toString(), 0);
        String[][] array = new String[arraySize][];
        for (int i = 0; i < arraySize; i++) {
            builder.delete(arrayName.length(), builder.length());
            builder.append("_arr_").append(i).append("_size");
            int size = prefs.getInt(builder.toString(), 0);
            array[i] = new String[size];
            for (int j = 0; j < size; j++) {
                builder.delete(arrayName.length() + 3, builder.length());
                builder.append(i).append("_ele_").append(j);
                array[i][j] = prefs.getString(builder.toString(), null);
            }
        }
        return array;
    }

    // Saves a string array to shared preferences
    public static void saveArray(SharedPreferences.Editor editor, ArrayList<String> array, String arrayName) {
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
    public static void saveTwoDimenArray(SharedPreferences.Editor editor,
                                         ArrayList<ArrayList<String>> array, String arrayName) {
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
}