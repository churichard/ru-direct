package me.rutgersdirect.rudirect.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BusStopTime implements Parcelable {

    private int minutes;
    private int vehicleId;

    public BusStopTime(int minutes, int vehicleId) {
        this.minutes = minutes;
        this.vehicleId = vehicleId;
    }

    public BusStopTime(int minutes) {
        this.minutes = minutes;
        this.vehicleId = -1;
    }

    public BusStopTime(BusStopTime time) {
        this.minutes = time.getMinutes();
        this.vehicleId = time.getVehicleId();
    }

    private BusStopTime(Parcel in) {
        minutes = in.readInt();
        vehicleId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(minutes);
        out.writeInt(vehicleId);
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

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
}