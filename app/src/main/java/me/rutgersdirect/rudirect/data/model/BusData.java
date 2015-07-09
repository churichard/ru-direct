package me.rutgersdirect.rudirect.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.HashMap;

public class BusData {

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> busTagsToBusTitles;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> busTitlesToBusTags;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> busTagsToStopTags;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> busTagsToStopTitles;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> stopTitlesToStopTags;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> busTagToStopLatitudes;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[]> busTagToStopLongitudes;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[][]> busTagToPathLatitudes;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String[][]> busTagToPathLongitudes;

    public BusData() {
        busTagsToBusTitles = null;
        busTitlesToBusTags = null;
        busTagsToStopTags = null;
        busTagsToStopTitles = null;
        stopTitlesToStopTags = null;
        busTagToStopLatitudes = null;
        busTagToStopLongitudes = null;
        busTagToPathLatitudes = null;
        busTagToPathLongitudes = null;
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

    public HashMap<String, String[]> getBusTagsToStopTags() {
        return busTagsToStopTags;
    }

    public void setBusTagsToStopTags(HashMap<String, String[]> busTagsToStopTags) {
        this.busTagsToStopTags = busTagsToStopTags;
    }

    public HashMap<String, String[]> getBusTagsToStopTitles() {
        return busTagsToStopTitles;
    }

    public void setBusTagsToStopTitles(HashMap<String, String[]> busTagsToStopTitles) {
        this.busTagsToStopTitles = busTagsToStopTitles;
    }

    public HashMap<String, String[]> getStopTitlesToStopTags() {
        return stopTitlesToStopTags;
    }

    public void setStopTitlesToStopTags(HashMap<String, String[]> stopTitlesToStopTags) {
        this.stopTitlesToStopTags = stopTitlesToStopTags;
    }

    public HashMap<String, String[]> getBusTagToStopLatitudes() {
        return busTagToStopLatitudes;
    }

    public void setBusTagToStopLatitudes(HashMap<String, String[]> busTagToStopLatitudes) {
        this.busTagToStopLatitudes = busTagToStopLatitudes;
    }

    public HashMap<String, String[]> getBusTagToStopLongitudes() {
        return busTagToStopLongitudes;
    }

    public void setBusTagToStopLongitudes(HashMap<String, String[]> busTagToStopLongitudes) {
        this.busTagToStopLongitudes = busTagToStopLongitudes;
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