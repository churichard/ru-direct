package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class BusRoute implements Parcelable, Serializable, Comparable<BusRoute> {

    private static final long serialVersionUID = 1632593783571404823L;
    private String tag;
    private String title;
    private boolean starred;
    private BusStop[] busStops;
    private BusPathSegment[] busPathSegments;
    private transient ArrayList<double[]> activeBusLocations;

    public BusRoute(String tag, String title) {
        this.tag = tag;
        this.title = title;
        starred = false;
        busStops = null;
        busPathSegments = null;
        activeBusLocations = null;
    }

    @SuppressWarnings("unchecked")
    private BusRoute(Parcel in) {
        tag = in.readString();
        title = in.readString();
        starred = in.readByte() != 0;
        busStops = in.createTypedArray(BusStop.CREATOR);
        busPathSegments = in.createTypedArray(BusPathSegment.CREATOR);
        activeBusLocations = new ArrayList<>();
        activeBusLocations = in.readArrayList(String.class.getClassLoader());
    }

    public BusRoute() {
        // Needed for ormlite
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public BusStop[] getBusStops() {
        return busStops;
    }

    public void setBusStops(BusStop[] busStops) {
        this.busStops = busStops;
    }

    public BusPathSegment[] getBusPathSegments() {
        return busPathSegments;
    }

    public void setBusPathSegments(BusPathSegment[] busPathSegments) {
        this.busPathSegments = busPathSegments;
    }

    public ArrayList<double[]> getActiveBusLocations() {
        return activeBusLocations;
    }

    public void setActiveBusLocations(ArrayList<double[]> activeBusLocations) {
        this.activeBusLocations = activeBusLocations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
        out.writeByte((byte) (starred ? 1 : 0));
        out.writeTypedArray(busStops, 0);
        out.writeTypedArray(busPathSegments, 0);
        out.writeList(activeBusLocations);
    }

    public static Parcelable.Creator<BusRoute> CREATOR = new Parcelable.Creator<BusRoute>() {
        public BusRoute createFromParcel(Parcel in) {
            return new BusRoute(in);
        }

        public BusRoute[] newArray(int size) {
            return new BusRoute[size];
        }
    };

    @Override
    public int compareTo(@NonNull BusRoute busRoute) {
        if (this == busRoute) {
            return 0;
        }
        return title.compareTo(busRoute.getTitle());
    }
}