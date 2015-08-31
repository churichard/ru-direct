package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BusPathSegment implements Parcelable, Serializable {

    private static final long serialVersionUID = 1378582749017485234L;
    private double[] latitudes;
    private double[] longitudes;

    public BusPathSegment(double[] latitudes, double[] longitudes) {
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public BusPathSegment(Parcel in) {
        latitudes = in.createDoubleArray();
        longitudes = in.createDoubleArray();
    }

    public BusPathSegment() {
        // Needed for ormlite
    }

    public double[] getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(double[] latitudes) {
        this.latitudes = latitudes;
    }

    public double[] getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(double[] longitudes) {
        this.longitudes = longitudes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDoubleArray(latitudes);
        out.writeDoubleArray(longitudes);
    }

    public static Parcelable.Creator<BusPathSegment> CREATOR = new Parcelable.Creator<BusPathSegment>() {
        public BusPathSegment createFromParcel(Parcel in) {
            return new BusPathSegment(in);
        }

        public BusPathSegment[] newArray(int size) {
            return new BusPathSegment[size];
        }
    };
}