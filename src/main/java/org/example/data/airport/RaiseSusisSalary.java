package org.example.data.airport;

import org.example.ctrl.Function;
import org.example.data.airport.model13.Employee;

public class RaiseSusisSalary implements Function<Employee> {
    private double perc;

    public  RaiseSusisSalary(double perc) {
        this.perc = perc;
    }
    public  RaiseSusisSalary() {
        this.perc = 1.2;
    }

    @Override
    public Employee apply(Employee o) {
        Employee ret = null;
        if (o.getName().toLowerCase().equals("susi")) {
            o.setSalary((long) (o.getSalary()*perc));
            ret = o;
        }
        return ret;
    }
}
