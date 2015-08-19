package me.rutgersdirect.rudirect.data.model;

public class DirectionsItem {

    private int iconId;
    private String title;
    private String time;

    public DirectionsItem(int iconId, String title, String time) {
        this.iconId = iconId;
        this.title = title;
        this.time = time;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}