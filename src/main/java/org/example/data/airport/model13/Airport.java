package org.example.data.airport.model13;

import java.io.Serializable;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Objects;

public class Airport implements Serializable {
    private String id;
    private String city;
    private HashSet<Hangar> hangars = new HashSet<>();
    private HashMap<String, Flight> flights = new HashMap<>();

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
