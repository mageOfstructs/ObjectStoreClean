package org.example.data.airport.model13;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Plane implements Serializable {
    private long pid;
    private String brand;
    private PlaneLicense license;
    private HashMap<String, Flight> flights = new HashMap<>();

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
}
