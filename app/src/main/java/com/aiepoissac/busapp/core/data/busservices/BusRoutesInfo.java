/**
 * Each instance of this class holds information of a bus service at a particular bus stop
 * @author Li Yi Cheng
 */

package com.aiepoissac.busapp.core.data.busservices;


import org.json.simple.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

final class BusRoutesInfo {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("HHmm");
    //this maps each bus service to the full bus route
    private static final HashMap<BusServiceInfo, ArrayList<BusRoutesInfo>> routes = new HashMap<>();
    //this maps each bus stop code to the bus services serving this bus stop
    private static final HashMap<Integer, ArrayList<BusRoutesInfo>> routesAtBusStops = new HashMap<>();

    private final String serviceNo;

    private final int direction;

    private final int stopSequence;

    private final int busStopCode;

    private final double distance;

    private final LocalTime WD_FirstBus;

    private final LocalTime WD_LastBus;

    private final LocalTime SAT_FirstBus;

    private final LocalTime SAT_LastBus;

    private final LocalTime SUN_FirstBus;

    private final LocalTime SUN_LastBus;

    /**
     * Initialise the data
     */
    static void initialise() {
        int count = 0;
        while(true) {
            ArrayList<JSONObject> jsonArray = DataConnection.getData("BUS ROUTES", count);
            if (jsonArray.isEmpty()) {
                break;
            } else {
                for (JSONObject jsonObject2 : jsonArray) {
                    BusRoutesInfo busRoutesInfo = new BusRoutesInfo(jsonObject2);
                    BusServiceInfo busServiceInfo =
                            BusServiceInfo.getBusServiceInfo(busRoutesInfo.serviceNo, busRoutesInfo.direction);
                    if (!BusRoutesInfo.routes.containsKey(busServiceInfo)) {
                        ArrayList<BusRoutesInfo> busRoutesInfos = new ArrayList<>();
                        busRoutesInfos.add(busRoutesInfo);
                        BusRoutesInfo.routes.put(busServiceInfo, busRoutesInfos);
                    } else {
                        BusRoutesInfo.routes.get(busServiceInfo).add(busRoutesInfo);
                    }

                    if (!BusRoutesInfo.routesAtBusStops.containsKey(busRoutesInfo.busStopCode)) {
                        ArrayList<BusRoutesInfo> busRoutesInfos = new ArrayList<>();
                        busRoutesInfos.add(busRoutesInfo);
                        BusRoutesInfo.routesAtBusStops.put(busRoutesInfo.busStopCode, busRoutesInfos);
                    } else {
                        BusRoutesInfo.routesAtBusStops.get(busRoutesInfo.busStopCode).add(busRoutesInfo);
                    }
                }
                count = count + 500;
            }
        }

        BusStopInfo.initialise();
    }

    /**
     * Get the bus route of a bus service starting from the first stop
     * @param busServiceInfo the bus service
     * @return an Arraylist containing the bus route
     */
    static ArrayList<BusRoutesInfo> getBusRoutesInfo(BusServiceInfo busServiceInfo) {
        return BusRoutesInfo.routes.get(busServiceInfo);
    }

