package com.aiepoissac.busapp.core.data.busarrival;

import com.aiepoissac.busapp.core.util.HasCoordinates;
import com.aiepoissac.busapp.core.util.LatLongPoint;
import org.json.simple.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class Bus implements HasCoordinates {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private enum BusType {
        SD("Single Decker"),
        DD("Double Decker"),
        BD("Bendy (Long)");

        private final String s;

        BusType(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private enum LoadType {
        SEA("Seats Available"),
        SDA("Standing Available"),
        LSD("Limited Standing");

        private final String s;

        LoadType(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private final int originCode;

    private final int destinationCode;

    private final LocalDateTime estimatedArrival;

    private final boolean monitored;

    private final LatLongPoint coordinates;

    private final int visitNumber;

    private final LoadType load;

    private final BusType type;

    Bus(final JSONObject data) {
        this.estimatedArrival = LocalDateTime.parse((String) data.get("EstimatedArrival"), Bus.format);
        this.originCode = Integer.parseInt((String) data.get("OriginCode"));
        this.destinationCode = Integer.parseInt((String) data.get("DestinationCode"));
        this.monitored = ((Long) data.get("Monitored")) == 1;
        this.visitNumber = Integer.parseInt((String) data.get("VisitNumber"));
        this.load = LoadType.valueOf((String) data.get("Load"));
        this.type = BusType.valueOf((String) data.get("Type"));
        if (this.monitored) {
            this.coordinates = new LatLongPoint((String) data.get("Latitude"), (String) data.get("Longitude"));
        } else {
            this.coordinates = null;
        }
    }

    public long remainingTime() {
        return Duration.between(LocalDateTime.now(), this.estimatedArrival).toMinutes();
    }

    public int getDestinationCode() {
        return this.destinationCode;
    }

    @Override
    public LatLongPoint getCoordinates() {
        return this.coordinates;
    }

    @Override
    public String toString() {
        if (this.visitNumber == 1) {
            return String.format("%d mins \t %s \t %s", this.remainingTime(), this.load, this.type);
        } else {
            return String.format("%d mins \t %s \t %s (visit no: %d)",
                    this.remainingTime(), this.load, this.type, this.visitNumber);
        }
    }

}
