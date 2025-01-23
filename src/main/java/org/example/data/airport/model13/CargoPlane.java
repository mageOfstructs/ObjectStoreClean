package org.example.data.airport.model13;

public class CargoPlane extends Plane {
    private int weightCapacity;

    public void setWeightCapacity(int weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

    public CargoPlane(String brand, int weightCapacity) {
        super.initProps(brand, PlaneLicense.CARGO);
        this.weightCapacity = weightCapacity;
    }

    public int getWeightCapacity() {
        return weightCapacity;
    }
}
