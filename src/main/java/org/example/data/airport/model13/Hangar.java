package org.example.data.airport.model13;

import java.io.Serializable;
import java.util.HashSet;

public class Hangar implements Serializable {
    private long id;
    private long planeCapacity;
    private HashSet<Plane> planes = new HashSet<>();

    @Override
    public int hashCode() {
        return (int)id;
    }
}