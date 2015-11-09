package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class BusRoute extends BusItem implements Parcelable, Serializable, Comparable<BusRoute> {

    private static final long serialVersionUID = 1632593783571404823L;
    private BusStop[] busStops;
    private BusPathSegment[] busPathSegments;
    private transient ArrayList<double[]> activeBusLocations;

    public BusRoute(String tag, String title) {
        super(tag, title);
        this.busStops = null;
        this.busPathSegments = null;
        this.activeBusLocations = null;
    }

    public BusRoute(String title) {
        super(title);
        this.tag = null;
        this.busStops = null;
        this.busPathSegments = null;
        this.activeBusLocations = null;
    }

    @SuppressWarnings("unchecked")
    private BusRoute(Parcel in) {
        super(in);
        busStops = in.createTypedArray(BusStop.CREATOR);
        busPathSegments = in.createTypedArray(BusPathSegment.CREATOR);
        activeBusLocations = new ArrayList<>();
        activeBusLocations = in.readArrayList(double[].class.getClassLoader());
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

    public ArrayList<BusTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<BusTime> times) {
        this.times = times;
    }

    public ArrayList<double[]> getActiveBusLocations() {
        return activeBusLocations;
    }

    public void setActiveBusLocations(ArrayList<double[]> activeBusLocations) {
        this.activeBusLocations = activeBusLocations;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
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
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BusRoute) {
            BusRoute other = (BusRoute) obj;
            return title.equals(other.getTitle());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull BusRoute other) {
        if (this == other) {
            return 0;
        } else if (isStarred() && !other.isStarred()) {
            return -1;
        } else if (!isStarred() && other.isStarred()) {
            return 1;
        } else {
            return title.compareTo(other.getTitle());
        }
    }
}