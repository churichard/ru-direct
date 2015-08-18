package me.rutgersdirect.rudirect.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class BusStop implements Parcelable, Serializable, Comparable<BusStop> {

    private static final long serialVersionUID = 1060767342449380984L;
    private transient int id;
    private String tag;
    private String title;
    private String latitude;
    private String longitude;
    private transient ArrayList<BusStopTime> times;
    private boolean isExpanded;

    public BusStop(String tag, String title, ArrayList<BusStopTime> times, String latitude, String longitude) {
        this.id = 0;
        this.tag = tag;
        this.title = title;
        this.times = times;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isExpanded = false;
    }

    private BusStop(Parcel in) {
        id = 0;
        tag = in.readString();
        title = in.readString();
        times = new ArrayList<>();
        times = in.readArrayList(BusStopTime.class.getClassLoader());
        latitude = in.readString();
        longitude = in.readString();
        isExpanded = false;
    }

    public BusStop(BusStop busStop) {
        id = 0;
        tag = busStop.getTag();
        title = busStop.getTitle();
        times = new ArrayList<>();
        for (BusStopTime time : busStop.getTimes()) {
            times.add(new BusStopTime(time));
        }
        latitude = busStop.getLatitude();
        longitude = busStop.getLongitude();
        isExpanded = false;
    }

    public BusStop() {
        // Needed by ormlite
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
        out.writeList(times);
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

    public ArrayList<BusStopTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<BusStopTime> times) {
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

    public boolean isActive() {
        return (times != null) && !(times.size() == 1 && times.get(0).getMinutes() == -1);
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        return title.hashCode();
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
    public int compareTo(BusStop busStop) {
        if (this == busStop) {
            return 0;
        }
        return title.compareTo(busStop.getTitle());
    }
}