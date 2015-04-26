package me.rutgersdirect.rudirect.model;

import java.util.Comparator;

import me.rutgersdirect.rudirect.BusConstants;

public class BusTagComparator implements Comparator<String> {
    public int compare(String tag1, String tag2) {
        if (tag1.equals(tag2)) {
            return 0;
        }
        int tag1Index = -1;
        int tag2Index = -1;
        for (int i = 0; i < BusConstants.allBusTags.length; i++) {
            if (BusConstants.allBusTags[i].equals(tag1)) {
                tag1Index = i;
            }
            else if (BusConstants.allBusTags[i].equals(tag2)) {
                tag2Index = i;
            }
        }
        return tag1Index - tag2Index;
    }
}
