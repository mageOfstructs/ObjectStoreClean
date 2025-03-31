package org.example.data.airport.model13;

import org.example.data.airport.frontends.ITermPrintable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Passenger extends Person implements Serializable {
    private int bagWeight;
    private long passportNr = super.pid;

    public int getBagWeight() {
        return bagWeight;
    }

    public void setBagWeight(int bagWeight) {
        this.bagWeight = bagWeight;
    }

    public Passenger(String name, int bagWeight) {
        super.initProps(name);
        this.bagWeight = bagWeight;
    }

    public static Passenger fromQuery(ResultSet rs) throws SQLException {
        return new Passenger(rs.getString("name"), rs.getInt("bagWeight"));
    }

    @Override
    public String toPrintable() {
        return ITermPrintable.concat(String.valueOf(passportNr), String.valueOf(bagWeight));
    }
}
