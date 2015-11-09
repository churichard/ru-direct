package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class BusItem implements Parcelable, Serializable {

    protected String tag;
    protected String title;
    protected boolean starred;
    protected transient ArrayList<BusTime> times;
    protected transient boolean isExpanded;
    protected transient long lastUpdatedTime;

    protected BusItem(String tag, String title, ArrayList<BusTime> times) {
        this.tag = tag;
        this.title = title;
        this.times = times;
        this.starred = false;
        this.isExpanded = false;
    }

    protected BusItem(String tag, String title) {
        this.tag = tag;
        this.title = title;
        this.times = null;
        this.starred = false;
        this.isExpanded = false;
    }

    protected BusItem(String title) {
        this.title = title;
        this.tag = null;
        this.times = null;
        this.starred = false;
        this.isExpanded = false;
    }

    protected BusItem(Parcel in) {
        tag = in.readString();
        title = in.readString();
        starred = in.readByte() != 0;
        isExpanded = false;
    }

    protected BusItem() {}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
        out.writeByte((byte) (starred ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public boolean isActive() {
        return (times != null) && !(times.size() == 1 && times.get(0).getMinutes() == -1);
    }
}