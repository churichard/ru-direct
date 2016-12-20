package org.rudirect.android.data.constants;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.rudirect.android.BuildConfig;
import org.rudirect.android.R;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

public class RUDirectApplication extends Application {

    private static final String TAG = RUDirectApplication.class.getSimpleName();
    private static Context mContext;
    private static DatabaseHelper databaseHelper;
    private static BusData busData;
    private static Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        OpenHelperManager.setOpenHelperClass(DatabaseHelper.class);
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public static BusData getBusData() {
        if (busData == null) {
            try {
                // Query database first
                List<BusData> data = getDatabaseHelper().getDao().queryForAll();
                if (data.size() > 0) {
                    busData = data.get(0);
                }
                else {
                    // Create a new object in the database if it is nonexistent
                    busData = new BusData();
                    getDatabaseHelper().getDao().create(busData);
                }
            } catch (SQLException e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        return busData;
    }

    public static synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getContext());
            GoogleAnalytics.getInstance(getContext()).setLocalDispatchPeriod(120);
            if (!BuildConfig.DEBUG) {
                mTracker = analytics.newTracker(getContext().getString(R.string.release_tracker_id));
            } else {
                mTracker = analytics.newTracker(getContext().getString(R.string.debug_tracker_id));
            }
        }
        return mTracker;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}