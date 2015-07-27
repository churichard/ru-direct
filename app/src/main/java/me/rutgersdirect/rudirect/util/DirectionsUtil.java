package me.rutgersdirect.rudirect.util;

import android.util.Log;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.data.model.BusStopTime;

public class DirectionsUtil {

    // The graph of active bus stops
    private static DirectedWeightedPseudograph<BusStop, BusRouteEdge> busStopsGraph;

    // Build the bus stop graph
    public static void setupBusStopsGraph() {
        busStopsGraph = new DirectedWeightedPseudograph<>(BusRouteEdge.class);
        // Add all the active bus stops to the graph
        for (String activeBusTag : AppData.activeBuses) { // Iterate through all the active bus routes
            if (activeBusTag != null) {
                String busName = RUDirectApplication.getBusData().getBusTagToBusTitle().get(activeBusTag);
                BusStop[] busStops = RUDirectApplication.getBusData().getBusTagToBusStops().get(activeBusTag);
                int vehicleId = -1;
                BusStopTime prevTime = null;
                if (busStops[0].isActive()) { // Add the first bus stop
                    busStopsGraph.addVertex(busStops[0]);
                    prevTime = busStops[0].getTimes().get(0);
                    vehicleId = prevTime.getVehicleId();
                }
                for (int i = 1; i < busStops.length; i++) { // Iterate through all the bus stops
                    if (busStops[i].isActive()) { // Add vertex if this bus stop is active
                        busStopsGraph.addVertex(busStops[i]);
                        if (busStops[i - 1].isActive()) { // Add edge if the previous bus stop was active
                            BusRouteEdge edge = busStopsGraph.addEdge(busStops[i - 1], busStops[i]);
                            edge.setRouteName(busName);
                            ArrayList<BusStopTime> busStopTimes = busStops[i].getTimes();
                            BusStopTime nextSmallestTime = null;
                            for (int j = 0; j < busStopTimes.size(); i++) {
                                BusStopTime time = busStopTimes.get(j);
                                if (nextSmallestTime == null && prevTime != null && time.getMinutes() > prevTime.getMinutes()
                                        && time.getVehicleId() != prevTime.getVehicleId()) {
                                    nextSmallestTime = time;
                                }
                                if (vehicleId != -1 && vehicleId == time.getVehicleId()) { // Staying on the same vehicle
                                    edge.setVehicleId(prevTime.getVehicleId());
                                    prevTime = time;
                                    busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes()); // Set edge weight
                                    break;
                                } else if (vehicleId == -1 && prevTime != null) { // Vehicle id hasn't been set yet
                                    vehicleId = time.getVehicleId();
                                    edge.setVehicleId(prevTime.getVehicleId());
                                    prevTime = time;
                                    busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes()); // Set edge weight
                                    break;
                                } else if (nextSmallestTime != null && j == busStopTimes.size() - 1) { // Transfer to another vehicle
                                    vehicleId = nextSmallestTime.getVehicleId();
                                    edge.setVehicleId(vehicleId);
                                    prevTime = time;
                                    busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes()); // Set edge weight
                                }
                            }
                        }
                    }
                }
                if (busStops[busStops.length - 1].isActive() && busStops[0].isActive()) {
                    busStopsGraph.addEdge(busStops[busStops.length - 1], busStops[0]);
                }
            }
        }
    }

    // Calculate the shortest path from the origin to the destination
    public static GraphPath<BusStop, BusRouteEdge> calculateShortestPath(BusStop origin, BusStop destination) throws IllegalArgumentException {
        DijkstraShortestPath<BusStop, BusRouteEdge> shortestPath = new DijkstraShortestPath<>(busStopsGraph, origin, destination);
        return shortestPath.getPath();
    }

    // Calculate the total travel time for the shortest path
    public static double calculateShortestPathTime(DijkstraShortestPath<BusStop, BusRouteEdge> shortestPath) {
        // TODO Add in initial wait time and vehicle transfer times!!!!!!!
        double totalTime = shortestPath.getPath().getWeight();
        return totalTime;
    }

    // Print out the vertices of the bus stops graph and their corresponding edges
    public static void printBusStopsGraph() {
        for (BusStop stop : busStopsGraph.vertexSet()) {
            Log.d(stop.toString(), busStopsGraph.outgoingEdgesOf(stop).toString());
        }
    }
}