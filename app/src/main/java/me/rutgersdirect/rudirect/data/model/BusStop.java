package me.rutgersdirect.rudirect.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BusStop implements Parcelable, Serializable, Comparable<BusStop> {

    private String tag;
    private String title;
    private String latitude;
    private String longitude;
    private transient int[] times;

    private boolean isExpanded;

    public BusStop(String tag, String title, int[] times, String latitude, String longitude) {
        this.tag = tag;
        this.title = title;
        this.times = times;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isExpanded = false;
    }

    private BusStop(Parcel in) {
        tag = in.readString();
        title = in.readString();
        times = in.createIntArray();
        latitude = in.readString();
        longitude = in.readString();
        isExpanded = false;
    }

    public BusStop() {
        // Needed by ormlite
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
        out.writeIntArray(times);
        out.writeString(latitude);
        out.writeString(longitude);
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

    public int[] getTimes() {
        return times;
    }

    public void setTimes(int[] times) {
        this.times = times;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BusStop) {
            return getTitle().equals(((BusStop) obj).getTitle());
        }
        return false;
    }

    @Override
    public int compareTo(BusStop busStop) {
        if (this == busStop) {
            return 0;
        }
        return getTitle().compareTo(busStop.getTitle());
    }
}