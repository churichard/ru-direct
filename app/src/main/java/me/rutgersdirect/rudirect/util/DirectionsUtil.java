package me.rutgersdirect.rudirect.util;

import android.util.Log;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.data.model.BusStopTime;

public class DirectionsUtil {

    private static final String TAG = DirectionsUtil.class.getSimpleName();
    public static boolean isReady = false; // Whether or not the bus stop times have been downloaded

    // The graph of active bus stops
    private static DirectedWeightedPseudograph<BusStop, BusRouteEdge> busStopsGraph;
    // Hash map of the bus stops
    private static HashMap<String, ArrayList<BusStop>> busStopsHashMap;

    // Build the bus stop graph
    public static void setupBusStopsGraph() {
        if (isReady) { // Check to see that the bus stop times are all downloaded
            busStopsGraph = new DirectedWeightedPseudograph<>(BusRouteEdge.class);
            busStopsHashMap = new HashMap<>();

            // Add all the active bus stops to the graph
            for (String activeBusTag : AppData.activeBuses) {
                if (activeBusTag != null) {
                    Log.d(TAG, "Active bus tag: " + activeBusTag);
                    String busName = RUDirectApplication.getBusData().getBusTagToBusTitle().get(activeBusTag);
                    BusStop[] busStops = RUDirectApplication.getBusData().getBusTagToBusStops().get(activeBusTag);
                    BusStopTime prevTime = null;

                    // Add vertex if the first bus stop is active
                    if (busStops[0].isActive()) {
                        addVertex(busName, busStops[0]);
                        prevTime = busStops[0].getTimes().get(0);
                    }

                    // Iterate through all the bus stops
                    for (int i = 1; i < busStops.length; i++) {
                        // Add vertex if this bus stop is active
                        if (busStops[i].isActive()) {
                            addVertex(busName, busStops[i]);
                            // Add edge between this bus stop and the previous bus stop if they are both active
                            if (busStops[i - 1].isActive()) {
                                prevTime = addEdge(busName, busStops[i - 1], busStops[i], prevTime);
                            }
                        }
                    }

                    // Add edge from last bus stop to first bus stop
                    if (busStops[busStops.length - 1].isActive() && busStops[0].isActive()) {
                        addEdge(busName, busStops[busStops.length - 1], busStops[0], prevTime);
                    }
                }
            }
        } else {
            Log.e(TAG, "Can't set up bus stops graph!");
        }
    }

    // Adds a weighted edge between two bus stops, giving preference to times with the same vehicle id
    private static BusStopTime addEdge(String busName, BusStop stop1, BusStop stop2, BusStopTime prevTime) {
        // Set previous time if it hasn't been set yet
        if (prevTime == null) {
            prevTime = stop2.getTimes().get(0);
        }

        // Add edge between stop1 and stop2
        BusRouteEdge edge = busStopsGraph.addEdge(stop1, stop2);
        ArrayList<BusStopTime> busStopTimes = stop2.getTimes();
        BusStopTime nextSmallestTime = null;
        int vehicleId = prevTime.getVehicleId();
        edge.setRouteName(busName);

        // Iterate through all the times for the bus stop to get the one with the correct vehicle id
        for (int j = 0; j < busStopTimes.size(); j++) {
            BusStopTime time = busStopTimes.get(j);
            Log.d(TAG, "Stop: " + stop1.getTitle() + ", Vehicle ID: " + time.getVehicleId() + ", Time: " + time.getMinutes());

            // Check to see that the time for this bus stop is greater than the time for the previous bus stop
            if (time.getMinutes() - prevTime.getMinutes() < 0) {
                continue;
            }

            // Adds the next smallest time just in case the same vehicle from before doesn't go to this stop
            if (nextSmallestTime == null && time.getMinutes() > prevTime.getMinutes()
                    && time.getVehicleId() != prevTime.getVehicleId()) {
                nextSmallestTime = time;
            }

            // Staying on the same vehicle
            if (vehicleId == time.getVehicleId()) {
                edge.setVehicleId(vehicleId);
                busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes());
                Log.d(TAG, "Edge (same vehicle): " + (time.getMinutes() - prevTime.getMinutes()));
                return time;
            }
            // Transfer to another vehicle
            else if (nextSmallestTime != null && j == busStopTimes.size() - 1) {
                edge.setVehicleId(nextSmallestTime.getVehicleId());
                busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes());
                Log.d(TAG, "Edge (vehicle transfer): " + (time.getMinutes() - prevTime.getMinutes()));
                return nextSmallestTime;
            }
        }

        // Could not add edge to the graph, e.g. because the times for stop 2 were smaller than the times for stop 1
        busStopsGraph.removeEdge(edge);
        return null;
    }

    // Adds the bus stop to the graph while also handling duplicate bus stops
    private static void addVertex(String busName, BusStop busStop) {
        ArrayList<BusStop> stopsArrayList;

        if (busStopsGraph.containsVertex(busStop)) { // If the bus stop already exists in the graph
            stopsArrayList = busStopsHashMap.get(busStop.getTitle());
            busStop.setId(stopsArrayList.size());
            busStopsGraph.addVertex(busStop);
            for (BusStop stop : stopsArrayList) {
                addEdge(busName, busStop, stop, busStop.getTimes().get(0));
                addEdge(busName, stop, busStop, stop.getTimes().get(0));
            }
            stopsArrayList.add(busStop);
        } else { // If the bus stop doesn't exist in the graph
            busStopsGraph.addVertex(busStop);
            stopsArrayList = new ArrayList<>();
            stopsArrayList.add(busStop);
            busStopsHashMap.put(busStop.getTitle(), stopsArrayList);
        }
    }

    // Calculate the shortest path from the origin to the destination
    public static GraphPath<BusStop, BusRouteEdge> calculateShortestPath(BusStop origin, BusStop destination)
            throws IllegalArgumentException {
        Log.d(TAG, "Vertex set: " + busStopsGraph.vertexSet().toString());
        Log.d(TAG, "Origin: " + origin.getTag() + " " + origin.getTitle());
        Log.d(TAG, "Destination: " + destination.getTag() + " " + destination.getTitle());
        DijkstraShortestPath<BusStop, BusRouteEdge> shortestPath
                = new DijkstraShortestPath<>(busStopsGraph, origin, destination);
        return shortestPath.getPath();
    }

    // Calculate and return the total travel time for the shortest path
    public static double getShortestPathTime(GraphPath<BusStop, BusRouteEdge> shortestPath) {
        // TODO Add in initial wait time and vehicle transfer times!!!!!!!
        // TODO I think vehicle transfer times might already be included? Check this!!!!!!
        double initialWait = shortestPath.getStartVertex().getTimes().get(0).getMinutes();
        return shortestPath.getWeight() + initialWait;
    }

    // Print out the vertices of the bus stops graph and their corresponding edges
    public static void printBusStopsGraph() {
        for (BusStop stop : busStopsGraph.vertexSet()) {
            Log.d(stop.toString(), busStopsGraph.outgoingEdgesOf(stop).toString());
        }
        Log.d(TAG, "Done printing out bus stops graph.");
    }
}