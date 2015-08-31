package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BusStopTime implements Parcelable {

    private transient int minutes;
    private transient String vehicleId;

    public BusStopTime(int minutes, String vehicleId) {
        this.minutes = minutes;
        this.vehicleId = vehicleId;
    }

    public BusStopTime(int minutes) {
        this.minutes = minutes;
        this.vehicleId = null;
    }

    private BusStopTime(Parcel in) {
        minutes = in.readInt();
        vehicleId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(minutes);
        out.writeString(vehicleId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator<BusStopTime> CREATOR = new Parcelable.Creator<BusStopTime>() {
        public BusStopTime createFromParcel(Parcel in) {
            return new BusStopTime(in);
        }

        public BusStopTime[] newArray(int size) {
            return new BusStopTime[size];
        }
    };

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}