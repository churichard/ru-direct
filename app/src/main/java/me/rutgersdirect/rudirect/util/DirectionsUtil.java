package me.rutgersdirect.rudirect.util;

import android.util.Log;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class DirectionsUtil {

    private static DirectedWeightedPseudograph<BusStop, DefaultWeightedEdge> busStopsGraph;

    // Build the bus stop graph
    public static void setupBusStopsGraph() {
        busStopsGraph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        for (String activeBusTag : AppData.activeBuses) {
            BusStop[] busStops = RUDirectApplication.getBusData().getBusTagToBusStops().get(activeBusTag);
            busStopsGraph.addVertex(busStops[0]);
            for (int i = 1; i < busStops.length; i++) {
                busStopsGraph.addVertex(busStops[i]);
                busStopsGraph.addEdge(busStops[i - 1], busStops[i]);
            }
            busStopsGraph.addEdge(busStops[busStops.length - 1], busStops[0]);
        }
    }

    // Calculate the shortest path from the origin to the destination
    public static String calculateShortestPath(BusStop origin, BusStop destination) throws IllegalArgumentException {
        DijkstraShortestPath<BusStop, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(busStopsGraph, origin, destination);
        String pathEdgeList = shortestPath.getPath().toString();
        Log.d("Shortest path", pathEdgeList);
        return pathEdgeList;
    }

    // Print out the vertices of the bus stops graph and their corresponding edges
    public static void printBusStopsGraph() {
        for (BusStop stop : busStopsGraph.vertexSet()) {
            Log.d(stop.toString(), busStopsGraph.outgoingEdgesOf(stop).toString());
        }
    }
}