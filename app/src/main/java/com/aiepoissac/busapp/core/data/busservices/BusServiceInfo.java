/**
 * Each instance of this class holds information of a bus service
 * @author Li Yi Cheng
 */

package com.aiepoissac.busapp.core.data.busservices;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public final class BusServiceInfo implements Comparable<BusServiceInfo> {
    //this maps each bus number to all bus services associated with it
    private static final HashMap<String, ArrayList<BusServiceInfo>> busServices = new HashMap<>();

    private final String serviceNo;

    private final int direction;

    private final String operator;

    private final String category;

    private final int originCode;

    private final int destinationCode;

    private final String loopDescription;

    private final String AM_Peak_Freq;

    private final String AM_Offpeak_Freq;

    private final String PM_Peak_Freq;

    private final String PM_Offpeak_Freq;

    /**
     * Initialise the data
     */
    public static void initialise() {
        int count = 0;
        while(true) {
            ArrayList<JSONObject> jsonArray = DataConnection.getData("BUS SERVICES", count);
            if (jsonArray.isEmpty()) {
                break;
            } else {
                for (JSONObject jsonObject2 : jsonArray) {
                    BusServiceInfo busServiceInfo = new BusServiceInfo(jsonObject2);
                    if (BusServiceInfo.busServices.get(busServiceInfo.serviceNo) == null) {
                        ArrayList<BusServiceInfo> busServiceInfos = new ArrayList<>();
                        busServiceInfos.add(busServiceInfo);
                        BusServiceInfo.busServices.put(busServiceInfo.serviceNo, busServiceInfos);
                    } else {
                        BusServiceInfo.busServices.get(busServiceInfo.serviceNo).add(busServiceInfo);
                    }
                }
                count = count + 500;
            }
        }

        BusRoutesInfo.initialise();
    }

    /**
     * Get the bus service using the bus number and direction
     * @param serviceNo bus number
     * @param direction direction
     * @return the bus service with that bus number and direction
     */
    static BusServiceInfo getBusServiceInfo(String serviceNo, int direction) {
        ArrayList<BusServiceInfo> busServiceInfos = BusServiceInfo.busServices.get(serviceNo);
        if (busServiceInfos != null) {
            for (BusServiceInfo busServiceInfo : busServiceInfos) {
                if (direction == busServiceInfo.direction) {
                    return busServiceInfo;
                }
            }
        }
        return null;
    }

    /**
     * Returns a String containing the details of this bus service, as well as the full route
     * @param serviceNo bus number
     * @param showFirstLastBusTiming
     * @return String
     */
    public static String getBusServiceInfoString(String serviceNo, boolean showFirstLastBusTiming) {
        ArrayList<BusServiceInfo> busServiceInfos = BusServiceInfo.busServices.get(serviceNo);
        if (busServiceInfos != null) {
            StringBuilder s = new StringBuilder();
            for (BusServiceInfo busServiceInfo : busServiceInfos) {
                s.append(busServiceInfo);
                s.append("\n");
                s.append(busServiceInfo.getBusFrequencyString());
                s.append("\n");
                ArrayList<BusRoutesInfo> busRoutesInfos = BusRoutesInfo.getBusRoutesInfo(busServiceInfo);
                for (BusRoutesInfo busRoutesInfo : busRoutesInfos) {
                    s.append(busRoutesInfo);
                    s.append("\n");
                    if (showFirstLastBusTiming) {
                        s.append(busRoutesInfo.getFirstLastBusTimingString());
                        s.append("\n");
                    }
                }
                s.append("\n");
            }
            return s.toString();
        } else {
            return null;
        }
    }

    private BusServiceInfo(final JSONObject data) {
        this.serviceNo = (String) data.get("ServiceNo");
        this.operator = (String) data.get("Operator");
        this.direction = Math.toIntExact((Long) data.get("Direction"));
        this.category = (String) data.get("Category");
        this.originCode = Integer.parseInt((String) data.get("OriginCode"));
        this.destinationCode = Integer.parseInt((String) data.get("DestinationCode"));
        this.loopDescription = (String) data.get("LoopDesc");
        this.AM_Peak_Freq = (String) data.get("AM_Peak_Freq");
        this.AM_Offpeak_Freq = (String) data.get("AM_Offpeak_Freq");
        this.PM_Peak_Freq = (String) data.get("PM_Peak_Freq");
        this.PM_Offpeak_Freq = (String) data.get("PM_Offpeak_Freq");
    }

    /**
     * Returns a String containing the Bus Service Number, Operator, Origin and Destination stop
     * @return String
     */
    @Override
    public String toString() {
        if (isLoop()) {
            return String.format("Service No: %s %s\nOperator: %s\n%s to %s\nLoop at: %s",
                    this.category, this.serviceNo, this.operator, BusStopInfo.getBusStopInfo(this.originCode),
                    BusStopInfo.getBusStopInfo(this.destinationCode), this.loopDescription);
        } else {
            return String.format("Service No: %s %s\nOperator: %s\n%s to %s\nDirection: %d",
                    this.category, this.serviceNo, this.operator, BusStopInfo.getBusStopInfo(this.originCode),
                    BusStopInfo.getBusStopInfo(this.destinationCode), this.direction);
        }
    }

    /**
     * Returns a String containing the Bus Frequency data
     * @return String
     */
    public String getBusFrequencyString() {
        return String.format(
                "0630H - 0830H: %s min\n0831H - 1659H: %s min\n1700H - 1900H: %s min\nAfter 1900H: %s min",
                this.AM_Peak_Freq, this.AM_Offpeak_Freq, this.PM_Peak_Freq, this.PM_Offpeak_Freq);
    }

    /**
     * Returns the bus number
     * @return the bus number
     */
    public String getServiceNo() {
        return this.serviceNo;
    }

    /**
     * Checks if the bus is a loop service
     * @return true if the bus is a loop service
     */
    public boolean isLoop() {
        return this.loopDescription != null && !this.loopDescription.isEmpty();
    }

    /**
     * Compares 2 bus services using the bus number and direction
     * @param other the object to be compared.
     * @return -1, 0 or 1 if this bus service is smaller than, equals, or greater than the other bus service
     * respectively
     */
    @Override
    public int compareTo(BusServiceInfo other) {
        int compared = this.serviceNo.compareTo(other.serviceNo);
        return compared == 0 ? Integer.compare(this.direction, other.direction) : compared;
    }
}
