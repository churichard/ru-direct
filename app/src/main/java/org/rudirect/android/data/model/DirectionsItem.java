package org.rudirect.android.data.model;

public class DirectionsItem {

    private int itemType;
    private String title;
    private String tag;
    private String time;
    private int iconId;

    public DirectionsItem(int itemType, String title, String tag, String time, int iconId) {
        this.itemType = itemType;
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.iconId = iconId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
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