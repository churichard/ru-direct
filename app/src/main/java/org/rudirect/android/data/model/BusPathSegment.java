package org.rudirect.android.data.model;

import java.io.Serializable;

public class BusPathSegment implements Serializable {

    private String[] latitudes;
    private String[] longitudes;

    public BusPathSegment(String[] latitudes, String[] longitudes) {
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public BusPathSegment() {
        // Needed for ormlite
    }

    public String[] getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(String[] latitudes) {
        this.latitudes = latitudes;
    }

    public String[] getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(String[] longitudes) {
        this.longitudes = longitudes;
    }
}