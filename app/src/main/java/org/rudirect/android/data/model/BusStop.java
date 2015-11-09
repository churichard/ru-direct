package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class BusStop extends BusItem implements Parcelable, Serializable, Comparable<BusStop> {

    private static final long serialVersionUID = 1060767342449380984L;
    private transient int id;
    private double latitude;
    private double longitude;
    private ArrayList<BusRoute> busRoutes;

    public BusStop(String tag, String title, ArrayList<BusTime> times, double latitude, double longitude) {
        super(tag, title, times);
        this.id = 0;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BusStop(String title) {
        super(title);
        this.id = 0;
        this.latitude = -1;
        this.longitude = -1;
    }

    @SuppressWarnings("unchecked")
    private BusStop(Parcel in) {
        super(in);
        id = 0;
        latitude = in.readDouble();
        longitude = in.readDouble();
        busRoutes = new ArrayList<>();
        busRoutes = in.readArrayList(BusRoute.class.getClassLoader());
    }

    public BusStop() {
        // Needed by ormlite
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeList(busRoutes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator<BusStop> CREATOR = new Parcelable.Creator<BusStop>() {
        public BusStop createFromParcel(Parcel in) {
            return new BusStop(in);
        }

        public BusStop[] newArray(int size) {
            return new BusStop[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(ArrayList<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
    }

    public ArrayList<BusTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<BusTime> times) {
        this.times = times;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        return title.hashCode() + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BusStop) {
            BusStop other = (BusStop) obj;
            return id == other.getId() && title.equals(other.getTitle());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull BusStop other) {
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