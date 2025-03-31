package org.example.data.airport.model13;

import org.example.data.airport.frontends.ITermPrintable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Person implements Serializable, ITermPrintable {
    private static long curPID = 0;
    protected long pid;
    private String name;

    public String getName() {
        return name;
    }
    public long getPID() { return pid; }

    public void setName(String name) {
        this.name = name;
    }

    public static boolean bumpPID(long newPID) {
        boolean ret = false;
        if (newPID > curPID) {
            ret = true;
            curPID = newPID;
        }
        return ret;
    }

    protected void initProps(String name) {
        this.name = name;
        this.pid = curPID++;
    }

    public static Person fromQuery(ResultSet rs) throws SQLException {
        System.out.println("Person.fromQuery() init");
        System.out.println("persType: " + rs.getInt("PERSTYPE"));
        return switch (rs.getInt("persType")) {
            case 0 -> Employee.fromQuery(rs);
            case 1 -> Pilot.fromQuery(rs);
            case 2 -> Stewardess.fromQuery(rs);
            case 3 -> Passenger.fromQuery(rs);
            default -> throw new IllegalArgumentException();
        };
    }

    public static int getDiscriminationIndex(Person e) {
        String name = e.getClass().getName();
        return switch (name.substring(name.lastIndexOf('.')+1)) {
            case "Employee" -> 0;
            case "Pilot" -> 1;
            case "Stewardess" -> 2;
            case "Passenger" -> 3;
            default -> throw new IllegalStateException("Unexpected value: " + e.getClass().getName());
        };
    }

    private static String getShortName(Person p) {
        String name = p.getClass().getName();
        return name.substring(name.lastIndexOf('.')+1);
    }
    @Override
    public String toString() {
        return getShortName(this);
    }
}
