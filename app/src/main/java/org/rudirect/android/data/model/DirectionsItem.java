package org.rudirect.android.data.model;

public class DirectionsItem {

    private String title;
    private String tag;
    private String time;
    private int iconId;

    public DirectionsItem(String title, String tag, String time, int iconId) {
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}