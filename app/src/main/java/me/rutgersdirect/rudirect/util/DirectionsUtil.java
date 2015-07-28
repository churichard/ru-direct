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

    private static final String TAG = DirectionsUtil.class.getSimpleName();
    public static boolean isReady = false;

    // The graph of active bus stops
    private static DirectedWeightedPseudograph<BusStop, BusRouteEdge> busStopsGraph;

    // Build the bus stop graph
    public static void setupBusStopsGraph() {
        if (isReady) {
            busStopsGraph = new DirectedWeightedPseudograph<>(BusRouteEdge.class);
            // Add all the active bus stops to the graph
            for (String activeBusTag : AppData.activeBuses) { // Iterate through all the active bus routes
                if (activeBusTag != null) {
                    Log.d(TAG, "Active bus tag: " + activeBusTag);
                    String busName = RUDirectApplication.getBusData().getBusTagToBusTitle().get(activeBusTag);
                    BusStop[] busStops = RUDirectApplication.getBusData().getBusTagToBusStops().get(activeBusTag);
                    int vehicleId = -1;
                    BusStopTime prevTime = null;
                    if (busStops[0].isActive()) { // Add the first bus stop
                        Log.d(TAG, "0th bus stop: " + busStops[0].getTitle());
                        if (busStopsGraph.containsVertex(busStops[0])) {
                            // TODO Add edges between this vertex and every other vertex for this bus stop
                        } else {
                            busStopsGraph.addVertex(busStops[0]);
                        }
                        prevTime = busStops[0].getTimes().get(0);
                        vehicleId = prevTime.getVehicleId();
                    }
                    for (int i = 1; i < busStops.length; i++) { // Iterate through all the bus stops
                        if (busStops[i].isActive()) { // Add vertex if this bus stop is active
                            Log.d(TAG, i + "th bus stop: " + busStops[i].getTitle());
                            if (busStopsGraph.containsVertex(busStops[i])) {
                                // TODO Add edges between this vertex and every other vertex for this bus stop
                            } else {
                                busStopsGraph.addVertex(busStops[i]);
                            }
                            if (busStops[i - 1].isActive()) { // Add edge if the previous bus stop was active
                                BusRouteEdge edge = busStopsGraph.addEdge(busStops[i - 1], busStops[i]);
                                edge.setRouteName(busName);
                                ArrayList<BusStopTime> busStopTimes = busStops[i].getTimes();
                                BusStopTime nextSmallestTime = null;
                                for (int j = 0; j < busStopTimes.size(); j++) {
                                    BusStopTime time = busStopTimes.get(j);
                                    if (prevTime != null && (time.getMinutes() - prevTime.getMinutes() < 0)) {
                                        continue;
                                    }
                                    Log.d(TAG, "Bus stop time: " + time.getVehicleId() + " " + time.getMinutes());
                                    if (nextSmallestTime == null && prevTime != null && time.getMinutes() > prevTime.getMinutes()
                                            && time.getVehicleId() != prevTime.getVehicleId()) {
                                        Log.d(TAG, "Next smallest time: " + time.getVehicleId() + " " + time.getMinutes());
                                        nextSmallestTime = time;
                                    }
                                    if (vehicleId != -1 && vehicleId == time.getVehicleId()) { // Staying on the same vehicle
                                        edge.setVehicleId(prevTime.getVehicleId());
                                        busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes()); // Set edge weight
                                        Log.d(TAG, "Edge weight (1st): " + (time.getMinutes() - prevTime.getMinutes()));
                                        prevTime = time;
                                        break;
                                    } else if (vehicleId == -1 && prevTime != null) { // Vehicle id hasn't been set yet
                                        vehicleId = time.getVehicleId();
                                        edge.setVehicleId(prevTime.getVehicleId());
                                        busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes()); // Set edge weight
                                        Log.d(TAG, "Edge weight (2nd): " + (time.getMinutes() - prevTime.getMinutes()));
                                        prevTime = time;
                                        break;
                                    } else if (nextSmallestTime != null && j == busStopTimes.size() - 1) { // Transfer to another vehicle
                                        vehicleId = nextSmallestTime.getVehicleId();
                                        edge.setVehicleId(vehicleId);
                                        busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes()); // Set edge weight
                                        Log.d(TAG, "Edge weight (3rd): " + (time.getMinutes() - prevTime.getMinutes()));
                                        prevTime = time;
                                    }
                                }
                                Log.d(TAG, i + "th edge: " + edge);
                            }
                        }
                    }
                    if (busStops[busStops.length - 1].isActive() && busStops[0].isActive()) {
                        busStopsGraph.addEdge(busStops[busStops.length - 1], busStops[0]);
                    }
                }
            }
        } else {
            Log.e(TAG, "Can't set up bus stops graph!");
        }
    }

    // Calculate the shortest path from the origin to the destination
    public static GraphPath<BusStop, BusRouteEdge> calculateShortestPath(BusStop origin, BusStop destination) throws IllegalArgumentException {
        Log.d(TAG, "Vertex set: " + busStopsGraph.vertexSet().toString());
        Log.d(TAG, "Origin: " + origin.getTag() + " " + origin.getTitle());
        Log.d(TAG, "Destination: " + destination.getTag() + " " + destination.getTitle());
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
        Log.d(TAG, "Done printing out bus stops graph.");
    }
}