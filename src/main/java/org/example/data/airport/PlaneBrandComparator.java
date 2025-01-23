package org.example.data.airport;

import org.example.data.airport.model13.Plane;

import java.util.Comparator;

public class PlaneBrandComparator implements Comparator<Plane> {

    @Override
    public int compare(Plane plane, Plane t1) {
        int ret = -1;
        if (plane.getBrand().toLowerCase().contains(t1.getBrand().toLowerCase())) {
            ret = 0;
        }
        return ret;
    }
}