    /**
     * Get the bus route of a bus service starting from the given bus stop
     * @param start the starting bus stop
     * @param truncateLoop whether to truncate bus route after looping point
     * @return an (ordered) Arraylist containing the bus route
     */
    private static ArrayList<BusRoutesInfo> getBusRoutesInfo(BusRoutesInfo start, boolean truncateLoop) {
        BusServiceInfo busServiceInfo =
                BusServiceInfo.getBusServiceInfo(start.serviceNo, start.direction);
        ArrayList<BusRoutesInfo> fullBusRoutesInfos = BusRoutesInfo.getBusRoutesInfo(busServiceInfo);
        ArrayList<BusRoutesInfo> busRoutesInfos = new ArrayList<>();
        boolean found = false;
        if (!busServiceInfo.isLoop() || !truncateLoop) {
            for (BusRoutesInfo current : fullBusRoutesInfos) {
                if (!found && current == start) {
                    found = true;
                }
                if (found) {
                    busRoutesInfos.add(current.getDifference(start));
                }
            }
        } else {
            HashSet<Integer> busStopCodesVisited = new HashSet<>();
            boolean previousStopWasOppositeVisitedStop = false;
            for (BusRoutesInfo current : fullBusRoutesInfos) {
                if (!found && current == start) {
                    found = true;
                }
                if (found) {
                    busStopCodesVisited.add(current.busStopCode);
                    int oppositeBusStopCode;
                    if (current.busStopCode % 10 == 9) {
                        oppositeBusStopCode = current.busStopCode / 10 * 10 + 1;
                    } else if (current.busStopCode % 10 == 1) {
                        oppositeBusStopCode = current.busStopCode / 10 * 10 + 9;
                    } else {
                        continue;
                    }
                    if (busStopCodesVisited.contains(oppositeBusStopCode)) {
                        if (previousStopWasOppositeVisitedStop) {
                            busRoutesInfos.removeLast();
                            break;
                        } else {
                            previousStopWasOppositeVisitedStop = true;
                        }
                    } else {
                        previousStopWasOppositeVisitedStop = false;
                    }
                    busRoutesInfos.add(current.getDifference(start));
                }
            }
        }
        return busRoutesInfos;
    }

    /**
     * Subtracts a bus stop from this bus stop
     * @param start the starting bus stop
     * @return a new BusRoutesInfo with the stop sequence and distance being the difference between
     * the starting and current bus stop
     */
    private BusRoutesInfo getDifference(BusRoutesInfo start) {
        if (this.serviceNo.equals(start.serviceNo) && this.direction == start.direction) {
            return new BusRoutesInfo(this,
                    this.stopSequence - start.stopSequence,
                    this.distance - start.distance);
        } else {
            return null;
        }
    }

    /**
     * Get all BusRoutesInfo serving this bus stop
     * @param busStopCode the bus stop code
     * @return an ArrayList containing all bus services serving this bus stop
     */
    private static ArrayList<BusRoutesInfo> getBusRoutesInfo(int busStopCode) {
        return BusRoutesInfo.routesAtBusStops.get(busStopCode);
    }

    /**
     * Get the routes of all bus services serving this bus stop, starting from this bus stop
     * @param busStopCode the bus stop code
     * @return a (sorted) TreeMap containing bus services mapped to bus routes
     */
    static TreeMap<BusServiceInfo, ArrayList<BusRoutesInfo>> getFullBusRoutesInfo(int busStopCode) {
        ArrayList<BusRoutesInfo> busRoutesInfos = BusRoutesInfo.getBusRoutesInfo(busStopCode);
        TreeMap<BusServiceInfo, ArrayList<BusRoutesInfo>> routes = new TreeMap<>();
        for (BusRoutesInfo busRoutesInfo : busRoutesInfos) {
            ArrayList<BusRoutesInfo> route = BusRoutesInfo.getBusRoutesInfo(busRoutesInfo, true);
            if (route.size() > 1) { //only include non-terminating services
                BusServiceInfo busServiceInfo =
                        BusServiceInfo.getBusServiceInfo(busRoutesInfo.serviceNo, busRoutesInfo.direction);
                if (routes.containsKey(busServiceInfo)) {
                    //if a loop bus service stops at same bus stop twice
                    routes.get(busServiceInfo).addAll(route);
                } else {
                    routes.put(busServiceInfo, route);
                }
            }
        }
        return routes;
    }

    /**
     * Get all unique bus service numbers serving this bus stop
     * @param busStopCode the bus stop code
     * @return a (sorted) TreeSet containing the bus service numbers
     */
    static TreeSet<String> getBusServicesNo(int busStopCode) {
        ArrayList<BusRoutesInfo> busRoutesInfos = BusRoutesInfo.getBusRoutesInfo(busStopCode);
        TreeSet<String> busServiceInfoStrings = new TreeSet<>();
        for (BusRoutesInfo busRoutesInfo : busRoutesInfos) {
            busServiceInfoStrings.add(busRoutesInfo.serviceNo);
        }
        return busServiceInfoStrings;
    }

