package org.rudirect.android.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.rudirect.android.data.constants.RUDirectApplication;

public class RUDirectUtil {

    // Checks to see if the network is available or not
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) RUDirectApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Convert from dp to px
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = RUDirectApplication.getContext().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    // Hides the keyboard
    public static void hideKeyboard(View v) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) RUDirectApplication.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}