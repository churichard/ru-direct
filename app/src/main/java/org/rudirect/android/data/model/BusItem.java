package org.rudirect.android.data.model;

import java.util.ArrayList;

public class BusItem {

    protected String tag;
    protected String title;
    protected boolean starred;
    protected transient ArrayList<BusTime> times;
    protected transient boolean isExpanded;
    protected transient long lastUpdatedTime;

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