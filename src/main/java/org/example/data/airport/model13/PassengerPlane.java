package org.example.data.airport.model13;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerPlane extends Plane {
    public PassengerPlane(String brand) {
        super.initProps(brand, PlaneLicense.PASSENGER);
    }

    public static PassengerPlane fromQuery(ResultSet rs) throws SQLException {
        return new PassengerPlane(rs.getString(2));
    }
}
