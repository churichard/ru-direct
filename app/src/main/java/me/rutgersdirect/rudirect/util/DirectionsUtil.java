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
                    int vehicleId = -1;
                    BusStopTime prevTime = null;

                    // Add the first bus stop
                    if (busStops[0].isActive()) {
                        Log.d(TAG, "0th bus stop: " + busStops[0].getTitle());
                        ArrayList<BusStop> stopsArrayList;

                        // Check for duplicate bus stops
                        if (busStopsGraph.containsVertex(busStops[0])) {
                            // TODO Add edges between this vertex and every other vertex for this bus stop
                            stopsArrayList = busStopsHashMap.get(busStops[0].getTitle());
                            stopsArrayList.add(busStops[0]);
                        } else {
                            busStopsGraph.addVertex(busStops[0]);
                            stopsArrayList = new ArrayList<>();
                            stopsArrayList.add(busStops[0]);
                            busStopsHashMap.put(busStops[0].getTitle(), stopsArrayList);
                        }

                        // Set prevTime and vehicle id
                        prevTime = busStops[0].getTimes().get(0);
                        vehicleId = prevTime.getVehicleId();
                    }

                    // Iterate through all the bus stops
                    for (int i = 1; i < busStops.length; i++) {
                        // Add vertex if this bus stop is active
                        if (busStops[i].isActive()) {
                            Log.d(TAG, i + "th bus stop: " + busStops[i].getTitle());
                            ArrayList<BusStop> stopsArrayList;

                            // Check for duplicate bus stops
                            if (busStopsGraph.containsVertex(busStops[i])) {
                                // TODO Add edges between this vertex and every other vertex for this bus stop
                                stopsArrayList = busStopsHashMap.get(busStops[i].getTitle());
                                stopsArrayList.add(busStops[i]);
                            } else {
                                busStopsGraph.addVertex(busStops[i]);
                                stopsArrayList = new ArrayList<>();
                                stopsArrayList.add(busStops[i]);
                                busStopsHashMap.put(busStops[i].getTitle(), stopsArrayList);
                            }

                            // Add edge if both this bus stop and the previous bus stop are active
                            if (busStops[i - 1].isActive()) {
                                BusRouteEdge edge = busStopsGraph.addEdge(busStops[i - 1], busStops[i]);
                                edge.setRouteName(busName);
                                ArrayList<BusStopTime> busStopTimes = busStops[i].getTimes();
                                BusStopTime nextSmallestTime = null;

                                // Iterate through all the times for the bus stop to get the one with the correct vehicle id
                                for (int j = 0; j < busStopTimes.size(); j++) {
                                    BusStopTime time = busStopTimes.get(j);
                                    Log.d(TAG, "Bus stop time: " + time.getVehicleId() + " " + time.getMinutes());

                                    // Check to see that the time for this bus stop is greater than the time for the previous bus stop
                                    if (prevTime != null && (time.getMinutes() - prevTime.getMinutes() < 0)) {
                                        continue;
                                    }

                                    // Adds the next smallest time just in case the same vehicle from before doesn't go to this stop
                                    if (nextSmallestTime == null && prevTime != null && time.getMinutes() > prevTime.getMinutes()
                                            && time.getVehicleId() != prevTime.getVehicleId()) {
                                        Log.d(TAG, "Next smallest time: " + time.getVehicleId() + " " + time.getMinutes());
                                        nextSmallestTime = time;
                                    }

                                    // Staying on the same vehicle
                                    if (vehicleId != -1 && vehicleId == time.getVehicleId()) {
                                        edge.setVehicleId(prevTime.getVehicleId());
                                        busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes());
                                        Log.d(TAG, "Edge weight (1st): " + (time.getMinutes() - prevTime.getMinutes()));
                                        prevTime = time;
                                        break;
                                    }
                                    // Vehicle id hasn't been set yet
                                    else if (vehicleId == -1 && prevTime != null) {
                                        vehicleId = time.getVehicleId();
                                        edge.setVehicleId(prevTime.getVehicleId());
                                        busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes());
                                        Log.d(TAG, "Edge weight (2nd): " + (time.getMinutes() - prevTime.getMinutes()));
                                        prevTime = time;
                                        break;
                                    }
                                    // Transfer to another vehicle
                                    else if (nextSmallestTime != null && j == busStopTimes.size() - 1) {
                                        vehicleId = nextSmallestTime.getVehicleId();
                                        edge.setVehicleId(vehicleId);
                                        busStopsGraph.setEdgeWeight(edge, time.getMinutes() - prevTime.getMinutes());
                                        Log.d(TAG, "Edge weight (3rd): " + (time.getMinutes() - prevTime.getMinutes()));
                                        prevTime = time;
                                    }
                                }
                                Log.d(TAG, i + "th edge: " + edge);
                            }
                        }
                    }

                    // Add edge from last bus stop to first bus stop
                    // TODO Check to see that they are the same vehicle
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