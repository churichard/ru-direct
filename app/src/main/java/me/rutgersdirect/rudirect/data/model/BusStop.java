package me.rutgersdirect.rudirect.data.model;

import android.os.Parcel;
import android.os.Parcelable;


public class BusStop implements Parcelable {

    private String tag;
    private String title;
    private int[] times;
    private boolean isExpanded;

    public BusStop(String tag, String title, int[] times) {
        this.tag = tag;
        this.title = title;
        this.times = times;
        this.isExpanded = false;
    }

    private BusStop(Parcel in) {
        tag = in.readString();
        title = in.readString();
        times = in.createIntArray();
        isExpanded = false;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
        out.writeIntArray(times);
    }

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

    public String getTitle() {
        return title;
    }

    public int[] getTimes() {
        return times;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}