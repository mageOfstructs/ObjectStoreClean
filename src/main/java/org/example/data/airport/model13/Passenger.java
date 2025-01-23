package org.example.data.airport.model13;

import java.io.Serializable;

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
}
