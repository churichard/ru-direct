package me.rutgersdirect.rudirect.data.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class BusRouteEdge extends DefaultWeightedEdge {

    private String routeName;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public String toString() {
        return routeName + ": " + super.toString();
    }
}