package org.rudirect.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BusVehicle implements Parcelable {

    // Location of the vehicle as [latitude, longitude]
    private transient double[] location;
    // Vehicle ID
    private transient String vehicleId;

    public BusVehicle() {
        location = null;
        vehicleId = null;
    }

    public BusVehicle(Parcel in) {
        location = in.createDoubleArray();
        vehicleId = in.readString();
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double latitude, double longitude) {
        this.location = new double[]{latitude, longitude};
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDoubleArray(location);
        out.writeString(vehicleId);
    }

    public static Parcelable.Creator<BusVehicle> CREATOR = new Parcelable.Creator<BusVehicle>() {
        public BusVehicle createFromParcel(Parcel in) {
            return new BusVehicle(in);
        }

        public BusVehicle[] newArray(int size) {
            return new BusVehicle[size];
        }
    };
}