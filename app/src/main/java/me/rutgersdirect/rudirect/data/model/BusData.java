package me.rutgersdirect.rudirect.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.HashMap;

public class BusData {

    @DatabaseField(id = true)
    private final int ID = 9000;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> busTagToBusTitle;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> busTitleToBusTag;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BusStop[]> busTagToBusStops;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BusPathSegment[]> busTagToBusPathSegments;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> stopTitleToStopTags;

    public BusData() {
    }

    public int getID() {
        return ID;
    }

    public HashMap<String, String> getBusTagToBusTitle() {
        return busTagToBusTitle;
    }

    public void setBusTagToBusTitle(HashMap<String, String> busTagToBusTitle) {
        this.busTagToBusTitle = busTagToBusTitle;
    }

    public HashMap<String, String> getBusTitleToBusTag() {
        return busTitleToBusTag;
    }

    public void setBusTitleToBusTag(HashMap<String, String> busTitleToBusTag) {
        this.busTitleToBusTag = busTitleToBusTag;
    }

    public HashMap<String, BusStop[]> getBusTagToBusStops() {
        return busTagToBusStops;
    }

    public void setBusTagToBusStops(HashMap<String, BusStop[]> busTagToBusStops) {
        this.busTagToBusStops = busTagToBusStops;
    }

    public HashMap<String, String[]> getStopTitleToStopTags() {
        return stopTitleToStopTags;
    }

    public void setStopTitleToStopTags(HashMap<String, String[]> stopTitleToStopTags) {
        this.stopTitleToStopTags = stopTitleToStopTags;
    }

    public HashMap<String, BusPathSegment[]> getBusTagToBusPathSegments() {
        return busTagToBusPathSegments;
    }

    public void setBusTagToBusPathSegments(HashMap<String, BusPathSegment[]> busTagToBusPathSegments) {
        this.busTagToBusPathSegments = busTagToBusPathSegments;
    }
}