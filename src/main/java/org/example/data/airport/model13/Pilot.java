package org.example.data.airport.model13;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

public class Pilot extends Employee {
    private long passportNr = super.ssnr;
    private HashMap<String, Flight> flights = new HashMap<>();
    private HashSet<PlaneLicense> licenses = new HashSet<>(3);

    public Pilot(String name, int salary) {
        super(name, salary);
    }

    public int getNumberOfFlights() {return flights.size();}

    public boolean addFlight(String name, Flight f) {
        boolean ret = f != null && name != null && !flights.containsKey(name);
        if (ret) {
            flights.put(name, f);
        }
        return ret;
    }

    public static Pilot fromQuery(ResultSet rs) throws SQLException {
        return new Pilot(rs.getString("name"), rs.getInt("salary"));
    }
}
