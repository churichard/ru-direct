package me.rutgersdirect.rudirect.model;

public class BusStop {
    public final String tag;
    public final String title;
    public final int[] times;

    public BusStop(String tag, String title, int[] times) {
        this.tag = tag;
        this.title = title;
        this.times = times;
    }
}
