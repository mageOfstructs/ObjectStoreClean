package org.example.data.airport.model13;

public class Stewardess extends Employee {
    private long passportNr = super.ssnr;

    public Stewardess(String name, long salary) {
        super(name, salary);
    }
}
