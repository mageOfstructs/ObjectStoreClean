package org.example.data.airport.model13;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

public class Flight implements Serializable {
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private Airport depAirport;
    private Airport arrAirport;
    private HashSet<Stewardess> stewardesses = new HashSet<Stewardess>();
    private HashMap<Long, Passenger> passengers = new HashMap<>();
    private Pilot[] pilots = new Pilot[2];
}
