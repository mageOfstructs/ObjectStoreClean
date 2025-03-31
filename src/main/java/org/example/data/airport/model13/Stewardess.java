package org.example.data.airport.model13;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Stewardess extends Employee {
    private long passportNr = super.ssnr;

    public Stewardess(String name, int salary) {
        super(name, salary);
    }

    public static Stewardess fromQuery(ResultSet rs) throws SQLException {
        return new Stewardess(rs.getString("name"), rs.getInt("salary"));
    }
}
