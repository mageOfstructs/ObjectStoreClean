package org.example.data.airport.model13;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MilitaryPlane extends Plane {
    private String arsenal;

    public MilitaryPlane(String brand, String arsenal) {
        super.initProps(brand, PlaneLicense.MILITARY);
        this.arsenal = arsenal;
    }

    public String getArsenal() {
        return arsenal;
    }

    public static MilitaryPlane fromQuery(ResultSet rs) throws SQLException {
        return new MilitaryPlane(rs.getString(2), rs.getString(5));
    }
}
