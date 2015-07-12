package me.rutgersdirect.rudirect.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;

public class BusStop implements Parcelable, Serializable, Comparable<BusStop> {

    private String tag;
    private String title;
    private int[] times;
    private String latitude;
    private String longitude;

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

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(tag);
        out.writeObject(title);
        out.writeObject(latitude);
        out.writeObject(longitude);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        tag = (String) in.readObject();
        title = (String) in.readObject();
        latitude = (String) in.readObject();
        longitude = (String) in.readObject();
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
    public int compareTo(BusStop busStop) {
        if (this == busStop) {
            return 0;
        }
        return getTitle().compareTo(busStop.getTitle());
    }
}