package org.example.data.airport.model13;

public class MilitaryPlane extends Plane {
    private String arsenal;

    public MilitaryPlane(String brand, String arsenal) {
        super.initProps(brand, PlaneLicense.MILITARY);
        this.arsenal = arsenal;
    }

    public String getArsenal() {
        return arsenal;
    }
}
