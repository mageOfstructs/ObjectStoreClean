package org.example.data.airport;

import org.example.data.airport.model13.Plane;

import java.util.Comparator;

public class PlaneTypeComparator implements Comparator<Plane> {

    @Override
    public int compare(Plane plane, Plane t1) {
        return plane.getLicense().compareTo(t1.getLicense());
    }
}