package org.example.data;

import org.example.ctrl.Function;

public class IncFINby implements Function<Car> {
    private long by = 1;

    public IncFINby() {
        this(1);
    }

    public IncFINby(long by) {
        this.by = by;
    }

    @Override
    public Car apply(Car c) {
        c.setFin(c.getFin()+by);
        return c;
    }
}