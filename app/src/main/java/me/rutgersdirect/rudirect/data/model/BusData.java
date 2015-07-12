package me.rutgersdirect.rudirect.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.HashMap;

public class BusData {

    @DatabaseField(id = true)
    private final int ID = 9000;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> busTagsToBusTitles;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> busTitlesToBusTags;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> stopTitlesToStopTags;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[][]> busTagToPathLatitudes;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[][]> busTagToPathLongitudes;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BusStop[]> busTagToBusStops;

    public BusData() {
    }

    public int getID() {
        return ID;
    }

    public HashMap<String, String> getBusTagsToBusTitles() {
        return busTagsToBusTitles;
    }

    public void setBusTagsToBusTitles(HashMap<String, String> busTagsToBusTitles) {
        this.busTagsToBusTitles = busTagsToBusTitles;
    }

    public HashMap<String, String> getBusTitlesToBusTags() {
        return busTitlesToBusTags;
    }

    public void setBusTitlesToBusTags(HashMap<String, String> busTitlesToBusTags) {
        this.busTitlesToBusTags = busTitlesToBusTags;
    }

    public HashMap<String, BusStop[]> getBusTagToBusStops() {
        return busTagToBusStops;
    }

    public void setBusTagToBusStops(HashMap<String, BusStop[]> busTagToBusStops) {
        this.busTagToBusStops = busTagToBusStops;
    }

    public HashMap<String, String[]> getStopTitlesToStopTags() {
        return stopTitlesToStopTags;
    }

    public void setStopTitlesToStopTags(HashMap<String, String[]> stopTitlesToStopTags) {
        this.stopTitlesToStopTags = stopTitlesToStopTags;
    }

    public HashMap<String, String[][]> getBusTagToPathLatitudes() {
        return busTagToPathLatitudes;
    }

    public void setBusTagToPathLatitudes(HashMap<String, String[][]> busTagToPathLatitudes) {
        this.busTagToPathLatitudes = busTagToPathLatitudes;
    }

    public HashMap<String, String[][]> getBusTagToPathLongitudes() {
        return busTagToPathLongitudes;
    }

    public void setBusTagToPathLongitudes(HashMap<String, String[][]> busTagToPathLongitudes) {
        this.busTagToPathLongitudes = busTagToPathLongitudes;
    }
}