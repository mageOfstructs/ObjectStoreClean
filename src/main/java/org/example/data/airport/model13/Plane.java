package org.example.data.airport.model13;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class Plane implements Serializable {
    private long pid;
    private String brand;
    private PlaneLicense license;
    private HashMap<String, Flight> flights = new HashMap<>();

    public static void bumpPID(int i) {
        id = i;
    }

    public long getPid() {
        return pid;
    }

    public String getBrand() {
        return brand;
    }

    public PlaneLicense getLicense() {
        return license;
    }

    private static long id = 0;

    protected void initProps(String brand, PlaneLicense license) {
        this.pid = id++;
        this.brand = brand;
        this.license = license;
    }

    @Override
    public int hashCode() {
        return (int) this.pid;
    }

    public static int getDiscriminationIndex(Plane p) {
        String name = p.getClass().getName();
        return switch (name.substring(name.lastIndexOf('.')+1)) {
            case "CargoPlane" -> 0;
            case "PassengerPlane" -> 1;
            case "MilitaryPlane" -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + p.getClass().getName());
        };
    }

    public static Plane fromQuery(ResultSet rs) throws SQLException {
        return switch (rs.getInt("planeType")) {
            case 0 -> CargoPlane.fromQuery(rs);
            case 1 -> PassengerPlane.fromQuery(rs);
            case 2 -> MilitaryPlane.fromQuery(rs);
            default -> {
                System.err.println(rs.getInt("planeType"));
                throw new IllegalArgumentException();
            }
        };
    }
}
