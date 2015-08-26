package org.rudirect.android.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import org.rudirect.android.util.RUDirectUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

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
        // Needed for ormlite
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

    public BusStop[] getBusStops() {
        // Create list of bus stops
        TreeSet<BusStop> busStops = new TreeSet<>(new Comparator<BusStop>() {
            @Override
            public int compare(BusStop stop1, BusStop stop2) {
                if (stop1 == stop2) {
                    return 0;
                }
                return stop1.getTitle().compareTo(stop2.getTitle());
            }
        });
        String[] busTags = RUDirectUtil.mapKeySetToSortedArray(busTagToBusStops);
        for (String busTag : busTags) {
            busStops.addAll(Arrays.asList(busTagToBusStops.get(busTag)));
        }
        return busStops.toArray(new BusStop[busStops.size()]);
    }
}