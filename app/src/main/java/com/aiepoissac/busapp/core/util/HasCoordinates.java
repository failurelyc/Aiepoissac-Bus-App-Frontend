package com.aiepoissac.busapp.core.util;

public interface HasCoordinates {

    LatLongPoint getCoordinates();

    default int getDistanceFrom(HasCoordinates other) {
        return this.getCoordinates().getDistanceFrom(other.getCoordinates());
    }

}
