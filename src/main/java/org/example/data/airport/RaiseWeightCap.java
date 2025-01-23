package org.example.data.airport;

import org.example.ctrl.Function;
import org.example.data.airport.model13.CargoPlane;
import org.example.data.airport.model13.Plane;

public class RaiseWeightCap implements Function<Plane> {
    private double raisePercentage;
    public RaiseWeightCap(double raisePercentage) {
        this.raisePercentage = raisePercentage;
    }
    public RaiseWeightCap() {
        this(1.1);
    }
    @Override
    public Plane apply(Plane o) {
        Plane ret = null;
        if (o instanceof CargoPlane) {
            ((CargoPlane) o).setWeightCapacity((int) (((CargoPlane) o).getWeightCapacity()*raisePercentage));
            ret = o;
        }
        return ret;
    }
}
