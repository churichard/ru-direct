package org.rudirect.android.data.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class BusRouteEdge extends DefaultWeightedEdge {

    private String routeName;
    private String routeTag;
    private String vehicleId;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteTag() {
        return routeTag;
    }

    public void setRouteTag(String routeTag) {
        this.routeTag = routeTag;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
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