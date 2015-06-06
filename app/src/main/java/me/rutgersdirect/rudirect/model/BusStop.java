package me.rutgersdirect.rudirect.model;

import android.os.Parcel;
import android.os.Parcelable;


public class BusStop implements Parcelable {

    private String tag;
    private String title;
    private int[] times;

    public BusStop(String tag, String title, int[] times) {
        this.tag = tag;
        this.title = title;
        this.times = times;
    }

    private BusStop(Parcel in) {
        tag = in.readString();
        title = in.readString();
        times = in.createIntArray();
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
}