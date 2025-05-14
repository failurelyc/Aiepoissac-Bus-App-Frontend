/**
 * Each instance of this class holds information of a bus stop
 * @author Li Yi Cheng
 */

package com.aiepoissac.busapp.core.data.busservices;

import com.aiepoissac.busapp.core.util.HasCoordinates;
import com.aiepoissac.busapp.core.util.LatLongPoint;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public final class BusStopInfo implements HasCoordinates {

    private static final HashMap<Integer, BusStopInfo> busStops = new HashMap<>();

    private final int busStopCode;

    private final String roadName;

    private final String description;

    private final LatLongPoint coordinates;

    static void initialise() {
        int count = 0;
        while(true) {
            ArrayList<JSONObject> jsonArray = DataConnection.getData("BUS STOPS", count);
            if (jsonArray.isEmpty()) {
                break;
            } else {
                for (JSONObject jsonObject2 : jsonArray) {
                    new BusStopInfo(jsonObject2);
                }
                count = count + 500;
            }
        }
    }

    static BusStopInfo getBusStopInfo(int busStopCode) {
        return BusStopInfo.busStops.get(busStopCode);
    }

    public static String getBusStopInfoString(int busStopCode, int nearbyStopsDistance, boolean withRoutes) {
        BusStopInfo busStopInfo = BusStopInfo.busStops.get(busStopCode);
        if (busStopInfo != null) {
            return busStopInfo.getBusStopInfoString(nearbyStopsDistance, withRoutes);
        } else {
            return null;
        }
    }

    private BusStopInfo(JSONObject data) {
        this.busStopCode = Integer.parseInt((String) data.get("BusStopCode"));
        this.roadName = (String) data.get("RoadName");
        this.description = (String) data.get("Description");
        this.coordinates = new LatLongPoint((Double) data.get("Latitude"), (Double) data.get("Longitude"));
        BusStopInfo.busStops.put(this.busStopCode, this);
    }

    @Override
    public LatLongPoint getCoordinates() {
        return this.coordinates;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.busStopCode, this.description);
    }

    private String getBusStopInfoString(int nearbyStopsDistance, boolean withRoutes) {
        StringBuilder s = new StringBuilder(this.toString());
        s.append("|");
        s.append(this.roadName);
        s.append(" ");
        s.append(this.getBusServicesInfoString());
        s.append("\n");
        if (nearbyStopsDistance > 0) {
            HashMap<BusStopInfo, Integer> nearbyBusStops = this.getNearbyBusStops(nearbyStopsDistance);
            if (nearbyBusStops.isEmpty()) {
                s.append("No nearby bus stops");
                s.append("\n");
            } else {
                s.append("\nNearby Bus Stops:\n");
                for (BusStopInfo nearbyBusStop : nearbyBusStops.keySet()) {
                    s.append("(");
                    s.append(nearbyBusStops.get(nearbyBusStop));
                    s.append("m) ");
                    s.append(nearbyBusStop.getBusStopInfoString(0, false));
                    s.append("\n");
                }
            }
        }
        if (withRoutes) {
            TreeMap<BusServiceInfo, ArrayList<BusRoutesInfo>> routes =
                    BusRoutesInfo.getFullBusRoutesInfo(this.busStopCode);
            for (BusServiceInfo busServiceInfo : routes.keySet()) {
                s.append("\n");
                s.append(busServiceInfo.toString());
                ArrayList<BusRoutesInfo> busRoutesInfos = routes.get(busServiceInfo);
                s.append("\n");
                for (BusRoutesInfo busRoutesInfo : busRoutesInfos) {
                    s.append(busRoutesInfo);
                    s.append("\n");
                }
                s.append("\n");
            }
        }
        return s.toString();
    }

    String getBusServicesInfoString() {
        return BusRoutesInfo.getBusServicesNo(this.busStopCode).toString();
    }

    private HashMap<BusStopInfo, Integer> getNearbyBusStops(int nearbyStopsDistance) {
        HashMap<BusStopInfo, Integer> nearbyBusStops = new HashMap<>();
        for (BusStopInfo busStopInfo : BusStopInfo.busStops.values()) {
            int distance = this.getDistanceFrom(busStopInfo);
            if (distance <= nearbyStopsDistance && this != busStopInfo) {
                nearbyBusStops.put(busStopInfo, distance);
            }
        }
        return nearbyBusStops;
    }
}
