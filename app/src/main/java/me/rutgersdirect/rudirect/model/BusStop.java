package me.rutgersdirect.rudirect.model;

public class BusStop {
    public final String tag;
    public final String title;
    public final String times;

    public BusStop(String tag, String title, String times) {
        this.tag = tag;
        this.title = title;
        this.times = times;
    }
}
