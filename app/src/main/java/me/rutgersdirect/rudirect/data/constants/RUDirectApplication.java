package me.rutgersdirect.rudirect.data.constants;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import me.rutgersdirect.rudirect.data.model.BusData;
import me.rutgersdirect.rudirect.database.DatabaseHelper;

public class RUDirectApplication extends Application {

    private static final String TAG = RUDirectApplication.class.getSimpleName();
    private static Context mContext;
    private static DatabaseHelper databaseHelper;
    private static BusData busData;
    private static GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        OpenHelperManager.setOpenHelperClass(DatabaseHelper.class);
        mContext = getApplicationContext();
        mGoogleApiClient = getGoogleApiClient();
        mGoogleApiClient.connect();
        MapsInitializer.initialize(mContext);
    }

    public static Context getContext() {
        return mContext;
    }

    public static synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) { /* Do nothing */ }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
                        if (status != ConnectionResult.SUCCESS) {
                            Log.e(TAG, "Could not connect to Google Play services.");
                        } else {
                            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
                        }
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    public static GoogleApiClient getGoogleApiClient() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        return mGoogleApiClient;
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
                List<BusData> busDataList = getDatabaseHelper().getDao().queryForAll();
                if (busDataList.size() != 0) {
                    busData = busDataList.get(0);
                } else {
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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }
}