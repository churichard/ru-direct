package me.rutgersdirect.rudirect.util;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import me.rutgersdirect.rudirect.data.model.BusStop;

public class DirectionsUtil {

    private static DirectedWeightedPseudograph<BusStop, DefaultWeightedEdge> busStopsGraph;

    public static void setupBusStopsGraph() {
        busStopsGraph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);

    }

    public static String[] calculateRoute(String origin, String destination) {
        return null;
    }
}