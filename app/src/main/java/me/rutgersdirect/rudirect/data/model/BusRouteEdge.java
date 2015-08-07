package me.rutgersdirect.rudirect.data.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class BusRouteEdge extends DefaultWeightedEdge {

    private String routeName;
    private int vehicleId;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getTravelTime() {
        return getWeight();
    }

    public BusStop getSourceBusStop() {
        return (BusStop) getSource();
    }

    public BusStop getTargetBusStop() {
        return (BusStop) getTarget();
    }

    @Override
    public String toString() {
        return "Route name: " + routeName
                + "\nVehicle ID: " + vehicleId
                + "\nTime: " + getWeight()
                + "\nEdge: " + super.toString();
    }
}