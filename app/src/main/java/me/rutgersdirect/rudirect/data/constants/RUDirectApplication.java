package me.rutgersdirect.rudirect.data.constants;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import me.rutgersdirect.rudirect.data.model.BusData;
import me.rutgersdirect.rudirect.database.DatabaseHelper;

public class RUDirectApplication extends Application {

    private static final String TAG = RUDirectApplication.class.getSimpleName();
    private static Context mContext;
    private static DatabaseHelper databaseHelper;
    private static BusData busData;

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
                for (BusData data : getDatabaseHelper().getDao()) {
                    busData = data;
                }
                // Create a new object and store in database
                if (busData == null) {
                    busData = new BusData();
                    getDatabaseHelper().getDao().create(busData);
                }
            } catch (SQLException e) {
                Log.d(TAG, e.toString(), e);
            }
        }
        return busData;
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