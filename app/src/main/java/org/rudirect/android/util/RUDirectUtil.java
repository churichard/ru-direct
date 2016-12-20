package org.rudirect.android.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.rudirect.android.data.constants.RUDirectApplication;

import java.util.Calendar;

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

    // Returns the relative time difference between the current time and the argument otherTime
    // This is for displaying bus times
    public static String getTimeDiff(long otherTime) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        String timeDiff = DateUtils.getRelativeTimeSpanString(otherTime, currentTime,
                DateUtils.MINUTE_IN_MILLIS).toString();
        if (timeDiff.equals("0 minutes ago")) {
            return "<1 minute ago";
        }
        return timeDiff;
    }
}