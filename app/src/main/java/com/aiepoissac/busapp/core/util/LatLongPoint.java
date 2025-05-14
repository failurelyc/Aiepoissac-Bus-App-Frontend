package com.aiepoissac.busapp.core.util;

public final class LatLongPoint {

    private final double latitude;

    private final double longitude;

    public LatLongPoint(String latitude, String longitude) {
        this(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    public LatLongPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets distance in metres between 2 points with latitude longitude coordinates
     * @param other the other point
     * @return the distance in metres
     */
    public int getDistanceFrom(LatLongPoint other) {
        double distance = 3963 * Math.acos(
                Math.sin(Math.toRadians(this.latitude)) * Math.sin(Math.toRadians(other.latitude)) +
                Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                        Math.cos(Math.toRadians(other.longitude - this.longitude)));
        return Math.toIntExact(Math.round(distance * 1609.344));
    }

}
