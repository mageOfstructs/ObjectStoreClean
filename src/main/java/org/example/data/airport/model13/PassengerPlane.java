package org.example.data.airport.model13;

public class PassengerPlane extends Plane {
    public PassengerPlane(String brand) {
        super.initProps(brand, PlaneLicense.PASSENGER);
    }
}
