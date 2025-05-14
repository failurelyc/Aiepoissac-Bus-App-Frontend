package com.aiepoissac.busapp.core.data.busarrival;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.time.format.DateTimeParseException;

final class BusService {

    private final String serviceNo;

    private final String operator;

    private final Bus[] buses;

    BusService(final JSONObject data) {
        this.serviceNo = (String) data.get("ServiceNo");
        this.operator = (String) data.get("Operator");
        this.buses = new Bus[3];
        try {
            this.buses[0] = new Bus((JSONObject) data.get("NextBus"));
        } catch (DateTimeParseException | NullPointerException e) {
            //there is guaranteed to be at least 1 next bus, for this constructor to be executed
            throw new IllegalArgumentException("Invalid Data");
        }
        try {
            this.buses[1] = new Bus((JSONObject) data.get("NextBus2"));
        } catch (DateTimeParseException | NullPointerException e) {
            this.buses[1] = null;
        }
        try {
            this.buses[2] = new Bus((JSONObject) data.get("NextBus3"));
        } catch (DateTimeParseException | NullPointerException e) {
            this.buses[2] = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Service: ");
        s.append(this.serviceNo);
        s.append("\t Operator: ");
        s.append(this.operator);
        s.append("\t To ");
        s.append(this.buses[0].getDestinationCode());
        s.append("\n");
        for (Bus bus : buses) {
            if (bus != null) {
                s.append(bus);
                s.append("\n");
            }
        }
        return s.toString();
    }

}
