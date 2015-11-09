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
        this.id = 0;
        this.tag = tag;
        this.title = title;
        this.times = times;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isExpanded = false;
    }

    public BusStop(String title) {
        this.id = 0;
        this.tag = null;
        this.title = title;
        this.times = null;
        this.latitude = -1;
        this.longitude = -1;
        this.isExpanded = false;
    }

    private BusStop(Parcel in) {
        id = 0;
        tag = in.readString();
        title = in.readString();
        times = in.createTypedArrayList(BusTime.CREATOR);
        latitude = in.readDouble();
        longitude = in.readDouble();
        isExpanded = false;
    }

    public BusStop() {
        // Needed by ormlite
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
        out.writeTypedList(times);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
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