    private BusRoutesInfo(BusRoutesInfo original, int stopSequence, double distance) {
        this.serviceNo = original.serviceNo;
        this.direction = original.direction;
        this.distance = distance;
        this.stopSequence = stopSequence;
        this.busStopCode = original.busStopCode;
        this.WD_FirstBus = original.WD_FirstBus;
        this.WD_LastBus = original.WD_LastBus;
        this.SAT_FirstBus = original.SAT_FirstBus;
        this.SAT_LastBus = original.SAT_LastBus;
        this.SUN_FirstBus = original.SUN_FirstBus;
        this.SUN_LastBus = original.SUN_LastBus;
    }

    private BusRoutesInfo(final JSONObject data) {
        this.serviceNo = (String) data.get("ServiceNo");
        this.direction = Math.toIntExact((Long) data.get("Direction"));
        this.stopSequence = Math.toIntExact((Long) data.get("StopSequence"));
        this.busStopCode = Integer.parseInt((String) data.get("BusStopCode"));
        this.distance = data.get("Distance") instanceof Double
                ? (Double) data.get("Distance")
                : (Long) data.get("Distance");
        String wd_FirstBus = (String) data.get("WD_FirstBus");
        this.WD_FirstBus = wd_FirstBus.equals("-") ? null : LocalTime.parse(wd_FirstBus, BusRoutesInfo.format);
        String wd_LastBus = (String) data.get("WD_LastBus");
        this.WD_LastBus = wd_LastBus.equals("-") ? null : LocalTime.parse(wd_LastBus, BusRoutesInfo.format);
        String sat_FirstBus = (String) data.get("SAT_FirstBus");
        this.SAT_FirstBus = sat_FirstBus.equals("-") ? null : LocalTime.parse(sat_FirstBus, BusRoutesInfo.format);
        String sat_LastBus = (String) data.get("SAT_LastBus");
        this.SAT_LastBus = sat_LastBus.equals("-") ? null : LocalTime.parse(sat_LastBus, BusRoutesInfo.format);
        String sun_FirstBus = (String) data.get("SUN_FirstBus");
        this.SUN_FirstBus = sun_FirstBus.equals("-") ? null : LocalTime.parse(sun_FirstBus, BusRoutesInfo.format);
        String sun_LastBus = (String) data.get("SUN_LastBus");
        this.SUN_LastBus = sun_LastBus.equals("-") ? null : LocalTime.parse(sun_LastBus, BusRoutesInfo.format);
    }

    /**
     * Returns a string containing the stop sequence, distance and information about the bus stop of
     * the BusRoutesInfo
     * @return String
     */
    @Override
    public String toString() {
        return String.format("%d\t%.1fkm\t%s",
                this.stopSequence, this.distance, BusStopInfo.getBusStopInfo(this.busStopCode));
    }

    /**
     * Returns a String containing the first and last bus timing of this bus service at this bus stop
     * @return String
     */
    public String getFirstLastBusTimingString() {
        return String.format("Mon to Fri: %s - %s, Sat: %s - %s, Sun: %s - %s", this.WD_FirstBus, this.WD_LastBus,
                this.SAT_FirstBus, this.SAT_LastBus, this.SUN_FirstBus, this.SUN_LastBus);
    }

    /**
     * Checks if this BusRoutesInfo represents the last stop of the bus service
     * @return true if this BusRoutesInfo represents the last stop of the bus service
     */
    public boolean isTerminating() {
        return BusRoutesInfo.routes
                .get(BusServiceInfo.getBusServiceInfo(this.serviceNo, this.direction))
                .getLast() == this;
    }

}
