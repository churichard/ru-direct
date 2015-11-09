package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BusTime implements Parcelable {

    private transient int minutes;
    private transient String vehicleId;

    public BusTime(int minutes, String vehicleId) {
        this.minutes = minutes;
        this.vehicleId = vehicleId;
    }

    private BusTime(Parcel in) {
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

    public static Parcelable.Creator<BusTime> CREATOR = new Parcelable.Creator<BusTime>() {
        public BusTime createFromParcel(Parcel in) {
            return new BusTime(in);
        }

        public BusTime[] newArray(int size) {
            return new BusTime[size];
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